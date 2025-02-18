package dk.medcom.edelivery.util;

import dk.medcom.edelivery.integration.domibus.model.Party;
import dk.medcom.edelivery.integration.domibus.model.Submission;
import domibus.eu.*;

import static dk.medcom.edelivery.TestConstants.PARTY_ID_TYPE;

public class DomibusWSUtil {

    public Messaging mapSubmissionToMessaging(Submission submission) {
        return createMessageHeader(submission.getMessageId(),
                submission.getAction(),
                submission.getService(),
                submission.getServiceType(),
                submission.getFromParties().stream().findFirst().orElse(null),
                submission.getToParties().stream().findFirst().orElse(null),
                submission.getOriginalSender().getPartyId(),
                submission.getFinalRecipient(),
                submission.getAgreementRef());
    }

    Messaging createMessageHeader(String messageId, String actionValue, String serviceValue, String serviceType,
                                  Party fromParty, Party toParty,
                                  String originalSender, Party finalRecipient, String agreementValue) {
        String mimeType = "text/xml";

        Messaging ebMSHeaderInfo = new Messaging();
        UserMessage userMessage = new UserMessage();

        if (messageId != null) {
            var messageInfo = new MessageInfo();
            messageInfo.setMessageId(messageId);
            userMessage.setMessageInfo(messageInfo);
        }

        CollaborationInfo collaborationInfo = new CollaborationInfo();
        collaborationInfo.setAction(actionValue);
        Service service = new Service();
        service.setValue(serviceValue);
        service.setType(serviceType);
        collaborationInfo.setService(service);
        AgreementRef agreementRef = new AgreementRef();
        agreementRef.setValue(agreementValue);
        collaborationInfo.setAgreementRef(agreementRef);
        userMessage.setCollaborationInfo(collaborationInfo);
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getProperty().add(createProperty("originalSender", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:" + originalSender, null));
        messageProperties.getProperty().add(createProperty("finalRecipient", finalRecipient.getPartyId(), "iso6523-actorid-upis"));
        userMessage.setMessageProperties(messageProperties);
        if (fromParty != null || toParty != null) {
            PartyInfo partyInfo = new PartyInfo();
            if (fromParty != null) {
                From from = new From();
                from.setRole("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/initiator");
                PartyId fromPartyId = new PartyId();
                fromPartyId.setType(PARTY_ID_TYPE);
                fromPartyId.setValue(fromParty.getPartyId());
                from.setPartyId(fromPartyId);
                partyInfo.setFrom(from);
            }
            if (toParty != null) {
                To to = new To();
                to.setRole("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/responder");
                PartyId receiver = new PartyId();
                receiver.setType(PARTY_ID_TYPE);
                receiver.setValue(toParty.getPartyId());
                to.setPartyId(receiver);
                partyInfo.setTo(to);
            }
            userMessage.setPartyInfo(partyInfo);
        }
        PayloadInfo payloadInfo = new PayloadInfo();
        PartInfo partInfo = new PartInfo();
        partInfo.setHref("cid:message");
        if (mimeType != null) {
            PartProperties partProperties = new PartProperties();
            partProperties.getProperty().add(createProperty("MimeType", mimeType, null));
            partInfo.setPartProperties(partProperties);
        }
        payloadInfo.getPartInfo().add(partInfo);
        userMessage.setPayloadInfo(payloadInfo);
        ebMSHeaderInfo.setUserMessage(userMessage);
        return ebMSHeaderInfo;
    }

    Property createProperty(String name, String value, String type) {
        Property aProperty = new Property();
        aProperty.setValue(value);
        aProperty.setName(name);
        aProperty.setType(type);
        return aProperty;
    }


}
