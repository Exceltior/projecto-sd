import java.rmi.RemoteException;
import java.util.ArrayList;

public class NotificationQueue {
    private final ArrayList<Notification> notifications = new ArrayList<Notification>();
    private RMI_Interface RMI;
    private int uid;

    /**
     * Builds a request queue. If there was any data on the RMI server, then it is loaded
     * @param RMI
     */
    NotificationQueue(RMI_Interface RMI, int uid) {
        this.RMI = RMI;
        this.uid = uid;
        checkRMI();
    }

    synchronized Notification getNextNotification() {
        synchronized (notifications) {
            if ( notifications.size() == 0 )
                return null;
            else {
                Notification n = notifications.get(0);
                return n;
            }
        }
    }

    synchronized void dequeue(Notification r) {
        synchronized (notifications) {
            notifications.remove(r);
        }
        try {
            RMI.writeNotificationsQueueFile(notifications, uid);
        } catch (RemoteException e) {
            //FIXME: Talvez fazer isto 3 vezes!
        }
    }

    synchronized void enqueue(Notification notification) {
        int i;

        /* Look for the right place to put it */
        synchronized (notifications) {
            for (i = 0; i < notifications.size() && notifications.get(i).timestamp.compareTo(notification.timestamp)<=0; i++) ;

            notifications.add(i, notification);

            try {
                RMI.writeNotificationsQueueFile(notifications, uid);
            } catch (RemoteException e) {
                //FIXME: Retry 3 times here!
            }

        }
    }

    public void checkRMI() {
        ArrayList<Notification> r = null;
        try {
            r = RMI.readNotificationsFromQueueFile(uid);
        } catch (RemoteException e) {
            //FIXME: Retry 3 times here!
        }

        if ( r != null ) {
            for (Notification i : r)
                notifications.add(i);
        }
    }
}
