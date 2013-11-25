package model.data.queues;

import model.data.queues.TimestampClass;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class OrderedTimestampQueue<E extends TimestampClass> {
    protected final ArrayList<E> queue = new ArrayList<E>();

    synchronized E getNextElement() {
        synchronized (queue) {
            if ( queue.size() == 0 )
                return null;
            else {
                E n = queue.get(0);
                return n;
            }
        }
    }

    public synchronized void dequeue(E r) {
        synchronized (queue) {
            queue.remove(r);
        }
    }

    public synchronized void enqueue(E notification) {
        int i;

        /* Look for the right place to put it */
        synchronized (queue) {
            for (i = 0; i < queue.size() && queue.get(i).getTimestamp().compareTo(notification.getTimestamp())<=0;
                 i++) ;

            queue.add(i, notification);
        }
    }
}
