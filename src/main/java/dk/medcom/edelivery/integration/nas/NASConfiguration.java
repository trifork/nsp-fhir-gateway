package dk.medcom.edelivery.integration.nas;

import dk.medcom.edelivery.integration.dgws.AugmentedJAXBContextInterceptor;
import dk.medcom.edelivery.integration.dgws.SOAPFaultCorrectingInterceptor;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.frontend.ClientProxy;
import org.oasis_open.docs.wsn.bw_2.NotificationBroker;
import org.oasis_open.docs.wsn.bw_2.NotificationBrokerService;
import org.oasis_open.docs.wsn.bw_2.NotificationConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.xml.ws.BindingProvider;
import java.util.Map;

@Configuration
public class NASConfiguration {

    @Bean
    @Lazy
    NotificationBroker notificationBroker(NASConfigurationProperties properties) {
        var service = new NotificationBrokerService();

        NotificationBroker port = service.getSoap();

        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put("javax.xml.ws.client.connectionTimeout", properties.getConnectTimeoutSeconds() * 1000);
        requestContext.put("javax.xml.ws.client.receiveTimeout", properties.getReadTimeoutSeconds() * 1000);

        var client = ClientProxy.getClient(port);
        client.getInInterceptors().add(new LoggingInInterceptor());
        client.getOutInterceptors().add(new LoggingOutInterceptor());
        client.getOutInterceptors().add(new AugmentedJAXBContextInterceptor());
        client.getInFaultInterceptors().add(new SOAPFaultCorrectingInterceptor());
        client.getInFaultInterceptors().add(new LoggingInInterceptor());

        client.getRequestContext().put(org.apache.cxf.message.Message.ENDPOINT_ADDRESS, properties.getUrl());

        return port;
    }

    @Bean
    NotificationService notificationService(@Lazy NotificationBroker notificationBroker) {
        return new NotificationService(notificationBroker);
    }

}
