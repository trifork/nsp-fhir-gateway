package dk.medcom.edelivery.integration.nas;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.oasis_open.docs.wsn.b_2.Notify;
import org.oasis_open.docs.wsn.bw_2.InvalidTopicExpressionFault;
import org.oasis_open.docs.wsn.bw_2.NotificationBroker;
import org.oasis_open.docs.wsn.bw_2.NotifyFailedFault;

public class NotificationService {

    private static final Logger log = LogManager.getLogger(NotificationService.class);

    private final NotificationBroker notificationBroker;

    public NotificationService(NotificationBroker notificationBroker) {
        this.notificationBroker = notificationBroker;
    }

    public void send(Notification notification) {
        sendNotification(NotificationMapper.toNotify(notification));
    }

    private void sendNotification(Notify notify) {
        long start = System.currentTimeMillis();
        try {
            log.info("Notifying NAS...");
            notificationBroker.notify(notify);
            log.info("NAS notify done, duration = {} ms", (System.currentTimeMillis() - start));
        } catch (InvalidTopicExpressionFault | NotifyFailedFault e) {
            log.info("NAS notify failed, duration = {} ms", (System.currentTimeMillis() - start));
            throw new IllegalStateException(e);
        }
    }
}
