package dk.medcom.edelivery.service;

import dk.medcom.edelivery.integration.dds.RegisterDocumentService;
import dk.medcom.edelivery.integration.nas.NotificationService;
import dk.medcom.edelivery.jms.Destinations;
import dk.medcom.edelivery.jms.MetadataSender;
import dk.medcom.edelivery.model.mapping.NotificationMetadataMapper;
import dk.medcom.edelivery.model.mapping.RegisterDocumentSetMetadataMapper;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    MetadataService registrationService(RegisterDocumentService registerDocumentService, MetadataSender publisher, Destinations destinations, RegisterDocumentSetMetadataMapper mapper) {
        var delegate = new GeneralMetadataService(registerDocumentService::registerDocument, mapper::toSubmitObjectsRequest);
        return new MessagingMetadataService(delegate, publisher, new ActiveMQQueue(destinations.getMetadataRegisterOutQueue()));
    }

    @Bean
    MetadataService notificationMetadataService(NotificationService notificationService, MetadataSender publisher, Destinations destinations) {
        var delegate = new GeneralMetadataService(notificationService::send, NotificationMetadataMapper::toNotification);
        return new MessagingMetadataService(delegate, publisher, new ActiveMQQueue(destinations.getMetadataNotifyOutQueue()));
    }
}
