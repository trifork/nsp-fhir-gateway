package dk.medcom.edelivery.demo;

import dk.medcom.edelivery.model.mapping.SBDUnmarshaller;
import dk.medcom.edelivery.service.RepositoryService;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import static dk.medcom.edelivery.jms.JmsConfiguration.JMS_LISTENER_CONTAINER_FACTORY;

@Component
@Profile("demo")
public class DemoJmsListener {

    public static final String DEMO_QUEUE_EXP = "${destinations.demo.queue:repository.demo}";

    private final RepositoryService repositoryService;

    public DemoJmsListener(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @JmsListener(destination = DEMO_QUEUE_EXP, containerFactory = JMS_LISTENER_CONTAINER_FACTORY)
    public void receiveSBDH(Message message) throws JMSException {

        if (message instanceof TextMessage) {
            String xml = ((TextMessage) message).getText();

            try (var is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))) {
                var dh = new DataHandler(new ByteArrayDataSource(is, "text/xml"));
                repositoryService.persist(SBDUnmarshaller.unmarshal(dh));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

}
