import java.rmi.RemoteException;
import java.util.ArrayList;

public class NotificationQueue extends OrderedTimestampQueue<Notification> {
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
        return getNextElement();
    }

    synchronized void dequeue(Notification r) {
        synchronized (queue) {
            super.dequeue(r);
            try {
                RMI.writeNotificationsQueueFile(queue, uid);
            } catch (RemoteException e) {
                //FIXME: Talvez fazer isto 3 vezes!
            }
        }
    }

    synchronized void enqueue(Notification notification) {
        int i;

        /* Look for the right place to put it */
        synchronized (queue) {
            super.enqueue(notification);

            try {
                RMI.writeNotificationsQueueFile(queue, uid);
            } catch (RemoteException e) {
                //FIXME: Retry 3 times here!
            }

        }
    }

    synchronized public void checkRMI() {
        ArrayList<Notification> r = null;
        try {
            r = RMI.readNotificationsFromQueueFile(uid);
        } catch (RemoteException e) {
            //FIXME: Retry 3 times here!
        }

        if ( r != null ) {
            System.out.println("There are notifications!");
            for (Notification i : r) {
                queue.add(i);
                System.out.println("Adding not: "+i);
            }
        }
    }
}
