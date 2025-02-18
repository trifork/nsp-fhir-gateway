package dk.medcom.edelivery.integration.nas;

import dk.nsi.advis.v10.NotifyContent;
import org.oasis_open.docs.wsn.b_2.NotificationMessageHolderType;
import org.oasis_open.docs.wsn.b_2.Notify;
import org.oasis_open.docs.wsn.b_2.TopicExpressionType;

public class NotificationMapper {

    public static final String ID_TYPE_CPR = "http://nsi.dk/advis/v10/CPR";
    public static final String SIMPLE_DIALECT_URI = "http://docs.oasis-open.org/wsn/t-1/TopicExpression/Simple";
    public static final String TOPIC = "http://medcom.dk/messagecommunication/2020/11/05:NewMessage";

    public static Notify toNotify(Notification notification) {
        // Unable to generate fluent API with CXF/wsdl2java
        Notify notify = new Notify();
        NotificationMessageHolderType messageHolder = new NotificationMessageHolderType();
        NotificationMessageHolderType.Message message = new NotificationMessageHolderType.Message();
        NotifyContent notifyContent = new NotifyContent();
        notifyContent.setId(notification.getPersonIdentifier().getValue());
        notifyContent.setIdType(ID_TYPE_CPR);
        notifyContent.setIsSystemNotification(false);
        notifyContent.setAny(notification);
        message.setAny(notifyContent);
        messageHolder.setMessage(message);
        TopicExpressionType topic = new TopicExpressionType();
        topic.setDialect(SIMPLE_DIALECT_URI);
        topic.getContent().add(TOPIC);
        messageHolder.setTopic(topic);
        notify.getNotificationMessage().add(messageHolder);
        return notify;
    }

    private NotificationMapper() {
    }

}
