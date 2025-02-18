package dk.medcom.edelivery.integration.domibus;

import dk.medcom.edelivery.integration.domibus.model.Party;
import dk.medcom.edelivery.integration.domibus.model.Payload;
import dk.medcom.edelivery.integration.domibus.model.Submission;
import dk.medcom.edelivery.integration.domibus.model.TypedProperty;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.lang.NonNull;

import javax.activation.DataHandler;
import javax.activation.URLDataSource;
import javax.annotation.Nonnull;
import javax.jms.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;

import static dk.medcom.edelivery.integration.domibus.MessagePropertyNames.*;
import static dk.medcom.edelivery.model.MedComConstants.PARTY_ID_PREFIX;
import static java.lang.System.getProperty;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.trim;

/**
 * This class is responsible for transformations from {@link javax.jms.MapMessage} to {@link Submission} and vice versa
 *
 * @author Padraig McGourty, Christian Koch, Stefan Mueller
 * @author Cosmin Baciu
 */

public class SubmissionConverter implements MessageConverter {

    private static final Logger log = LogManager.getLogger(SubmissionConverter.class);

    @Override
    @Nonnull
    public Message toMessage(@Nonnull Object object, @NonNull Session session) throws JMSException {
        if (!(object instanceof Submission))
            throw new MessageConversionException(String.format("Cannot convert object of type %s to message.", object.getClass().getCanonicalName()));

        Submission submission = (Submission) object;

        MapMessage messageOut = session.createMapMessage();

        try {
            if (submission.getMpc() != null)
                messageOut.setStringProperty(MessagePropertyNames.MPC, submission.getMpc());

            messageOut.setStringProperty(ACTION, submission.getAction());
            messageOut.setStringProperty(SERVICE, submission.getService());
            messageOut.setStringProperty(SERVICE_TYPE, submission.getServiceType());
            messageOut.setStringProperty(CONVERSATION_ID, submission.getConversationId());
            messageOut.setStringProperty(MESSAGE_ID, submission.getMessageId());

            submission.getFromParties().forEach(fromParty -> addFromParty(messageOut, fromParty));

            messageOut.setStringProperty(FROM_ROLE, submission.getFromRole());

            submission.getToParties().forEach(toParty -> addToParty(messageOut, toParty));

            messageOut.setStringProperty(TO_ROLE, submission.getToRole());

            messageOut.setStringProperty(PROPERTY_ORIGINAL_SENDER, submission.getOriginalSender().getPartyIdType() + ":" + submission.getOriginalSender().getPartyId());
//            messageOut.setStringProperty(PROPERTY_ORIGINAL_SENDER, submission.getOriginalSender().getPartyId());
            messageOut.setStringProperty(PROPERTY_FINAL_RECIPIENT, submission.getFinalRecipient().getPartyId());
            messageOut.setStringProperty(PROPERTY_FINAL_RECIPIENT_TYPE, submission.getFinalRecipient().getPartyIdType());
            messageOut.setStringProperty(PROPERTY_ENDPOINT, submission.getEndpoint());

            addMessageProperties(submission, messageOut);

            messageOut.setStringProperty(PROTOCOL, "AS4");
            messageOut.setStringProperty(JMS_BACKEND_MESSAGE_TYPE_PROPERTY_KEY, MESSAGE_TYPE_SUBMIT);
            messageOut.setJMSCorrelationID(UUID.randomUUID().toString());

            messageOut.setStringProperty(AGREEMENT_REF, submission.getAgreementRef());
            messageOut.setStringProperty(REF_TO_MESSAGE_ID, submission.getRefToMessageId());

            // save the first payload (payload_1) for the bodyload (if exists)
            int counter = 1;
            for (final Payload p : submission.getPayloads()) {
                if (p.isInBody()) {
                    counter = 2;
                    break;
                }
            }

            final boolean putAttachmentsInQueue = Boolean.parseBoolean(getProperty(PUT_ATTACHMENTS_IN_QUEUE, "true"));

            for (final Payload p : submission.getPayloads()) {
                // counter is increased for payloads (not for bodyload which is always set to payload_1)
                counter = transformFromSubmissionHandlePayload(messageOut, putAttachmentsInQueue, counter, p);
            }

            messageOut.setIntProperty(TOTAL_NUMBER_OF_PAYLOADS, submission.getPayloads().size());
        } catch (IOException ex) {
            throw new MessageConversionException("Error converting submission to message.", ex);
        }

        log.debug("Converted submission to MapMessage.");
        logMessage(messageOut);

        return messageOut;
    }

