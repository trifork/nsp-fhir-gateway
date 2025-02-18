package dk.medcom.edelivery.service;

import dk.medcom.edelivery.jms.MetadataSender;
import dk.medcom.edelivery.model.Metadata;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.Destination;

public class MessagingMetadataService extends MetadataServiceDecorator {

    private final MetadataSender publisher;
    private final Destination destination;

    public MessagingMetadataService(MetadataService delegate, MetadataSender publisher, Destination destination) {
        super(delegate);
        this.publisher = publisher;
        this.destination = destination;
    }

    @Override
    @Transactional
    public void publish(Metadata metadata) {
        publisher.send(metadata, destination);
        super.publish(metadata);
    }
}
