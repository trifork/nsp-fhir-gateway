package dk.medcom.edelivery.model.mapping;

import dk.medcom.edelivery.integration.nas.Notification;
import dk.medcom.edelivery.integration.nas.PersonIdentifier;
import dk.medcom.edelivery.model.Metadata;

public class NotificationMetadataMapper {

    private NotificationMetadataMapper() {
    }

    public static Notification toNotification(Metadata metadata) {
        return new Notification()
                .setPersonIdentifier(new PersonIdentifier().setStandard("1.2.208.176.1.2").setValue(metadata.getCpr()))
                .setDocumentId(metadata.getExchangeInfo().getDocumentId())
                .setOriginalSender(metadata.getAddressingInfo().getSender())
                .setFinalRecipient(metadata.getAddressingInfo().getSender())
                .setSenderId(metadata.getExchangeInfo().getSenderId())
                .setReceiverId(metadata.getExchangeInfo().getReceiverId())
                .setStandard(metadata.getDocumentInfo().getStandard())
                .setType(metadata.getDocumentInfo().getType())
                .setTypeVersion(metadata.getDocumentInfo().getTypeVersion());
    }
}
