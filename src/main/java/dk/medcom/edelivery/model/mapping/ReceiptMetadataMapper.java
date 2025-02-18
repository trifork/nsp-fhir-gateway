package dk.medcom.edelivery.model.mapping;

import dk.medcom.edelivery.model.ScopeIdentifiers;
import dk.medcom.edelivery.model.ScopeTypes;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.*;

import javax.xml.bind.JAXB;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.ByteArrayOutputStream;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.UUID;

import static dk.medcom.edelivery.model.ScopeIdentifiers.DK_MEDCOM_MESSAGING;
import static dk.medcom.edelivery.model.ScopeIdentifiers.DK_MESSAGING_DOCID;
import static dk.medcom.edelivery.model.ScopeTypes.MEDCOM_SBDH_RECEIPTACKNOWLEDGEMENT;

public class ReceiptMetadataMapper {

    private static final ObjectFactory factory = new ObjectFactory();
    private static final String FALSE = "false";
    private static final Duration tenMinutes;
    private static final DatatypeFactory datatypeFactory;

    public static final String DI_STANDARD_EBBP_SIGNALS = "ebbp-signals";
    public static final String DI_TYPE_RECEIPT_ACKNOWLEDGEMENT = "ReceiptAcknowledgement";
    public static final String DI_TYPE_RECEIPT_EXCEPTION = "ReceiptException";
    public static final String DI_TYPEVERSION_EBBP_SIGNALS_2_0 = "ebbp-signals-2.0";
    public static final String SBDHRECEIPT_DOCUMENTID = "urn:dk:healthcare:medcom:messaging:ebxml";
    public static final String SBDHRECEIPT_ACKNOWLEDGEMENT = "urn:dk:healthcare:medcom:messaging:ebxml:schema:xsd:SBDHReceiptAcknowledgement";

