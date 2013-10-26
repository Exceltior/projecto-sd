import java.rmi.RemoteException;
import java.util.ArrayList;

class NotificationQueue extends OrderedTimestampQueue<Notification> {
    private final RMI_Interface RMI;
    private final int uid;

    /**
     * Builds a request queue. If there was any data on the RMI server, then it is loaded
     * @param RMI
     */
    NotificationQueue(RMI_Interface RMI, int uid) {
        this.RMI = RMI;
        this.uid = uid;
        checkRMI(null);
    }

    synchronized Notification getNextNotification() {
        return getNextElement();
    }

    synchronized void dequeue(Notification r, Server server) {
        synchronized (queue) {
            for(;;) {
                super.dequeue(r);
                try {
                    RMI.writeNotificationsQueueFile(queue, uid);
                    break;
                } catch (RemoteException e) {
                    server.getConnection().onRMIFailed();
                    server.killSockets();
                }
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
            } catch (RemoteException ignored) {} //Will never happen because only the RMI calls this

        }
    }

    synchronized public void checkRMI(Server server) {
        ArrayList<Notification> r = null;

        try {
            if (server != null)
                r = server.getConnection().getRMIInterface().readNotificationsFromQueueFile(uid);
            else
                r = RMI.readNotificationsFromQueueFile(uid);
        } catch (RemoteException e) {
            if ( server != null ) {
                server.getConnection().onRMIFailed();
                server.killSockets();
            }
        }

        if ( r != null && r.size() > 0 )
            for (Notification i : r)
                queue.add(i);
    }
}
