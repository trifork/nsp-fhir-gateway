package dk.medcom.edelivery;

import dk.medcom.edelivery.integration.domibus.model.Submission;
import dk.medcom.edelivery.integration.edelivery.GatewayConfigurationProperties;
import dk.medcom.edelivery.integration.edelivery.ReceiptService;
import dk.medcom.edelivery.integration.edelivery.SubmissionFactory;
import dk.medcom.edelivery.integration.tracking.TrackingService;
import dk.medcom.edelivery.integration.tracking.TrackingStatus;
import dk.medcom.edelivery.jms.SubmissionReceiver;
import dk.medcom.edelivery.service.ForwardingService;
import dk.medcom.edelivery.service.RepositoryService;
import dk.medcom.edelivery.testing.DomibusWSService;
import dk.medcom.edelivery.util.PayloadUtil;
import org.awaitility.Awaitility;
import org.awaitility.core.ThrowingRunnable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.BinaryContentType;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.ReceiptAcknowledgement;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import javax.xml.bind.JAXB;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;

import static dk.medcom.edelivery.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Launches end-to-end test based on TestContainer setup (see TestContainers.java)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@UseTestContainers
@ActiveProfiles({"demo"})
class EndToEndIT_SMPOnly {

    public static final String PARTY_AP1 = "ap1";
    public static final String FINAL_SENDER_AP1 = "0088:ap1";
    public static final String FINAL_RECIPIENT_AP2 = "0088:ap2";
    public static final String PARTY_DOMIBUS_GATEWAY = "domibus-gateway";

    @Autowired
    GatewayConfigurationProperties properties;

    @SpyBean
    private SubmissionReceiver submissionReceiver;

    @SpyBean
    private TrackingService trackingService;

    @SpyBean
    private ForwardingService forwardingService;

    @SpyBean
    private RepositoryService repositoryService;

    @SpyBean
    private ReceiptService receiptService;

    private DomibusWSService serviceAP1;
    private DomibusWSService serviceAP2;

    @BeforeEach
    void setUp() {
        serviceAP1 = getService(TestContainers.AP_1_DOMIBUS);
        serviceAP2 = getService(TestContainers.AP_2_DOMIBUS);

        clearPendingMessages(serviceAP1);
        clearPendingMessages(serviceAP2);
    }

    private void clearPendingMessages(DomibusWSService service) {
        service.fetchPendingMessageIds().stream()
                .peek(id -> System.out.println("Fetching pending message " + id))
                .forEach(service::getMessage);
    }

    @Test
    void ap1_sends_message_to_dds() throws Exception {
        var sub = SubmissionFactory.submission(properties, PARTY_AP1, null, TEST_ACTION)
                .setOriginalSender(FINAL_SENDER_AP1, null)
                .setFinalRecipient(DDS_RECIPIENT_ID, ISO6523_ACTORID_UPIS)
                .addPayload(PayloadUtil.getXmlContent("test-messages/new-example.xml"));

        TestDataHelper.setService(sub, SDN_EMERGENCE);

        serviceAP1.sendSubmitRequest(sub);

        var receivedSubmission = verifyAndGetReceivedSubmission();

        assertEquals(sub.getMessageId(), receivedSubmission.getMessageId());

        await(() -> verify(trackingService).logTrackingStatus(any(TrackingStatus.class)));
        await(() -> verify(repositoryService).persist(any(StandardBusinessDocument.class)));
        await(() -> verify(receiptService).sendReceipt(any(Submission.class), any(StandardBusinessDocument.class), any()));

        DomibusWSService.MessageResponse receipt = getFirstMessage(serviceAP1);

        assertNotNull(receipt);
        StandardBusinessDocument receiptSBD = unmarshal(receipt.getRetrieveMessageResponse());

        BinaryContentType bct = receiptSBD.getBinaryContent().get(0);
        ReceiptAcknowledgement ack = unmarshal(bct, ReceiptAcknowledgement.class);

        assertEquals("iso6523-actorid-upis", ack.getToPartyInfo().getType());
        assertEquals("0088:5790000121526", ack.getToPartyInfo().getValue());
        assertEquals("iso6523-actorid-upis", ack.getFromPartyInfo().getType());
        assertEquals("0088:5790000201389", ack.getFromPartyInfo().getValue());

        verifyNoInteractions(forwardingService);
    }

