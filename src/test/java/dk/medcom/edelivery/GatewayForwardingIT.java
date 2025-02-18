package dk.medcom.edelivery;

import dk.medcom.edelivery.integration.domibus.model.Payload;
import dk.medcom.edelivery.integration.domibus.model.Submission;
import dk.medcom.edelivery.integration.edelivery.DomibusReceiptService;
import dk.medcom.edelivery.integration.edelivery.GatewayConfigurationProperties;
import dk.medcom.edelivery.integration.edelivery.SubmissionFactory;
import dk.medcom.edelivery.integration.tracking.TrackingService;
import dk.medcom.edelivery.jms.Destinations;
import dk.medcom.edelivery.jms.SubmissionReceiver;
import dk.medcom.edelivery.jms.SubmissionSender;
import dk.medcom.edelivery.model.mapping.SBDHAdapter;
import dk.medcom.edelivery.model.mapping.SBDUnmarshaller;
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
class GatewayForwardingIT {

    @MockBean
    SubmissionSender submissionSender;

    @MockBean
    DomibusReceiptService domibusReceiptService; // to avoid more SubmissionSender interactions than required for the test

    @MockBean
    TrackingService trackingService; // not interesting for this test

    @Autowired
    SubmissionReceiver submissionReceiver;

    @Autowired
    Destinations destinations;

    @Autowired
    GatewayConfigurationProperties properties;

    @Test
    void forwardedXDomainMessage() {
        verifyGatewayForwarding(SDN_EMERGENCE, TestConstants.FOD_EMERGENCE);
        verifyGatewayForwarding(TestConstants.SDN_DISTRIBUTION, TestConstants.FOD_EMERGENCE);
        verifyGatewayForwarding(TestConstants.FOD_EMERGENCE, SDN_EMERGENCE);
        verifyGatewayForwarding(TestConstants.FOD_DISTRIBUTION, SDN_EMERGENCE);
    }

    @Test
    void forwardedDomainMessage() {
        properties.setType("sdn");

        verifyGatewayForwarding(SDN_EMERGENCE, TestConstants.SDN_DISTRIBUTION);
        verifyGatewayForwarding(TestConstants.FOD_EMERGENCE, TestConstants.FOD_DISTRIBUTION);
    }

    private void verifyGatewayForwarding(String sentService, String mappedService) {
        reset(submissionSender);
        Submission submission = getSubmission(sentService);
        submission.setService(sentService);
        submissionReceiver.receiveSubmission(submission);

        ArgumentCaptor<Submission> argumentCaptor = ArgumentCaptor.forClass(Submission.class);
        verify(submissionSender, times(1)).send(argumentCaptor.capture(), any());

        Submission forwardedSubmission = argumentCaptor.getValue();
        StandardBusinessDocumentHeader fullOriginalSBDH = getStandardBusinessDocument(submission).getStandardBusinessDocumentHeader();
        StandardBusinessDocumentHeader fullForwardedSBDH = getStandardBusinessDocument(forwardedSubmission).getStandardBusinessDocumentHeader();
        SBDHAdapter originalSDBH = SBDHAdapter.from(fullOriginalSBDH);
        SBDHAdapter forwardedSDBH = SBDHAdapter.from(fullForwardedSBDH);

        // Check submission
        assertEquals(submission.getAction(), forwardedSubmission.getAction());
        assertNotNull(forwardedSubmission.getMessageId());
        assertNotEquals(submission.getMessageId(), forwardedSubmission.getMessageId());
        assertEquals(submission.getToParties().stream().findFirst().get().getPartyId(), forwardedSubmission.getOriginalSender().getPartyId());
        assertEquals(submission.getFromParties().stream().findFirst().get(), forwardedSubmission.getFromParties().stream().findFirst().get());
        assertEquals(submission.getAgreementRef(), forwardedSubmission.getAgreementRef());
        assertEquals(submission.getAgreementRefType(), forwardedSubmission.getAgreementRefType());
        assertEquals(submission.getConversationId(), forwardedSubmission.getConversationId());
        assertEquals(submission.getToRole(), forwardedSubmission.getToRole());

        // check SBDH
        assertNotEquals(fullOriginalSBDH.getDocumentIdentification().getCreationDateAndTime(), fullForwardedSBDH.getDocumentIdentification().getCreationDateAndTime());
        assertNotEquals(fullOriginalSBDH.getDocumentIdentification().getInstanceIdentifier(), fullForwardedSBDH.getDocumentIdentification().getInstanceIdentifier());
        assertEquals(originalSDBH.getScope("HEALTHCARE_FACILITY_TYPE_CODE").get().getInstanceIdentifier(), forwardedSDBH.getScope("HEALTHCARE_FACILITY_TYPE_CODE").get().getInstanceIdentifier());
        assertEquals(originalSDBH.getScope("LANGUAGECODE").get().getInstanceIdentifier(), forwardedSDBH.getScope("LANGUAGECODE").get().getInstanceIdentifier());
        assertEquals(originalSDBH.getScope("PATIENTID").get().getInstanceIdentifier(), forwardedSDBH.getScope("PATIENTID").get().getInstanceIdentifier());
        assertEquals(originalSDBH.getScope("TITLE").get().getInstanceIdentifier(), forwardedSDBH.getScope("TITLE").get().getInstanceIdentifier());

        // Specific service/processId checks
        assertEquals(mappedService, forwardedSubmission.getService());
        if (properties.isXDomain()) {
            assertEquals("urn:www.digst.dk:profile:fod-emergence", forwardedSDBH.getScope("PROCESSID").get().getInstanceIdentifier()); // mapped from file
        }
        if (properties.isSDN()) {
            assertEquals("urn:www.digst.dk:profile:sdn-distribution", forwardedSDBH.getScope("PROCESSID").get().getInstanceIdentifier()); // mapped from file
        }
    }

    private StandardBusinessDocument getStandardBusinessDocument(Submission submission) {
        Payload payload = submission.getPayloads().stream().findFirst().orElseThrow();
        return SBDUnmarshaller.unmarshal(payload.getPayloadDatahandler());
    }

    private Submission getSubmission(String service) {
        Payload xmlContent = PayloadUtil.getXmlContent("test-messages/new-example.xml");
        return SubmissionFactory.submission(properties, AP_EDELIVERY1, AP_EDELIVERY1, TEST_ACTION)
                .setService(service)
                .setOriginalSender(AP_EDELIVERY1, "originalSenderType")
                .setFinalRecipient(AP_EDELIVERY1, null)
                .addPayload(xmlContent);
    }

}
