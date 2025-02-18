package dk.medcom.edelivery;

import dk.medcom.edelivery.integration.domibus.model.Payload;
import dk.medcom.edelivery.integration.domibus.model.Submission;
import dk.medcom.edelivery.integration.edelivery.GatewayConfigurationProperties;
import dk.medcom.edelivery.integration.edelivery.SubmissionFactory;
import dk.medcom.edelivery.integration.tracking.TrackingService;
import dk.medcom.edelivery.jms.Destinations;
import dk.medcom.edelivery.jms.SubmissionReceiver;
import dk.medcom.edelivery.jms.SubmissionSender;
import dk.medcom.edelivery.model.ScopeTypes;
import dk.medcom.edelivery.model.mapping.SBDHAdapter;
import dk.medcom.edelivery.model.mapping.SBDUnmarshaller;
import dk.medcom.edelivery.service.ForwardingService;
import dk.medcom.edelivery.util.PayloadUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import java.util.UUID;

import static dk.medcom.edelivery.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@UseTestContainers
class GatewayReceiptIT {

    @MockBean
    SubmissionSender submissionSender;

    @MockBean
    ForwardingService forwardingService; // not interesting for this test

    @MockBean
    TrackingService trackingService; // not interesting for this test

    @Autowired
    SubmissionReceiver submissionReceiver;

    @Autowired
    Destinations destinations;

    @Autowired
    GatewayConfigurationProperties properties;

    @Test
    void verifyReceipt() {
        reset(submissionSender);
        Submission submission = getSubmission(TestConstants.SDN_EMERGENCE, "test-messages/new-example.xml");
        submission.setService(TestConstants.SDN_EMERGENCE);
        submissionReceiver.receiveSubmission(submission);

        ArgumentCaptor<Submission> argumentCaptor = ArgumentCaptor.forClass(Submission.class);
        verify(submissionSender, times(1)).send(argumentCaptor.capture(), any());

        Submission receiptSubmission = argumentCaptor.getValue();
        StandardBusinessDocumentHeader fullOriginalSBDH = getStandardBusinessDocument(submission).getStandardBusinessDocumentHeader();
        StandardBusinessDocumentHeader fullReceiptSBDH = getStandardBusinessDocument(receiptSubmission).getStandardBusinessDocumentHeader();
        SBDHAdapter receiptsSDBH = SBDHAdapter.from(fullReceiptSBDH);

        // Check submission
        assertEquals(submission.getAction(), receiptSubmission.getAction());
        assertNotNull(receiptSubmission.getMessageId());
        assertNotEquals(submission.getMessageId(), receiptSubmission.getMessageId());
        assertEquals(properties.getOriginalSender(), receiptSubmission.getOriginalSender().getPartyId());
        assertEquals("domibus-gateway", receiptSubmission.getFromParties().stream().findFirst().get().getPartyId());
        assertEquals(submission.getAgreementRef(), receiptSubmission.getAgreementRef());
        assertEquals(submission.getAgreementRefType(), receiptSubmission.getAgreementRefType());
        assertEquals(submission.getConversationId(), receiptSubmission.getConversationId());
        assertEquals(submission.getToRole(), receiptSubmission.getFromRole());
        assertEquals(submission.getFromRole(), receiptSubmission.getToRole());

        assertEquals("ReceiptAcknowledgement", fullReceiptSBDH.getDocumentIdentification().getType());
        assertEquals("ebbp-signals-2.0", fullReceiptSBDH.getDocumentIdentification().getTypeVersion());
        assertEquals("ebbp-signals", fullReceiptSBDH.getDocumentIdentification().getStandard());

        //InputStream inputStream = receiptSubmission.getPayloads().stream().findFirst().get().getPayloadDatahandler().getInputStream();
        //String text = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));

        // check SBDH
        assertNotEquals(fullOriginalSBDH.getDocumentIdentification().getCreationDateAndTime(), fullReceiptSBDH.getDocumentIdentification().getCreationDateAndTime());
        assertNotEquals(fullOriginalSBDH.getDocumentIdentification().getInstanceIdentifier(), fullReceiptSBDH.getDocumentIdentification().getInstanceIdentifier());
        assertEquals(fullOriginalSBDH.getDocumentIdentification().getInstanceIdentifier(), receiptsSDBH.getScope("OriginalMessageIdentifier").get().getInstanceIdentifier());
        assertEquals("Response", receiptsSDBH.getScope(ScopeTypes.MEDCOM_SBDH_RECEIPTACKNOWLEDGEMENT).get().getInstanceIdentifier());
        assertEquals("dk-medcom-messaging", receiptsSDBH.getScope(ScopeTypes.MEDCOM_SBDH_RECEIPTACKNOWLEDGEMENT).get().getIdentifier());
    }

    @Test
    void verifyNoReceipt() {
        reset(submissionSender);
        Submission submission = getSubmission(TestConstants.SDN_EMERGENCE,"test-messages/receipt.xml");
        submission.setService(TestConstants.SDN_EMERGENCE);
        submissionReceiver.receiveSubmission(submission);

        ArgumentCaptor<Submission> argumentCaptor = ArgumentCaptor.forClass(Submission.class);
        verify(submissionSender, times(0)).send(argumentCaptor.capture(), any());
    }

    private StandardBusinessDocument getStandardBusinessDocument(Submission submission) {
        Payload payload = submission.getPayloads().stream().findFirst().orElseThrow();
        return SBDUnmarshaller.unmarshal(payload.getPayloadDatahandler());
    }

    private Submission getSubmission(String service, String fileName) {
        Payload xmlContent = PayloadUtil.getXmlContent(fileName);
        return SubmissionFactory.submission(properties, AP_EDELIVERY1, AP_EDELIVERY1, TEST_ACTION)
                .setService(service)
                .setOriginalSender(AP_EDELIVERY1, "originalSenderType")
                .setFinalRecipient(AP_EDELIVERY1, null)
                .addPayload(xmlContent);
    }

}