    @Test
    void ap1_gets_exception_for_invalid_sbdh() throws Exception {
        var sub = SubmissionFactory.submission(properties, PARTY_AP1, PARTY_DOMIBUS_GATEWAY, TEST_ACTION)
                .setService(SDN_EMERGENCE)
                .setOriginalSender(FINAL_SENDER_AP1, null)
                .setFinalRecipient(DDS_RECIPIENT_ID, ISO6523_ACTORID_UPIS)
                .addPayload(PayloadUtil.getXmlContent("test-messages/invalid-sbdh.xml"));

        serviceAP1.sendSubmitRequest(sub);

        var receivedSubmission = verifyAndGetReceivedSubmission();

        assertEquals(sub.getMessageId(), receivedSubmission.getMessageId());

        await(() -> verify(receiptService).sendReceipt(any(Submission.class), any(StandardBusinessDocument.class), any()));

        DomibusWSService.MessageResponse exceptionAnswer = getFirstMessage(serviceAP1);

        assertNotNull(exceptionAnswer);
        RetrieveMessageResponse retrieveMessageResponse = exceptionAnswer.getRetrieveMessageResponse();

        StandardBusinessDocument sbd = unmarshal(retrieveMessageResponse);
        org.unece.cefact.namespaces.standardbusinessdocumentheader.Exception exception = unmarshal(sbd.getBinaryContent().get(0), org.unece.cefact.namespaces.standardbusinessdocumentheader.Exception.class);

        assertEquals("Internal error", exception.getReason());
        assertEquals("101", exception.getExceptionType().getGeneralException());

        verifyNoInteractions(repositoryService);
        verifyNoInteractions(forwardingService);
    }

    @Test
    void ap1_gets_exception_for_invalid_sbdh_content() throws Exception {
        var sub = SubmissionFactory.submission(properties, PARTY_AP1, PARTY_DOMIBUS_GATEWAY, TEST_ACTION)
                .setService(SDN_EMERGENCE)
                .setOriginalSender(FINAL_SENDER_AP1, null)
                .setFinalRecipient(DDS_RECIPIENT_ID, ISO6523_ACTORID_UPIS)
                .addPayload(PayloadUtil.getXmlContent("test-messages/sbdh-with-missing-uuid.xml"));

        serviceAP1.sendSubmitRequest(sub);

        var receivedSubmission = verifyAndGetReceivedSubmission();

        assertEquals(sub.getMessageId(), receivedSubmission.getMessageId());

        await(() -> verify(receiptService).sendReceipt(any(Submission.class), any(StandardBusinessDocument.class), any()));

        DomibusWSService.MessageResponse exceptionAnswer = getFirstMessage(serviceAP1);

        assertNotNull(exceptionAnswer);
        RetrieveMessageResponse retrieveMessageResponse = exceptionAnswer.getRetrieveMessageResponse();

        StandardBusinessDocument sbd = unmarshal(retrieveMessageResponse);
        org.unece.cefact.namespaces.standardbusinessdocumentheader.Exception exception = unmarshal(sbd.getBinaryContent().get(0), org.unece.cefact.namespaces.standardbusinessdocumentheader.Exception.class);

        assertEquals("ReceiptException", sbd.getStandardBusinessDocumentHeader().getDocumentIdentification().getType());
        assertEquals("SBDH integrity error", exception.getReason());
        assertEquals("102", exception.getExceptionType().getGeneralException());
        assertEquals("Missing scope type 'ENTRYUUID' with identifier 'dk-dds-metadata'", exception.getExceptionMessage());

        verifyNoInteractions(repositoryService);
        verifyNoInteractions(forwardingService);
    }

    @Test
    void ap1_gets_exception_for_non_wellformed_sbdh_content() throws Exception {
        var sub = SubmissionFactory.submission(properties, PARTY_AP1, PARTY_DOMIBUS_GATEWAY, TEST_ACTION)
                .setService(SDN_EMERGENCE)
                .setOriginalSender(FINAL_SENDER_AP1, null)
                .setFinalRecipient(DDS_RECIPIENT_ID, ISO6523_ACTORID_UPIS)
                .addPayload(PayloadUtil.getXmlContent("test-messages/sbdh-with-non-wellformed-binarycontents.xml"));

        serviceAP1.sendSubmitRequest(sub);

        var receivedSubmission = verifyAndGetReceivedSubmission();

        assertEquals(sub.getMessageId(), receivedSubmission.getMessageId());

        await(() -> verify(receiptService).sendReceipt(any(Submission.class), any(StandardBusinessDocument.class), any()));

        DomibusWSService.MessageResponse exceptionAnswer = getFirstMessage(serviceAP1);

        assertNotNull(exceptionAnswer);
        RetrieveMessageResponse retrieveMessageResponse = exceptionAnswer.getRetrieveMessageResponse();

        StandardBusinessDocument sbd = unmarshal(retrieveMessageResponse);
        org.unece.cefact.namespaces.standardbusinessdocumentheader.Exception exception = unmarshal(sbd.getBinaryContent().get(0), org.unece.cefact.namespaces.standardbusinessdocumentheader.Exception.class);

        assertEquals("SBDH integrity error", exception.getReason());
        assertEquals("102", exception.getExceptionType().getGeneralException());
        assertEquals("Unable to parse SBDH BinaryContent: The element type \"Bundle\" must be terminated by the matching end-tag \"</Bundle>\".", exception.getExceptionMessage());

        verifyNoInteractions(repositoryService);
        verifyNoInteractions(forwardingService);
    }

    private <T> T unmarshal(BinaryContentType bct, Class<T> clazz) {
        return JAXB.unmarshal(new ByteArrayInputStream(bct.getValue()), clazz);
    }

