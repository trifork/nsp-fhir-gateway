package dk.medcom.edelivery.service;

import dk.medcom.edelivery.MedComGateway;
import dk.medcom.edelivery.UseTestContainers;
import dk.medcom.edelivery.jms.JmsConfiguration;
import dk.medcom.edelivery.jms.MetadataSender;
import dk.medcom.edelivery.jms.TestMessageRelay;
import dk.medcom.edelivery.model.Metadata;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        classes = {JmsConfiguration.class, MedComGateway.class, TestMessageRelay.class, TestServiceConfig.class},
        properties = {
                "spring.activemq.broker-url=vm://embedded?broker.persistent=false,useShutdownHook=false",
                "spring.activemq.user=admin",
                "spring.activemq.password=admin",
                "spring.activemq.in-memory=true"
        })
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MessagingMetadataServiceIT {

    @Autowired
    TestMessageRelay messageRelay;

    @Autowired
    MetadataSender publisher;

    @Autowired
    MetadataService metaDataServiceMock;

    @BeforeEach
    void setUp() {
        messageRelay.reset();
    }

    @Test
    void successful_operation_of_service_results_in_new_jms_message() {
        publisher.send(new Metadata().setEntryId("1234"), new ActiveMQQueue("send.success"));

        await().pollInterval(Duration.ofMillis(200L))
                .atMost(Duration.ofSeconds(5L))
                .untilAsserted(() -> assertTrue(messageRelay.isReceivedSuccess()));
    }

    @Test
    void failure_of_service_operation_results_in_no_new_jms_message() {
        publisher.send(new Metadata().setEntryId("1234"), new ActiveMQQueue("send.fail"));

        await().pollDelay(Duration.ofSeconds(4L))
                .atMost(Duration.ofSeconds(5L))
                .untilAsserted(() -> assertFalse(messageRelay.isReceivedFail()));
    }

    @Test
    void failure_sending_new_jms_message_results_in_service_not_called() {
        publisher.send(new Metadata().setEntryId("1234"), new ActiveMQQueue("send.fail-publish"));

        await().pollDelay(Duration.ofSeconds(4L))
                .atMost(Duration.ofSeconds(5L))
                .untilAsserted(() -> Mockito.verifyNoInteractions(metaDataServiceMock));
    }
}
