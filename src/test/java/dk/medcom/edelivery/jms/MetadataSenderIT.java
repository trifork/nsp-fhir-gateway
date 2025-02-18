package dk.medcom.edelivery.jms;

import dk.medcom.edelivery.UseTestContainers;
import dk.medcom.edelivery.model.Metadata;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@UseTestContainers
class MetadataSenderIT {

    @Autowired
    MetadataSender publisher;

    @Autowired
    Destinations destinations;

    @Test
    void name() throws InterruptedException {
        publisher.send(new Metadata().setEntryId("Hello JMS"), new ActiveMQQueue(destinations.getMetadataRegisterInQueue()));

        Thread.sleep(10_000L);
    }
}
