package dk.medcom.edelivery.jms;

import dk.medcom.edelivery.integration.domibus.SubmissionConverter;
import dk.medcom.edelivery.integration.domibus.model.Submission;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.Destination;

@Component
public class SubmissionSender {

    private static final Logger log = LogManager.getLogger(SubmissionSender.class);

    private final JmsTemplate jmsTemplate;
    private final SubmissionConverter messageConverter = new SubmissionConverter();

    public SubmissionSender(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Transactional
    public void send(Submission submission, Destination destination) {
        log.debug("Sending message {} to destination '{}'", submission, destination);
        jmsTemplate.send(destination, session -> messageConverter.toMessage(submission, session));
    }
}
