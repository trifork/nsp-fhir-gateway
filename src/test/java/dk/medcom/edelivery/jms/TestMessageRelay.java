package dk.medcom.edelivery.jms;

import dk.medcom.edelivery.model.Metadata;
import dk.medcom.edelivery.service.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.transaction.annotation.Transactional;

import static dk.medcom.edelivery.jms.JmsConfiguration.METADATA_LISTENER_CONTAINER_FACTORY;

@TestComponent
public class TestMessageRelay {

    @Autowired
    MetadataService failingService;

    @Autowired
    MetadataService successService;

    @Autowired
    MetadataService failPublishService;

    private boolean receivedFail = false;
    private boolean receivedSuccess = false;

    @Transactional
    @JmsListener(destination = "send.fail", containerFactory = METADATA_LISTENER_CONTAINER_FACTORY)
    public void sendFail(Metadata metadata) {
        System.out.println("TestMessageRelay.sendFail");
        failingService.publish(metadata);
    }

    @Transactional
    @JmsListener(destination = "send.success", containerFactory = METADATA_LISTENER_CONTAINER_FACTORY)
    public void sendSuccess(Metadata metadata) {
        System.out.println("TestMessageRelay.sendSuccess");
        successService.publish(metadata);
    }

    @Transactional
    @JmsListener(destination = "send.fail-publish", containerFactory = METADATA_LISTENER_CONTAINER_FACTORY)
    public void sendFailPublish(Metadata metadata) {
        System.out.println("TestMessageRelay.sendFailPublish");
        failPublishService.publish(metadata);
    }

    @Transactional
    @JmsListener(destination = "receive.fail", containerFactory = METADATA_LISTENER_CONTAINER_FACTORY)
    public void receiveFail(Metadata metadata) {
        System.out.println("TestMessageRelay.receiveFail");
        receivedFail = true;
    }

    @Transactional
    @JmsListener(destination = "receive.success", containerFactory = METADATA_LISTENER_CONTAINER_FACTORY)
    public void receiveSuccess(Metadata metadata) {
        System.out.println("TestMessageRelay.receiveSuccess");
        receivedSuccess = true;
    }

    public void reset() {
        this.receivedFail = false;
        this.receivedSuccess = false;
    }

    public boolean isReceivedFail() {
        return receivedFail;
    }

    public boolean isReceivedSuccess() {
        return receivedSuccess;
    }
}