    //@Test
    void signal_exception_is_forwarded_and_logged_but_not_acknowledged() throws Exception {
        var sub = SubmissionFactory.submission(properties, PARTY_AP1, null, TEST_ACTION)
                .setOriginalSender(FINAL_SENDER_AP1, null)
                .setFinalRecipient(FINAL_RECIPIENT_AP2, ISO6523_ACTORID_UPIS)
                .addPayload(PayloadUtil.getXmlContent("test-messages/exception-message.xml"));

        TestDataHelper.setService(sub, SDN_EMERGENCE);
        serviceAP1.sendSubmitRequest(sub);

        verifyAndGetReceivedSubmission();
        await(() -> verify(forwardingService).forwardSubmission(any(Submission.class), any(StandardBusinessDocument.class)));
        await(() -> verify(trackingService).logTrackingStatus(any(TrackingStatus.class)));

        var message = getFirstMessage(serviceAP2);

        assertNotNull(message);

        StandardBusinessDocument sbd = unmarshal(message.getRetrieveMessageResponse());
        org.unece.cefact.namespaces.standardbusinessdocumentheader.Exception exception = unmarshal(sbd.getBinaryContent().get(0), org.unece.cefact.namespaces.standardbusinessdocumentheader.Exception.class);
        assertEquals("Internal error", exception.getReason());
        assertEquals("101", exception.getExceptionType().getGeneralException());

        verifyNoInteractions(receiptService);
        verifyNoInteractions(repositoryService);
    }

    public static StandardBusinessDocument unmarshal(RetrieveMessageResponse retrieveMessageResponse) throws IOException {
        return JAXB.unmarshal(retrieveMessageResponse.getPayload().get(0).getValue().getInputStream(), StandardBusinessDocument.class);
    }

    private DomibusWSService getService(GenericContainer<?> domibusContainer) {
        return DomibusWSService.getService(String.format("http://localhost:%d/domibus/services/backend", domibusContainer.getMappedPort(8080)));
    }

    //@Test
    void ap1_sends_receipt_via_gateway() throws Exception {
        var sub = SubmissionFactory.submission(properties, PARTY_AP1, null, TEST_ACTION)
                .setOriginalSender(FINAL_SENDER_AP1, null)
                .setFinalRecipient(FINAL_RECIPIENT_AP2, ISO6523_ACTORID_UPIS)
                .addPayload(PayloadUtil.getXmlContent("test-messages/receipt.xml"));

        TestDataHelper.setService(sub, SDN_EMERGENCE);
        serviceAP1.sendSubmitRequest(sub);

        verifyAndGetReceivedSubmission();
        await(() -> verify(forwardingService).forwardSubmission(any(Submission.class), any(StandardBusinessDocument.class)));

        var message = getFirstMessage(serviceAP2);

        assertNotNull(message);

        verifyNoInteractions(trackingService);
        verifyNoInteractions(repositoryService);
        verifyNoInteractions(receiptService);
    }

    //@Test
    void ap1_sends_to_ap2_via_gateway() throws SubmitMessageFault {
        var sub = SubmissionFactory.submission(properties, PARTY_AP1, null, TEST_ACTION)
                .setOriginalSender(FINAL_SENDER_AP1, null)
                .setFinalRecipient(FINAL_RECIPIENT_AP2, ISO6523_ACTORID_UPIS)
                .addPayload(PayloadUtil.getXmlContent("test-messages/new-example.xml"));

        TestDataHelper.setService(sub, SDN_EMERGENCE);
        serviceAP1.sendSubmitRequest(sub);

        verifyAndGetReceivedSubmission();
        await(() -> verify(trackingService).logTrackingStatus(any(TrackingStatus.class)));
        await(() -> verify(forwardingService).forwardSubmission(any(Submission.class), any(StandardBusinessDocument.class)));
        await(() -> verify(receiptService).sendReceipt(any(Submission.class), any(StandardBusinessDocument.class), any()));

        DomibusWSService.MessageResponse receipt = getFirstMessage(serviceAP1);

        assertNotNull(receipt);

        var message = getFirstMessage(serviceAP2);

        assertNotNull(message);

        verifyNoInteractions(repositoryService);
    }

    private DomibusWSService.MessageResponse getFirstMessage(DomibusWSService service) {
        await(() -> assertFalse(service.fetchPendingMessageIds().isEmpty()));

        var receiptIds = service.fetchPendingMessageIds();
        return service.getMessage(receiptIds.get(0));
    }

    private Submission verifyAndGetReceivedSubmission() {
        var submissionArgumentCaptor = ArgumentCaptor.forClass(Submission.class);
        await(() -> verify(submissionReceiver, times(1)).receiveSubmission(submissionArgumentCaptor.capture()));
        return submissionArgumentCaptor.getValue();
    }

    private void await(ThrowingRunnable runnable) {
        Awaitility.await()
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(runnable);
    }

}
