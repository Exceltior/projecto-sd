import java.rmi.RemoteException;
import java.util.ArrayList;


public class RequestQueue extends Thread {
    private final ArrayList<Request> requests = new ArrayList<Request>();
    RMI_Interface RMI;

    /**
     * Builds a request queue. If there was any data on the RMI server, then it is loaded
     * @param RMI
     */
    RequestQueue(RMI_Interface RMI) {
        ArrayList<Request> r = null;
        try {
            r = RMI.readRequestsFromQueueFile();
        } catch (RemoteException e) {
            //FIXME: Retry 3 times here!
        }

        if ( r != null ) {
            for (Request i : r)
                requests.add(i);
        }
    }

    synchronized void enqueueRequest(Request request) {
        int i;

        /* Look for the right place to put it */
        synchronized (requests) {
            for (i = 0; i < requests.size() && requests.get(i).timestamp.compareTo(request.timestamp)<=0; i++) ;

            requests.add(i, request);

            try {
                RMI.writeRequestQueueFile(requests);
            } catch (RemoteException e) {
                //FIXME: Retry 3 times here!
            }

            // Notify the RequestQueue processing thread (not implemented yet)
            requests.notify();
        }
    }

    /**
     * Find the first request that exists in the queue given by this UID
     * @param uid
     * @return The Request, or null if no such request was found
     */
    synchronized Request getFirstRequestByUID(int uid) {
        synchronized (requests) {
            for (Request r : requests)
                if ( r.uid == uid )
                    return r;
        }

        return null;
    }

    /**
     * This function just continuously loops the request list.
     */
    @Override
    public void run() {
        synchronized (requests) {
            while ( requests.isEmpty() ) {
                try {
                    requests.wait();
                } catch (InterruptedException e){}
            }

            //There is at least one request
            for (Request r : requests) {
                // FIXME: Process request r in here!
                // 1. Run the Query
                // 2. Save the Query output in r.queryOutput
                // 3. do r.dispatched = true
                // 4. Do notify()
                // 5. Do RMI.writeRequestQueueFile(requests);
            }
        }
    }

}
