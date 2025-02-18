package dk.medcom.edelivery.jms;

import dk.medcom.edelivery.model.Metadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.Destination;

@Component
public class MetadataSender {

    private static final Logger log = LogManager.getLogger(MetadataSender.class);

    private final JmsTemplate jmsTemplate;
    private final MetadataMessageConverter messageConverter = new MetadataMessageConverter();

    public MetadataSender(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Transactional
    public void send(Metadata metadata, Destination destination) {
        log.debug("Sending message {} to destination '{}'", metadata, destination);
        jmsTemplate.send(destination, session -> messageConverter.toMessage(metadata, session));
    }

}
