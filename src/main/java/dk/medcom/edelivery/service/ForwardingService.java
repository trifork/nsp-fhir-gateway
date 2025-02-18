package dk.medcom.edelivery.service;

import dk.medcom.edelivery.integration.domibus.model.Submission;
import dk.medcom.edelivery.integration.edelivery.GatewayConfigurationProperties;
import dk.medcom.edelivery.integration.edelivery.SubmissionFactory;
import dk.medcom.edelivery.jms.Destinations;
import dk.medcom.edelivery.jms.SubmissionSender;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import javax.jms.Destination;

@Service
public class ForwardingService {

    private static final Logger log = LogManager.getLogger(ForwardingService.class);

    private final GatewayConfigurationProperties gatewayProperties;
    private final SubmissionSender submissionSender;
    private final Destination destination;

    public ForwardingService(GatewayConfigurationProperties properties, SubmissionSender submissionSender, Destinations destinations) {
        this.gatewayProperties = properties;
        this.submissionSender = submissionSender;
        destination = new ActiveMQQueue(destinations.getDomibusInQueue());
    }

    public void forwardSubmission(Submission original, StandardBusinessDocument document) {
        log.debug("Forwarding submission with MessageId '{}'", original.getMessageId());

        if (gatewayProperties.isXDomain()) {
            var submission = SubmissionFactory.createCrossDomainForwardedSubmission(original, document, gatewayProperties);
            submissionSender.send(submission, destination);
        } else if (gatewayProperties.isSDN() || gatewayProperties.isFOD()) {
            var submission = SubmissionFactory.createDomainForwardedSubmission(original, document, gatewayProperties);
            submissionSender.send(submission, destination);
        } else {
            log.error("Gateway with unsupported type: " + gatewayProperties.getType());
        }
    }

}
