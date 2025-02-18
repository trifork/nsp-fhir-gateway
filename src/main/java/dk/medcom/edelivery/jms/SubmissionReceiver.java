package dk.medcom.edelivery.jms;

import dk.medcom.edelivery.integration.domibus.model.Payload;
import dk.medcom.edelivery.integration.domibus.model.Submission;
import dk.medcom.edelivery.integration.edelivery.GatewayConfigurationProperties;
import dk.medcom.edelivery.integration.edelivery.ReceiptService;
import dk.medcom.edelivery.integration.tracking.TrackingService;
import dk.medcom.edelivery.integration.tracking.TrackingStatus;
import dk.medcom.edelivery.integration.tracking.TrackingStatusMapper;
import dk.medcom.edelivery.model.ScopeTypes;
import dk.medcom.edelivery.model.mapping.*;
import dk.medcom.edelivery.service.ForwardingService;
import dk.medcom.edelivery.service.RepositoryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXB;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.Exception;
import java.util.*;

import static dk.medcom.edelivery.integration.edelivery.SubmissionFactory.SBDH_ACKNOWLEDGEMENT_ACTION;
import static dk.medcom.edelivery.integration.edelivery.SubmissionFactory.SBDH_EXCEPTIONACKNOWLEDGEMENT_ACTION;
import static dk.medcom.edelivery.model.MedComConstants.*;

@Component
public class SubmissionReceiver {

    private static final Logger log = LogManager.getLogger(SubmissionReceiver.class);

    private final TrackingService trackingService;
    private final ReceiptService receiptService;
    private final RepositoryService repositoryService;
    private final ForwardingService forwardingService;
    private final GatewayConfigurationProperties properties;

    public SubmissionReceiver(TrackingService trackingService, ReceiptService receiptService, RepositoryService repositoryService,
                              ForwardingService forwardingService, GatewayConfigurationProperties properties) {
        this.trackingService = trackingService;
        this.receiptService = receiptService;
        this.repositoryService = repositoryService;
        this.forwardingService = forwardingService;
        this.properties = properties;
    }

    @JmsListener(destination = Destinations.DOMIBUS_OUT_QUEUE_EXP, containerFactory = JmsConfiguration.SUBMISSION_LISTENER_CONTAINER_FACTORY)
    @Transactional(timeout=180)
    public void receiveSubmission(Submission submission) {
        StandardBusinessDocument document = null;
        try {
            log.info("Received submission with messageId='{}'", submission.getMessageId());
            log.debug(submission);

            document = getStandardBusinessDocument(submission);
            handleSubmission(submission, document);
        } catch (SBDHIntegrityException e) {
            log.warn("Data integrity issue. " + e.getMessage(), e);
            receiptService.sendReceipt(submission, ReceiptMetadataMapper.createException(document, SBDHError.SBDH_INTEGRITY.withMessage(e.getMessage())), SBDH_EXCEPTIONACKNOWLEDGEMENT_ACTION);
        } catch (Exception e) {
            log.error(String.format("Error handling submission with MessageId '%s'", submission.getMessageId()), e);
            receiptService.sendReceipt(submission, ReceiptMetadataMapper.createException(document, SBDHError.INTERNAL_ERROR.withMessage(e.getMessage())), SBDH_EXCEPTIONACKNOWLEDGEMENT_ACTION);
        }
    }

    // check that fields both in submission and SBDH are consistent
    private void validateConsistence(Submission submission, StandardBusinessDocument document) {
        if (document == null || document.getStandardBusinessDocumentHeader() == null) return;
        String subService = submission.getService();
        String subRecipientPartyId = submission.getFinalRecipient().getPartyId();
        String subRecipientPartyIdType = submission.getFinalRecipient().getPartyIdType();

        var adapter = SBDHAdapter.from(document.getStandardBusinessDocumentHeader());
        Optional<Scope> processId = adapter.getScope(ScopeTypes.PROCESSID);
        String sbdService = processId.isPresent() ? processId.get().getInstanceIdentifier() : null;
        String sbdReceiverId = document.getStandardBusinessDocumentHeader().getReceiver().get(0).getIdentifier().getValue();
        String sbdReceiverAuthority = document.getStandardBusinessDocumentHeader().getReceiver().get(0).getIdentifier().getAuthority();

        verifyMatch("service/process id", subService, sbdService);
        // TODO enable constraints when playtime is over
        // verifyMatch("receiver identifier", subRecipientPartyId, sbdReceiverId);
        // verifyMatch("receiver authority", subRecipientPartyIdType, sbdReceiverAuthority); type seems to be unmapped. Is null even though it is set in submission
    }

