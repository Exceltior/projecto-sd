import java.rmi.RemoteException;
import java.util.ArrayList;

class OrderedTimestampQueue<E extends TimestampClass> {
    final ArrayList<E> queue = new ArrayList<E>();

    synchronized E getNextElement() {
        synchronized (queue) {
            if ( queue.size() == 0 )
                return null;
            else {
                return queue.get(0);
            }
        }
    }

    synchronized void dequeue(E r) {
        synchronized (queue) {
            queue.remove(r);
        }
    }

    synchronized void enqueue(E notification) {
        int i;

        /* Look for the right place to put it */
        synchronized (queue) {
            for (i = 0; i < queue.size() && queue.get(i).getTimestamp().compareTo(notification.getTimestamp())<=0;
                 i++) ;

            queue.add(i, notification);
        }
    }
}