    private void addMessageProperties(Submission submission, MapMessage messageOut) throws JMSException {
        for (final TypedProperty p : submission.getMessageProperties()) {
            addMessageProperty(messageOut, p);
        }
    }

    private void addMessageProperty(MapMessage messageOut, TypedProperty p) throws JMSException {
        messageOut.setStringProperty(PROPERTY_PREFIX + p.getKey(), p.getValue());
        messageOut.setStringProperty(PROPERTY_TYPE_PREFIX + p.getKey(), p.getType());
    }

    private void addToParty(MapMessage messageOut, Party toParty) {
        setStringProperty(messageOut, TO_PARTY_ID, toParty.getPartyId());
        setStringProperty(messageOut, TO_PARTY_TYPE, toParty.getPartyIdType());
    }

    private void addFromParty(MapMessage messageOut, Party fromParty) {
        setStringProperty(messageOut, FROM_PARTY_ID, fromParty.getPartyId());
        setStringProperty(messageOut, FROM_PARTY_TYPE, fromParty.getPartyIdType());
    }

    private void setStringProperty(MapMessage mapMessage, String name, String value) {
        try {
            mapMessage.setStringProperty(name, value);
        } catch (JMSException e) {
            throw new MessageConversionException(String.format("Error setting property %s on Message", name), e);
        }
    }

    private int transformFromSubmissionHandlePayload(MapMessage messageOut, boolean putAttachmentsInQueue, int counter, Payload p) throws IOException, JMSException {
        if (p.isInBody()) {
            if (p.getPayloadDatahandler() != null) {
                messageOut.setBytes(MessageFormat.format(PAYLOAD_NAME_FORMAT, 1), IOUtils.toByteArray(p.getPayloadDatahandler().getInputStream()));
                messageOut.setStringProperty(P1_IN_BODY, "true");
            }

            messageOut.setStringProperty(MessageFormat.format(PAYLOAD_MIME_TYPE_FORMAT, 1), findMime(p.getPayloadProperties()));
            messageOut.setStringProperty(MessageFormat.format(PAYLOAD_MIME_CONTENT_ID_FORMAT, 1), p.getContentId());
        } else {
            final String payContID = MessageFormat.format(PAYLOAD_MIME_CONTENT_ID_FORMAT, counter);
            final String propPayload = MessageFormat.format(PAYLOAD_NAME_FORMAT, counter);
            final String payMimeTypeProp = MessageFormat.format(PAYLOAD_MIME_TYPE_FORMAT, counter);
            final String payFileNameProp = MessageFormat.format(PAYLOAD_FILE_NAME_FORMAT, counter);
            if (p.getPayloadDatahandler() != null) {
                if (putAttachmentsInQueue) {
                    messageOut.setBytes(propPayload, IOUtils.toByteArray(p.getPayloadDatahandler().getInputStream()));
                } else {
                    messageOut.setStringProperty(payFileNameProp, findFilename(p.getPayloadProperties()));
                }
            }
            messageOut.setStringProperty(payMimeTypeProp, findMime(p.getPayloadProperties()));
            messageOut.setStringProperty(payContID, p.getContentId());
            counter++;
        }
        return counter;
    }

    private String findElement(String element, Collection<TypedProperty> props) {
        for (TypedProperty prop : props) {
            if (element.equals(prop.getKey()) && StringUtils.isEmpty(StringUtils.trim(prop.getType()))) {
                return prop.getValue();
            }
        }
        return null;
    }

