package dk.medcom.edelivery.integration.domibus;

import dk.medcom.edelivery.integration.domibus.model.Submission;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;

import static dk.medcom.edelivery.model.MedComConstants.PARTY_ID_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SubmissionConverterTest {


    @Mock
    private Session session;

    private SubmissionConverter submissionConverter;

    @BeforeEach
    void setUp() throws JMSException {
        when(session.createMapMessage()).thenReturn(new ActiveMQMapMessage());
        submissionConverter = new SubmissionConverter();
    }

    @Test
    void convert_submission_to_message_and_back() throws JMSException {

        var submission = new Submission()
                .setFinalRecipient("finalRecipient", "finalRecipientType")
                .setOriginalSender("originalSender", PARTY_ID_PREFIX)
                .addToParty("toPartyId", "toPartyIdType")
                .setToRole("toRole")
                .addFromParty("fromPartyId", "fromPartyType")
                .setFromRole("fromRole")
                .setService("service")
                .setAction("foobarbaz");

        var message = submissionConverter.toMessage(submission, session);

        assertTrue(message instanceof MapMessage);

        var result = submissionConverter.fromMessage(message);

        assertTrue(result instanceof Submission);

        System.out.println(result);

        assertEquals(submission.toString(), result.toString());
    }
}
