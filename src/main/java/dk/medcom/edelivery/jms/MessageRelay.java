package dk.medcom.edelivery.jms;

import dk.medcom.edelivery.model.Metadata;
import dk.medcom.edelivery.service.MetadataService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.Message;

import static dk.medcom.edelivery.jms.JmsConfiguration.JMS_LISTENER_CONTAINER_FACTORY;
import static dk.medcom.edelivery.jms.JmsConfiguration.METADATA_LISTENER_CONTAINER_FACTORY;

@Component
public class MessageRelay {

    private static final Logger log = LogManager.getLogger(MessageRelay.class);

    private final MetadataService registrationService;
    private final MetadataService notificationMetadataService;
    private final Destinations destinations;

    public MessageRelay(MetadataService registrationService, MetadataService notificationMetadataService, Destinations destinations) {
        this.registrationService = registrationService;
        this.notificationMetadataService = notificationMetadataService;
        this.destinations = destinations;
    }

    @Transactional
    @JmsListener(destination = Destinations.METADATA_REGISTER_IN_QUEUE_EXP, containerFactory = METADATA_LISTENER_CONTAINER_FACTORY)
    public void metadataStored(Metadata metadata) {
        log(metadata, destinations.getMetadataRegisterInQueue());
        registrationService.publish(metadata);
    }

    @Transactional
    @JmsListener(destination = Destinations.METADATA_NOTIFY_IN_QUEUE_EXP, containerFactory = METADATA_LISTENER_CONTAINER_FACTORY)
    public void metadataRegistered(Metadata metadata) {
        log(metadata, destinations.getMetadataNotifyInQueue());
        notificationMetadataService.publish(metadata);
    }


    @Transactional
    @JmsListener(destination = Destinations.DOMIBUS_REPLY_QUEUE_EXP, containerFactory = JMS_LISTENER_CONTAINER_FACTORY)
    public void receiveReply(Message message) throws JMSException {
        message.getPropertyNames().asIterator().forEachRemaining(
                name -> {
                    try {
                        log.info("{} : {}", name, message.getObjectProperty((String)name));
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    private void log(Metadata metadata, String destination) {
        log.info("Received message on destination '{}': entryId = '{}', uniqueId = '{}'", destination, metadata.getEntryId(), metadata.getUniqueId());
    }
}
