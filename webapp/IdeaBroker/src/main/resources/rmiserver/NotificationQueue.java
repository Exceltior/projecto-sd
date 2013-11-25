package rmiserver;

import model.RMI.RMI_Interface;
import model.data.queues.Notification;

public class NotificationQueue extends OrderedTimestampQueue<Notification> {
    private RMI_Interface RMI;
    private int           uid;



    synchronized void enqueue(Notification notification) {
        int i;

        /* Look for the right place to put it */
        synchronized (queue) {
            super.enqueue(notification);


        }
    }

}