    private void verifyMatch(String field, String subscriptionData, String sbdhData) {
        boolean match = Objects.equals(subscriptionData, sbdhData);
        if (!match) {
            // TODO: for now, just log a warning about the inconsistency. But return exception when the POC phase is over
            log.warn("SBDH data integrity violation (" + field + "): SBDH '" + sbdhData + "' does not match '" + subscriptionData + "'");
            // throw new SBDHIntegrityException("SBDH data integrity violation (" + field + "): SBDH '" + sbdhData + "' does not match '" + subscriptionData + "'");
        }
    }

    private void handleSubmission(Submission submission, StandardBusinessDocument document) {
        logSBDH(document);
        boolean isReceipt = isReceipt(document);
        boolean isException = isException(document);

        log.info("isReceipt = " + isReceipt);
        log.info("isException = " + isException);

        if (!isReceipt) {
            track(document);
        }

        validateConsistence(submission, document);

        // Disable forwarding for now
        /* if (shouldForward(submission, document, submission.getService())) {
            log.info("shouldForward = true");
            forwardingService.forwardSubmission(submission, document);
        } else {
            log.info("shouldForward = false");
        }*/

        if (shouldPersist(submission, document)) {
            log.info("shouldPersist = true");
            repositoryService.persist(document);
        } else {
            log.info("shouldPersist = false");
        }

        if (!isReceipt && !isException)
            receiptService.sendReceipt(submission, ReceiptMetadataMapper.createReceiptAcknowledgement(document), SBDH_ACKNOWLEDGEMENT_ACTION);
    }

    private void logSBDH(StandardBusinessDocument document) {
        StringWriter sw = new StringWriter();
        if (document == null || document.getStandardBusinessDocumentHeader() == null) return;
        StandardBusinessDocumentHeader sbdh = document.getStandardBusinessDocumentHeader();
        if (sbdh.getBusinessScope() == null || sbdh.getBusinessScope().getScope() == null) return;

        String senderStr = "";
        List<Partner> sender = sbdh.getSender();
        if (sender != null && sender.size() > 0 && sender.get(0).getIdentifier() != null) {
            PartnerIdentification id = sender.get(0).getIdentifier();
            senderStr = "Authority=" + id.getAuthority() + ", Value=" + id.getValue();
        }

        String receiverStr = "";
        List<Partner> receiver = sbdh.getReceiver();
        if (receiver != null && receiver.size() > 0 && receiver.get(0).getIdentifier() != null) {
            PartnerIdentification id = receiver.get(0).getIdentifier();
            receiverStr = "Authority=" + id.getAuthority() + ", Value=" + id.getValue();
        }

        String documentId = "";
        DocumentIdentification di = sbdh.getDocumentIdentification();
        if (di != null) {
            documentId += "(" +
              "Standard=" + di.getStandard() +
              ", TypeVersion=" + di.getTypeVersion() +
              ", InstanceIdentifier=" + di.getInstanceIdentifier() +
              ", Type=" + di.getType() +
              ", MultipleType=" + di.isMultipleType() +
              ", CreationDateAndTime=" + di.getCreationDateAndTime() +
              ", Standard=" + di.getStandard() + ")";
        }
        List<Scope> scope = sbdh.getBusinessScope().getScope();
        var businessScope = new ArrayList<String>();
        scope.stream()
                .filter(bs -> bs.getType() != null)
                .forEach(bs -> businessScope.add(bs.getType() + "|" + bs.getIdentifier() + "|" + bs.getInstanceIdentifier()));
        sw.append("SBDH summary: Sender=(" + senderStr + "), Receiver=(" + receiverStr + "), DocumentIdentification=(" + documentId + "), BusinessScope=(" + businessScope + ")");
        log.debug(sw);
    }