    private String findMime(Collection<TypedProperty> props) {
        return findElement(MIME_TYPE, props);
    }

    private String findFilename(Collection<TypedProperty> props) {
        return findElement(PAYLOAD_FILENAME, props);
    }

    @Override
    @Nonnull
    public Object fromMessage(@NonNull Message message) throws JMSException {
        MapMessage messageIn = toMapMessage(message);

        log.debug("Converting MapMessage to Submission object");
        logMessage(messageIn);

        final Submission target = new Submission();

        String mpc = trim(messageIn.getStringProperty(MPC));
        if (!isEmpty(mpc)) {
            target.setMpc(mpc);
        }
        target.setMessageId(trim(messageIn.getStringProperty(MESSAGE_ID)));

        setTargetFromPartyIdAndFromPartyType(messageIn, target);

        target.setFromRole(getPropertyWithFallback(messageIn, FROM_ROLE));

        setTargetToPartyIdAndToPartyType(messageIn, target);

        target.setToRole(getPropertyWithFallback(messageIn, TO_ROLE));

        target.setAction(getPropertyWithFallback(messageIn, ACTION));

        target.setService(getPropertyWithFallback(messageIn, SERVICE));

        target.setServiceType(getPropertyWithFallback(messageIn, SERVICE_TYPE));

        target.setAgreementRef(getPropertyWithFallback(messageIn, AGREEMENT_REF));

        target.setConversationId(trim(messageIn.getStringProperty(CONVERSATION_ID)));

        String strOriginalSender = trim(messageIn.getStringProperty(PROPERTY_ORIGINAL_SENDER));
        if (!isEmpty(strOriginalSender)) {
            var party = PartyMapper.toParty(strOriginalSender);
            target.setOriginalSender(party);

            var idx = strOriginalSender.indexOf(PARTY_ID_PREFIX);
            if (idx >= 0) {
                idx += PARTY_ID_PREFIX.length();
                var id = strOriginalSender.substring(idx + 1);
                var type = strOriginalSender.substring(0, idx);
                target.setOriginalSender(id, type);
            } else {
                target.setOriginalSender(strOriginalSender, null);
            }
        }

        String endpoint = trim(messageIn.getStringProperty(PROPERTY_ENDPOINT));
        if (!isEmpty(endpoint)) {
            target.setEndpoint(endpoint);
        }

        String strFinalRecipient = trim(messageIn.getStringProperty(PROPERTY_FINAL_RECIPIENT));
        String strFinalRecipientType = trim(messageIn.getStringProperty(PROPERTY_FINAL_RECIPIENT_TYPE));
        if (!isEmpty(strFinalRecipient)) {
            target.setFinalRecipient(strFinalRecipient, strFinalRecipientType);
        }

        target.setRefToMessageId(trim(messageIn.getStringProperty(REF_TO_MESSAGE_ID)));

        final int numPayloads = messageIn.getIntProperty(TOTAL_NUMBER_OF_PAYLOADS);

        Enumeration<?> allProps = messageIn.getPropertyNames();
        while (allProps.hasMoreElements()) {
            String key = allProps.nextElement().toString();
            if (key.startsWith(PROPERTY_PREFIX)) {
                target.addMessageProperty(key.substring(PROPERTY_PREFIX.length()), trim(messageIn.getStringProperty(key)), trim(messageIn.getStringProperty(PROPERTY_TYPE_PREFIX + key.substring(PROPERTY_PREFIX.length()))));
            }
        }

        String bodyloadEnabled = getPropertyWithFallback(messageIn, P1_IN_BODY);
        for (int i = 1; i <= numPayloads; i++) {
            transformToSubmissionHandlePayload(messageIn, target, bodyloadEnabled, i);
        }

        return target;
    }

    private MapMessage toMapMessage(Message message) {
        if (!(message instanceof MapMessage))
            throw new MessageConversionException(String.format("Could not convert message of type %s to Submission", message.getClass().getCanonicalName()));

        return (MapMessage) message;
    }

