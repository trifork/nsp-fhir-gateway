package dk.medcom.edelivery.jms;

import dk.medcom.edelivery.integration.domibus.SubmissionConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.SingleConnectionFactory;

import javax.jms.Session;

@Configuration
@EnableJms
public class JmsConfiguration {

    public static final String METADATA_LISTENER_CONTAINER_FACTORY = "metadataListenerContainerFactory";
    public static final String SUBMISSION_LISTENER_CONTAINER_FACTORY = "submissionListenerContainerFactory";
    public static final String JMS_LISTENER_CONTAINER_FACTORY = "jmsListenerContainerFactory";

    @Bean
    @Qualifier(METADATA_LISTENER_CONTAINER_FACTORY)
    public DefaultJmsListenerContainerFactory metadataListenerContainerFactory(SingleConnectionFactory connectionFactory) {
        var factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new MetadataMessageConverter());
        factory.setConcurrency("1-1");
        factory.setSessionTransacted(true);
        factory.setSessionAcknowledgeMode(Session.SESSION_TRANSACTED);
        factory.setPubSubDomain(false);
        return factory;
    }

    @Bean
    @Qualifier(SUBMISSION_LISTENER_CONTAINER_FACTORY)
    public DefaultJmsListenerContainerFactory submissionListenerContainerFactory(SingleConnectionFactory connectionFactory) {
        var factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new SubmissionConverter());
        factory.setConcurrency("1-1");
        factory.setSessionTransacted(true);
        factory.setSessionAcknowledgeMode(Session.SESSION_TRANSACTED);
        factory.setPubSubDomain(false);
        return factory;
    }


    @Bean
    @Qualifier(JMS_LISTENER_CONTAINER_FACTORY)
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(SingleConnectionFactory connectionFactory) {
        var factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrency("1-1");
        factory.setSessionTransacted(true);
        factory.setSessionAcknowledgeMode(Session.SESSION_TRANSACTED);
        factory.setPubSubDomain(false);
        return factory;
    }
}