    private boolean isReceipt(StandardBusinessDocument document) {
        if (document == null || document.getStandardBusinessDocumentHeader() == null) return false;
        DocumentIdentification di = document.getStandardBusinessDocumentHeader().getDocumentIdentification();
        boolean isReceipt = ReceiptMetadataMapper.DI_STANDARD_EBBP_SIGNALS.equals(di.getStandard()) &&
                ReceiptMetadataMapper.DI_TYPE_RECEIPT_ACKNOWLEDGEMENT.equals(di.getType()) &&
                ReceiptMetadataMapper.DI_TYPEVERSION_EBBP_SIGNALS_2_0.equals(di.getTypeVersion());
        return isReceipt;
    }

    private <T> T unmarshal(BinaryContentType bct, Class<T> clazz) {
        return JAXB.unmarshal(new ByteArrayInputStream(bct.getValue()), clazz);
    }

    private void checkIfXmlIsParseable(List<BinaryContentType> binaryContentList) throws ParserConfigurationException, IOException, SAXException {
        if (binaryContentList == null || binaryContentList.size() == 0) return;
        BinaryContentType binaryContent = binaryContentList.get(0);
        InputSource inputSource = new InputSource(new StringReader(new String(binaryContent.getValue())));
        inputSource.setEncoding(binaryContent.getEncoding());
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        dBuilder.parse(inputSource);
    }

    private boolean isException(StandardBusinessDocument document) {
        if (document == null || document.getBinaryContent() == null || document.getBinaryContent().size() == 0) return false;
        try {
            org.unece.cefact.namespaces.standardbusinessdocumentheader.Exception exception = unmarshal(document.getBinaryContent().get(0), org.unece.cefact.namespaces.standardbusinessdocumentheader.Exception.class);
            return exception != null && exception.getExceptionType() != null;
        } catch (Exception e) {
            log.info(e);
            return false;
        }
    }

    private void track(StandardBusinessDocument document) {
        log.info("Logging to KMD track'n'trace...");
        try {
            TrackingStatus trackingStatus = TrackingStatusMapper.toTrackingStatus(document.getStandardBusinessDocumentHeader());
            trackingService.logTrackingStatus(trackingStatus);
        } catch (Exception e) {
            // Continue processing even though track'n'trace logging fails
            log.error("Unable to log track'n'trace", e);
        }
    }

    private StandardBusinessDocument getStandardBusinessDocument(Submission submission) {
        log.debug("Payloads before filtering: " + submission.getPayloads().size());
        Payload payload = submission.getPayloads().stream()
                .filter(p -> p.getContentId().equals("cid:message"))
                .findFirst()
                .orElseThrow();
        log.debug("Payloads after filtering: " + submission.getPayloads().size());

        StandardBusinessDocument unmarshalled = SBDUnmarshaller.unmarshal(payload.getPayloadDatahandler());
        try {
            checkIfXmlIsParseable(unmarshalled.getBinaryContent());
        } catch (Exception e) {
            throw new SBDHIntegrityException("Unable to parse SBDH BinaryContent: " + e.getMessage());
        }
        return unmarshalled;
    }

    private boolean shouldForward(Submission submission, StandardBusinessDocument document, String p) {
        if (isForDDS(submission)) {
            return false;
        }
        return isException(document) ||
                EMERGENCE_SDN.equals(p)
                || EMERGENCE_FOD.equals(p)
                || DISTRIBUTION_FOD.equals(p)
                || DISTRIBUTION_SDN.equals(p);
    }

    private boolean shouldPersist(Submission submission, StandardBusinessDocument document) {
        if (document == null || document.getStandardBusinessDocumentHeader() == null) return false;
        // Check if metadata creation is ok
        SBDHMetadataMapper.toMetadata(document.getStandardBusinessDocumentHeader());
        return isForDDS(submission);
    }

    private boolean isForDDS(Submission submission) {
        return true; // For now, send everything to DDS
        // boolean ddsMatch = properties.getDdsRecipient().equals(submission.getFinalRecipient().getPartyId());
        // return ddsMatch;
    }

}
