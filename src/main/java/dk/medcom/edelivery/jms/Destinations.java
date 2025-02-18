package dk.medcom.edelivery.jms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Destinations {

    public static final String DOMIBUS_OUT_QUEUE_EXP = "${destinations.domibus.out.queue:domibus.backend.jms.outQueue}";
    public static final String DOMIBUS_REPLY_QUEUE_EXP = "${destinations.domibus.reply.queue:domibus.backend.jms.replyQueue}";
    public static final String DOMIBUS_IN_QUEUE_EXP = "${destinations.domibus.in.queue:domibus.backend.jms.inQueue}";

    public static final String METADATA_REGISTER_IN_QUEUE_EXP = "${destinations.metadata.register.in.queue:repository.metadata.register}";
    public static final String METADATA_REGISTER_OUT_QUEUE_EXP = "${destinations.metadata.register.out.queue:repository.metadata.notify}";

    public static final String METADATA_NOTIFY_IN_QUEUE_EXP = "${destinations.metadata.notify.in.queue:repository.metadata.notify}";
    public static final String METADATA_NOTIFY_OUT_QUEUE_EXP = "${destinations.metadata.notify.out.queue:repository.metadata.receipt}";

    public static final String METADATA_RECEIPT_IN_QUEUE_EXP = "${destinations.metadata.receipt.in.queue:repository.metadata.receipt}";

    @Value(DOMIBUS_OUT_QUEUE_EXP)
    private String domibusOutQueue;

    @Value(DOMIBUS_REPLY_QUEUE_EXP)
    private String domibusReplyQueue;

    @Value(DOMIBUS_IN_QUEUE_EXP)
    private String domibusInQueue;

    @Value(METADATA_REGISTER_IN_QUEUE_EXP)
    private String metadataRegisterInQueue;

    @Value(METADATA_REGISTER_OUT_QUEUE_EXP)
    private String metadataRegisterOutQueue;

    @Value(METADATA_NOTIFY_IN_QUEUE_EXP)
    private String metadataNotifyInQueue;

    @Value(METADATA_NOTIFY_OUT_QUEUE_EXP)
    private String metadataNotifyOutQueue;

    @Value(METADATA_RECEIPT_IN_QUEUE_EXP)
    private String metadataReceiptInQueue;

    public String getDomibusOutQueue() {
        return domibusOutQueue;
    }

    public String getDomibusReplyQueue() {
        return domibusReplyQueue;
    }

    public String getDomibusInQueue() {
        return domibusInQueue;
    }

    public String getMetadataRegisterInQueue() {
        return metadataRegisterInQueue;
    }

    public String getMetadataRegisterOutQueue() {
        return metadataRegisterOutQueue;
    }

    public String getMetadataNotifyInQueue() {
        return metadataNotifyInQueue;
    }

    public String getMetadataNotifyOutQueue() {
        return metadataNotifyOutQueue;
    }

    public String getMetadataReceiptInQueue() {
        return metadataReceiptInQueue;
    }
}
