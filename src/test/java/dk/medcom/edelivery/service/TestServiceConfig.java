package dk.medcom.edelivery.service;

import dk.medcom.edelivery.integration.dgws.SOSIConfigurationProperties;
import dk.medcom.edelivery.integration.dgws.SOSIIDCardMocesProviderImpl;
import dk.medcom.edelivery.integration.dgws.SOSIIDCardProvider;
import dk.medcom.edelivery.integration.dgws.SOSIIDCardProviderImpl;
import dk.medcom.edelivery.jms.MetadataSender;
import dk.medcom.edelivery.model.Metadata;
import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.vault.CredentialVault;
import org.apache.activemq.command.ActiveMQQueue;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Destination;

@TestConfiguration
public class TestServiceConfig {

    @Bean
    @Primary
    public SOSIIDCardProvider sosiidCardProvider(SOSIConfigurationProperties properties, SOSIFactory factory, CredentialVault credentialVault) {
        return new SOSIIDCardProviderImpl(properties, factory, credentialVault);
    }

    @Bean
    MetadataService failingService(MetadataSender publisher) {
        return new MessagingMetadataService((meta -> {throw new RuntimeException();}), publisher, new ActiveMQQueue("receive.fail"));
    }

    @Bean
    MetadataService successService(MetadataSender publisher) {
        return new MessagingMetadataService(meta -> System.out.println("Success! " + meta.getEntryId()), publisher, new ActiveMQQueue("receive.success"));
    }

    @Bean
    MetadataService metaDataServiceMock() {
        return Mockito.mock(MetadataService.class);
    }

    @Bean
    MetadataService failPublishService(MetadataService metaDataServiceMock) {
        return new MessagingMetadataService(metaDataServiceMock, new FailingSender(), new ActiveMQQueue("receive.fail-publish"));
    }

    private static class FailingSender extends MetadataSender {

        public FailingSender() {
            super(new JmsTemplate());
        }

        @Override
        public void send(Metadata metadata, Destination destination) {
            throw new RuntimeException();
        }
    }

}
