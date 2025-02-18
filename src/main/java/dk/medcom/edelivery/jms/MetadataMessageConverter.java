package dk.medcom.edelivery.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dk.medcom.edelivery.model.Metadata;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import javax.annotation.Nonnull;
import javax.jms.*;

public class MetadataMessageConverter implements MessageConverter {

    private final ObjectMapper mapper;

    public MetadataMessageConverter() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }


    @Override
    @Nonnull
    public Message toMessage(@Nonnull Object o, Session session) throws JMSException {
        try {
            return session.createTextMessage(mapper.writeValueAsString(o));
        } catch (JsonProcessingException e) {
            throw new MessageConversionException(e.getMessage(), e);
        }
    }

    @Override
    @Nonnull
    public Object fromMessage(@Nonnull Message message) throws JMSException {
        if (message instanceof TextMessage) {
            return fromTextMessage((TextMessage) message);
        }

        throw new MessageFormatException(String.format("Expected message of type %s, but was %s", TextMessage.class.getCanonicalName(), message.getClass().getCanonicalName()));
    }

    private Metadata fromTextMessage(TextMessage message) throws JMSException {
        try {
            return mapper.readValue(message.getText(), Metadata.class);
        } catch (JsonProcessingException e) {
            throw new MessageConversionException(e.getMessage(), e);
        }
    }
}
