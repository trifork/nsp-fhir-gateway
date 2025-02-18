package dk.medcom.edelivery;

import dk.medcom.edelivery.integration.domibus.model.Payload;
import dk.medcom.edelivery.integration.domibus.model.Submission;
import dk.medcom.edelivery.integration.domibus.model.TypedProperty;
import dk.medcom.edelivery.integration.edelivery.DomibusReceiptService;
import dk.medcom.edelivery.integration.edelivery.GatewayConfigurationProperties;
import dk.medcom.edelivery.integration.edelivery.SubmissionFactory;
import dk.medcom.edelivery.integration.tracking.TrackingService;
import dk.medcom.edelivery.jms.Destinations;
import dk.medcom.edelivery.jms.SubmissionReceiver;
import dk.medcom.edelivery.jms.SubmissionSender;
import dk.medcom.edelivery.model.mapping.SBDUnmarshaller;
import dk.medcom.edelivery.service.RepositoryService;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@UseTestContainers
class GatewayPersistIT {

    static String DDS_RECIPIENT_TYPE = "iso6523-actorid-upis";
    static String DDS_RECIPIENT_ID = "0088:dds";
    static String DDS_PROCESS_ID = "urn:www.digst.dk:profile:dds-persist";

    @MockBean
    SubmissionSender submissionSender;

    @MockBean
    DomibusReceiptService domibusReceiptService; // to avoid more SubmissionSender interactions than required for the test

    @MockBean
    TrackingService trackingService; // not interesting for this test

    @Autowired
    SubmissionReceiver submissionReceiver;

    @MockBean
    RepositoryService repositoryService;

    @Autowired
    Destinations destinations;

    @Autowired
    GatewayConfigurationProperties properties;

    @Test
    void persistMessage() {
        Submission submission = getDDSSubmission();
        submissionReceiver.receiveSubmission(submission);

        ArgumentCaptor<StandardBusinessDocument> argumentCaptor = ArgumentCaptor.forClass(StandardBusinessDocument.class);
        verify(repositoryService, times(1)).persist(argumentCaptor.capture());

        StandardBusinessDocumentHeader persistedSubmission = argumentCaptor.getValue().getStandardBusinessDocumentHeader();
        StandardBusinessDocumentHeader fullOriginalSBDH = getStandardBusinessDocument(submission).getStandardBusinessDocumentHeader();

        assertEquals(fullOriginalSBDH.getDocumentIdentification().getStandard(), persistedSubmission.getDocumentIdentification().getStandard());
        assertEquals(fullOriginalSBDH.getDocumentIdentification().getType(), persistedSubmission.getDocumentIdentification().getType());
        assertEquals(fullOriginalSBDH.getDocumentIdentification().getTypeVersion(), persistedSubmission.getDocumentIdentification().getTypeVersion());
    }

    private StandardBusinessDocument getStandardBusinessDocument(Submission submission) {
        Payload payload = submission.getPayloads().stream().findFirst().orElseThrow();
        return SBDUnmarshaller.unmarshal(payload.getPayloadDatahandler());
    }

    private Submission getDDSSubmission() {
        Payload xmlContent = PayloadUtil.getXmlContent("test-messages/new-example.xml");
        TypedProperty finalRecipient = new TypedProperty(DDS_RECIPIENT_TYPE, DDS_RECIPIENT_ID, "finalRecipient");
        var submission = SubmissionFactory.submission(properties, AP_EDELIVERY1, AP_EDELIVERY1, TEST_ACTION)
                .setService(DDS_PROCESS_ID)
                .setOriginalSender(AP_EDELIVERY1, "originalSenderType")
                .setFinalRecipient("0088:dds", null)
                .addPayload(xmlContent);
        submission.getMessageProperties().add(finalRecipient);
        return submission;
    }

}
