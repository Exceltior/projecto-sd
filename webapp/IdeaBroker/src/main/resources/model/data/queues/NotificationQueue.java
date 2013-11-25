package model.data.queues;

import model.RMI.RMI_Interface;

public class NotificationQueue extends OrderedTimestampQueue<Notification> {
    private RMI_Interface RMI;
    private int           uid;



    public synchronized void enqueue(Notification notification) {
        int i;

        /* Look for the right place to put it */
        synchronized (queue) {
            super.enqueue(notification);


        }
    }

}
