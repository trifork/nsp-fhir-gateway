package dk.medcom.edelivery.integration.edelivery;

import dk.medcom.edelivery.integration.domibus.model.Description;
import dk.medcom.edelivery.integration.domibus.model.Payload;
import dk.medcom.edelivery.integration.domibus.model.Submission;
import dk.medcom.edelivery.integration.domibus.model.TypedProperty;
import dk.medcom.edelivery.jms.Destinations;
import dk.medcom.edelivery.jms.SubmissionSender;
import dk.medcom.edelivery.model.mapping.SBDMarshaller;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import javax.jms.Destination;
import java.util.List;
import java.util.Locale;

import static dk.medcom.edelivery.integration.domibus.MessagePropertyNames.MIME_TYPE;

@Service
public class DomibusReceiptService implements ReceiptService {

    private static final Logger log = LogManager.getLogger(DomibusReceiptService.class);

    private final SubmissionSender submissionSender;
    private final Destination destination;
    private final GatewayConfigurationProperties properties;

    public DomibusReceiptService(SubmissionSender submissionSender, Destinations destinations, GatewayConfigurationProperties properties) {
        this.properties = properties;
        this.submissionSender = submissionSender;
        this.destination = new ActiveMQQueue(destinations.getDomibusInQueue());
    }

    @Override
    public void sendReceipt(Submission original, StandardBusinessDocument receipt, String action) {
        log.debug("Sending receipt for MessageId '{}'", original.getMessageId());
        var payload = createPayload(receipt);
        var submission = SubmissionFactory.createResponse(original, payload, properties, action);

        submissionSender.send(submission, destination);
    }

    private Payload createPayload(StandardBusinessDocument receipt) {
        return new Payload(
                "cid:message",
                SBDMarshaller.marshal(receipt),
                List.of(new TypedProperty(MIME_TYPE, "text/xml")),
                false,
                new Description(Locale.getDefault(), "Receipt"),
                null);
    }

}
