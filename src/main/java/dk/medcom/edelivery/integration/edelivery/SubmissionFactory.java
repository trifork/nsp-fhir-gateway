package dk.medcom.edelivery.integration.edelivery;

import dk.medcom.edelivery.integration.domibus.model.Payload;
import dk.medcom.edelivery.integration.domibus.model.Submission;
import dk.medcom.edelivery.model.mapping.SBDHAdapter;
import dk.medcom.edelivery.model.mapping.SBDMarshaller;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.GregorianCalendar;
import java.util.UUID;

import static dk.medcom.edelivery.model.MedComConstants.*;
import static dk.medcom.edelivery.model.ScopeTypes.PROCESSID;

public class SubmissionFactory {

    public static final String SERVICE_TYPE = "dk-messaging-procid";
    public static final String SBDH_ACKNOWLEDGEMENT_ACTION = "urn:dk:healthcare:medcom:messaging:ebxml:schema:xsd:sbdhreceiptacknowledgement#urn:dk:healthcare:medcom:messaging:ebxml:schema:xsd:sbdhreceiptacknowledgement:ebbp-signals:ebbp-signals-2.0";
    public static final String SBDH_EXCEPTIONACKNOWLEDGEMENT_ACTION = "urn:dk:healthcare:medcom:messaging:ebxml:schema:xsd:sbdhreceiptacknowledgementexception#urn:dk:healthcare:medcom:messaging:ebxml:schema:xsd:sbdhreceiptacknowledgementexception:ebbp-signals:ebbp-signals-2.0";

    private SubmissionFactory() {
    }

    public static Submission submission(GatewayConfigurationProperties properties, String fromParty, String toParty) {
        return submission(properties, fromParty, toParty, null);
    }

    public static Submission submission(GatewayConfigurationProperties properties, String fromParty, String toParty, String action) {
        Submission submission = new Submission()
                .setMessageId(UUID.randomUUID().toString())
                .setFromRole("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/initiator")
                .setToRole("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/responder")
                .setAgreementRef(properties.getAgreementRef())
                .setServiceType(SERVICE_TYPE);
        if (action != null) {
            submission.setAction(action);
        }
        if (fromParty != null) {
            submission.addFromParty(fromParty, properties.getPartyIdType());
        }
        if (toParty != null) {
            submission.addToParty(toParty, properties.getPartyIdType());
        }
        return submission;
    }

    public static Submission createResponse(Submission request, Payload payload, GatewayConfigurationProperties properties, String action) {

        Submission submission = new Submission()
                .setMessageId(UUID.randomUUID().toString())
                .addFromParty(properties.getName(), properties.getPartyIdType())
                .setAgreementRef(properties.getAgreementRef())
                .setFromRole(request.getFromRole())
                .setToRole(request.getToRole())
                .setAction(action)
                .setService(request.getService())
                .setServiceType(request.getServiceType())
                .setOriginalSender(properties.getOriginalSender(), properties.getPartyType())
                .setFinalRecipient(request.getOriginalSender().getPartyId(), properties.getPartyType())
                .addPayload(payload);
        return submission;
    }

    public static Submission createDomainForwardedSubmission(Submission original, StandardBusinessDocument standardBusinessDocument, GatewayConfigurationProperties gatewayProperties) {
        var submission = createForwardedSubmission(original, gatewayProperties);

        updateStandardBusinessDocumentForDomainForwarding(standardBusinessDocument);
        submission.getPayloads().clear();
        submission.getPayloads().add(createForwardedPayload(original.getPayloads().stream().findFirst().orElseThrow(), standardBusinessDocument));

        submission.setService(mapDomainService(original.getService()));
        return submission;
    }

    public static Submission createCrossDomainForwardedSubmission(Submission original, StandardBusinessDocument standardBusinessDocument, GatewayConfigurationProperties gatewayProperties) {
        var submission = createForwardedSubmission(original, gatewayProperties);

        updateStandardBusinessDocumentForCrossDomainForwarding(standardBusinessDocument);
        submission.getPayloads().clear();
        submission.getPayloads().add(createForwardedPayload(original.getPayloads().stream().findFirst().orElseThrow(), standardBusinessDocument));

        submission.setService(mapCrossDomainService(original.getService()));
        return submission;
    }

    public static Submission createForwardedSubmission(Submission original, GatewayConfigurationProperties properties) {
        var submission = submission(properties, properties.getName(), null)
                .setFromRole(original.getFromRole())
                .setToRole(original.getToRole())
                .setAction(original.getAction())
                .setService(original.getService())
                .setServiceType(original.getServiceType())
                .setOriginalSender(original.getOriginalSender().getPartyId(), properties.getPartyType())
                .setFinalRecipient(original.getFinalRecipient().getPartyId(), properties.getPartyType());

        submission.getPayloads().addAll(original.getPayloads());

        return submission;
    }

    private static Payload createForwardedPayload(Payload payload, StandardBusinessDocument sbd) {
        return new Payload(
                payload.getContentId(),
                SBDMarshaller.marshal(sbd),
                payload.getPayloadProperties(),
                payload.isInBody(),
                payload.getDescription(),
                payload.getSchemaLocation());
    }

    private static void updateStandardBusinessDocumentForDomainForwarding(StandardBusinessDocument document) {
        // New Documentation/InstanceIdentifier, Documentation/CreationDateAndTime, and BusinessScope ProcessId/Service mapping
        var adapter = SBDHAdapter.from(document.getStandardBusinessDocumentHeader());
        resetCreationDateTime(document);
        resetDocumentId(document);
        adapter.getScope(PROCESSID).ifPresent(scope -> scope.setInstanceIdentifier(mapDomainService(scope.getInstanceIdentifier())));
    }

    private static void updateStandardBusinessDocumentForCrossDomainForwarding(StandardBusinessDocument document) {
        // New Documentation/InstanceIdentifier, Documentation/CreationDateAndTime, and BusinessScope ProcessId/Service mapping
        var adapter = SBDHAdapter.from(document.getStandardBusinessDocumentHeader());
        if (document.getStandardBusinessDocumentHeader() != null) {
            resetCreationDateTime(document);
            resetDocumentId(document);
            adapter.getScope(PROCESSID).ifPresent(scope -> scope.setInstanceIdentifier(mapCrossDomainService(scope.getInstanceIdentifier())));
        }
    }

    private static void resetDocumentId(StandardBusinessDocument document) {
        document.getStandardBusinessDocumentHeader().getDocumentIdentification().setInstanceIdentifier(UUID.randomUUID().toString());
    }

    private static void resetCreationDateTime(StandardBusinessDocument document) {
        try {
            document.getStandardBusinessDocumentHeader().getDocumentIdentification().setCreationDateAndTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    static String mapDomainService(String service) {
        if (EMERGENCE_SDN.equals(service)) {
            return DISTRIBUTION_SDN;
        } else if (EMERGENCE_FOD.equals(service)) {
            return DISTRIBUTION_FOD;
        } else {
            return service;
        }
    }

    // Emergence: standard forwarding between domains
    // Distribution: when a domain 3-corners forwards to the X-domain-gateway
    static String mapCrossDomainService(String service) {
        if (EMERGENCE_SDN.equals(service)) {
            return EMERGENCE_FOD;
        } else if (EMERGENCE_FOD.equals(service)) {
            return EMERGENCE_SDN;
        } else if (DISTRIBUTION_SDN.equals(service)) {
            return EMERGENCE_FOD;
        } else if (DISTRIBUTION_FOD.equals(service)) {
            return EMERGENCE_SDN;
        } else {
            return service;
        }
    }

}