    private String getPropertyWithFallback(final MapMessage messageIn, String propName) throws JMSException {
        String propValue = trim(messageIn.getStringProperty(propName));

        if (isEmpty(propValue))
            propValue = getProperty(propName);

        return propValue;
    }

    private void setTargetToPartyIdAndToPartyType(MapMessage messageIn, Submission target) throws JMSException {
        String toPartyID = getPropertyWithFallback(messageIn, TO_PARTY_ID);
        String toPartyType = getPropertyWithFallback(messageIn, TO_PARTY_TYPE);

        if (toPartyID != null)
            target.addToParty(toPartyID, toPartyType);
    }

    private void setTargetFromPartyIdAndFromPartyType(MapMessage messageIn, Submission target) throws JMSException {
        String fromPartyID = getPropertyWithFallback(messageIn, FROM_PARTY_ID);
        String fromPartyType = getPropertyWithFallback(messageIn, FROM_PARTY_TYPE);
        target.addFromParty(fromPartyID, fromPartyType);
    }

    private void transformToSubmissionHandlePayload(MapMessage messageIn, Submission target, String bodyloadEnabled, int i) throws JMSException {

        final Collection<TypedProperty> partProperties = new ArrayList<>();

        final String mimeType = getPropertyByFormattedName(messageIn, PAYLOAD_MIME_TYPE_FORMAT, i);
        if (isNotNullOrBlank(mimeType)) {
            partProperties.add(new TypedProperty(MIME_TYPE, mimeType));
        }

        final String fileName = getPropertyByFormattedName(messageIn, PAYLOAD_FILE_NAME_FORMAT, i);
        if (isNotNullOrBlank(fileName)) {
            partProperties.add(new TypedProperty(PAYLOAD_FILENAME, fileName));
        }

        final String payloadName = getPropertyByFormattedName(messageIn, JMS_PAYLOAD_NAME_FORMAT, i);
        if (StringUtils.isNotBlank(payloadName)) {
            partProperties.add(new TypedProperty(PAYLOAD_PROPERTY_FILE_NAME, payloadName));
        }

        final String propPayload = MessageFormat.format(PAYLOAD_NAME_FORMAT, i);
        DataHandler payloadDataHandler = getDataHandler(messageIn, propPayload, mimeType);

        boolean inBody = (i == 1 && "true".equalsIgnoreCase(bodyloadEnabled));

        final String contentId = getPropertyByFormattedName(messageIn, PAYLOAD_MIME_CONTENT_ID_FORMAT, i);
        target.addPayload(contentId, payloadDataHandler, partProperties, inBody, null, null);
    }

    private boolean isNotNullOrBlank(String mimeType) {
        return mimeType != null && !mimeType.trim().equals("");
    }

    private String getPropertyByFormattedName(MapMessage messageIn, String propertyFormat, int i) throws JMSException {
        final String propertyName = MessageFormat.format(propertyFormat, i);
        return trim(messageIn.getStringProperty(propertyName));
    }

    private DataHandler getDataHandler(MapMessage messageIn, String propPayload, String mimeType) throws JMSException {
        try {
            return new DataHandler(new ByteArrayDataSource(messageIn.getBytes(propPayload), mimeType));
        } catch (JMSException jmsEx) {
            return getUrlSourceDataHandler(messageIn, propPayload);
        }
    }

    private DataHandler getUrlSourceDataHandler(MapMessage messageIn, String propPayload) throws JMSException {
        try {
            return new DataHandler(new URLDataSource(new URL(messageIn.getString(propPayload))));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(propPayload + " neither available as byte[] or URL, aborting transformation");
        }
    }

    private void logMessage(MapMessage message) throws JMSException {
        Map<String, Object> properties = new LinkedHashMap<>();

        var iterator = message.getPropertyNames().asIterator();

        while (iterator.hasNext()) {
            var key = (String) iterator.next();
            properties.put(key, message.getObjectProperty(key));
        }

        log.debug(properties);
    }
}
