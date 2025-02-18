package dk.medcom.edelivery.jms;

import dk.medcom.edelivery.UseTestContainers;
import dk.medcom.edelivery.fhir.MessageBundleFacade;
import dk.medcom.edelivery.integration.domibus.SubmissionConverter;
import dk.medcom.edelivery.integration.domibus.model.Description;
import dk.medcom.edelivery.integration.domibus.model.Payload;
import dk.medcom.edelivery.integration.domibus.model.Submission;
import dk.medcom.edelivery.integration.domibus.model.TypedProperty;
import dk.medcom.edelivery.integration.edelivery.GatewayConfigurationProperties;
import dk.medcom.edelivery.integration.edelivery.SubmissionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import java.util.List;
import java.util.Locale;

import static dk.medcom.edelivery.TestConstants.*;
import static dk.medcom.edelivery.integration.domibus.MessagePropertyNames.MIME_TYPE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"docker"})
@UseTestContainers
class SubmissionPublisherIT {

    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    GatewayConfigurationProperties properties;

    @MockBean
    MetadataSender metadataSender;

    @MockBean
    MessageBundleFacade messageBundleFacade;

    @BeforeEach
    void setUp() {
        jmsTemplate.setMessageConverter(new SubmissionConverter());
    }

    @Test
    void send_from_ac1_to_ac2() {
        Submission submission = SubmissionFactory.submission(properties, AP_EDELIVERY1, AP_EDELIVERY2, TEST_ACTION)
                .setService(BDX_NOPROCESS)
                .setOriginalSender(AP_EDELIVERY1, "originalSenderType")
                .setFinalRecipient(AP_EDELIVERY2, null)
                .addPayload(getXmlContent());

        jmsTemplate.convertAndSend("domibus.backend.jms.inQueue", submission);
    }

    @Test
    void send_from_ac1_to_ac1() throws InterruptedException {
        Submission submission = SubmissionFactory.submission(properties, AP_EDELIVERY1, AP_EDELIVERY1, TEST_ACTION)
                .setService(BDX_NOPROCESS)
                .setOriginalSender(AP_EDELIVERY1, "originalSenderType")
                .setFinalRecipient(AP_EDELIVERY1, null)
                .addPayload(getXmlContent());

        jmsTemplate.convertAndSend("domibus.backend.jms.inQueue", submission);

        Thread.sleep(1_000L);

        verify(metadataSender).send(any(), any());
    }

    private Payload getXmlContent() {
        return new Payload(
                "cid:message",
                new DataHandler(new ByteArrayDataSource("<Hello/>".getBytes(), "text/xml")),
                List.of(new TypedProperty(MIME_TYPE, "text/xml")),
                false,
                new Description(Locale.getDefault(), "test content"),
                null
        );
    }
}