    static {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
            tenMinutes = datatypeFactory.newDuration(10 * 60 * 1000);
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    private ReceiptMetadataMapper() {
    }

    public static StandardBusinessDocument createReceiptAcknowledgement(StandardBusinessDocument original) {
        return createReceipt(original, factory.createReceiptAcknowledgement(), DI_TYPE_RECEIPT_ACKNOWLEDGEMENT);
    }

    public static StandardBusinessDocument createException(StandardBusinessDocument original, SBDHError error) {
        var exception = factory.createException()
                .withExceptionType(factory.createExceptionExceptionType().withGeneralException(error.type))
                .withExceptionMessage(error.message)
                .withReason(error.reason);

        return createReceipt(original, exception, DI_TYPE_RECEIPT_EXCEPTION);
    }

    private static StandardBusinessDocument createReceipt(StandardBusinessDocument original, SignalIdentificationInformation signal, String documentIdentificationType) { var createTime = datatypeFactory.newXMLGregorianCalendar(GregorianCalendar.from(ZonedDateTime.now()));
        if (original == null || original.getStandardBusinessDocumentHeader() == null) {
            return new StandardBusinessDocument().withBinaryContent(toBinaryContent(signal));
        }
        var adapter = SBDHAdapter.from(original.getStandardBusinessDocumentHeader());
        DocumentIdentification originalDocumentIdentification = original.getStandardBusinessDocumentHeader() != null ? original.getStandardBusinessDocumentHeader().getDocumentIdentification() : null;
        var senderId = adapter.getScope(ScopeTypes.SENDERID).orElseThrow();
        var receiverId = adapter.getScope(ScopeTypes.RECEIVERID).orElseThrow();
        var processScope = adapter.getScope(ScopeTypes.PROCESSID).orElseThrow();

        var standardBusinessDocument = new StandardBusinessDocument()
                .withStandardBusinessDocumentHeader(factory.createStandardBusinessDocumentHeader()
                        .withHeaderVersion("1.0")
                        .withSender(original.getStandardBusinessDocumentHeader().getReceiver())
                        .withReceiver(original.getStandardBusinessDocumentHeader().getSender())
                        .withDocumentIdentification(factory.createDocumentIdentification()
                                .withCreationDateAndTime(createTime)
                                .withInstanceIdentifier(UUID.randomUUID().toString())
                                .withStandard(DI_STANDARD_EBBP_SIGNALS)
                                .withTypeVersion(DI_TYPEVERSION_EBBP_SIGNALS_2_0)
                                .withType(documentIdentificationType))
                        .withBusinessScope(factory.createBusinessScope()
                                .withScope(factory.createScope()
                                        .withType(MEDCOM_SBDH_RECEIPTACKNOWLEDGEMENT)
                                        .withInstanceIdentifier("Response")
                                        .withIdentifier(DK_MEDCOM_MESSAGING)
                                        .withScopeInformation(
                                                factory.createCorrelationInformation(getCorrelationInformation(originalDocumentIdentification)),
                                                factory.createBusinessService(getBusinessService())))
                                .withScope(factory.createScope()
                                        .withType(ScopeTypes.RECEIVERID)
                                        .withIdentifier(senderId.getIdentifier())
                                        .withInstanceIdentifier(senderId.getInstanceIdentifier()))
                                .withScope(factory.createScope()
                                        .withType(ScopeTypes.SENDERID)
                                        .withIdentifier(receiverId.getIdentifier())
                                        .withInstanceIdentifier(receiverId.getInstanceIdentifier()))
                                .withScope(processScope)
                                .withScope(factory.createScope()
                                        .withType(ScopeTypes.DOCUMENTID)
                                        .withIdentifier(SBDHRECEIPT_DOCUMENTID)
                                        .withInstanceIdentifier(SBDHRECEIPT_ACKNOWLEDGEMENT))));

        signal.withOriginalMessageIdentifier(originalDocumentIdentification != null ? originalDocumentIdentification.getInstanceIdentifier() : null)
                .withOriginalDocumentIdentifier(adapter.getInstanceIdentifier(ScopeTypes.DOCUMENTID, DK_MESSAGING_DOCID).orElseThrow())
                .withOriginalMessageDateTime(originalDocumentIdentification != null ? originalDocumentIdentification.getCreationDateAndTime() : null)
                .withThisMessageDateTime(createTime);
        Partner receiver = original.getStandardBusinessDocumentHeader().getReceiver().get(0);
        Partner sender = original.getStandardBusinessDocumentHeader().getSender().get(0);
        signal.withFromPartyInfo(factory.createPartyInfoType()
                        .withType(receiver.getIdentifier().getAuthority())
                        .withValue(receiver.getIdentifier().getValue()))
                .withToPartyInfo(factory.createPartyInfoType()
                        .withType(sender.getIdentifier().getAuthority())
                        .withValue(sender.getIdentifier().getValue()));

        BinaryContentType bct = toBinaryContent(signal);
        StandardBusinessDocument sbd = standardBusinessDocument.withBinaryContent(bct);
        return sbd;
    }

    private static BinaryContentType toBinaryContent(SignalIdentificationInformation signal) {
        var os = new ByteArrayOutputStream();
        JAXB.marshal(signal, os);
        return factory.createBinaryContentType()
                .withMimeType("application/xml")
                .withEncoding("UTF-8")
                .withValue(os.toByteArray());
    }

    private static BusinessService getBusinessService() {
        return factory.createBusinessService()
                .withBusinessServiceName("SBDH_MedCom_ReceiptAcknowledgement_Response")
                .withServiceTransaction(factory.createServiceTransaction()
                        .withTypeOfServiceTransaction(TypeOfServiceTransaction.RESPONDING_SERVICE_TRANSACTION)
                        .withIsNonRepudiationRequired(FALSE)
                        .withIsAuthenticationRequired(FALSE)
                        .withIsNonRepudiationOfReceiptRequired(FALSE)
                        .withIsIntelligibleCheckRequired(FALSE)
                        .withIsApplicationErrorResponseRequested(FALSE)
                        .withTimeToAcknowledgeReceipt("0")
                        .withTimeToAcknowledgeAcceptance("0")
                        .withTimeToPerform("0")
                        .withRecurrence("0"));
    }

    private static CorrelationInformation getCorrelationInformation(DocumentIdentification originalDocumentIdentification) {
        if (originalDocumentIdentification == null) return null;
        var creationDateAndTime = originalDocumentIdentification.getCreationDateAndTime();

        return factory.createCorrelationInformation()
                .withRequestingDocumentInstanceIdentifier(originalDocumentIdentification.getInstanceIdentifier())
                .withRequestingDocumentCreationDateTime(creationDateAndTime);
    }
}
