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
        this.RMI = RMI;
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
     * Use this to aknowledge a request (right after you chang the NEED_NOTIFY variable of a user's request)
     * @param r
     */
    synchronized void dequeue(Request r) {
        synchronized (requests) {
            requests.remove(r);
        }

    }

    /**
     * This function just continuously loops the request list.
     * * FIXME: We can only do something for a user if that user is NOT in the NEED_NOTIFY state. If it is in that
     * state, then we need to wait until we notify the user. If we find something to do and it's the user is in
     * NEED_NOTIFY, we will sleep for a while (like 3 seconds or so) to give the user time to reconnect and check one
     * of the dispatched requests
     * --> This means we need a function to explicitly remove requests
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
           for (int i = 0; i < requests.size(); i++) {
                Request r = requests.get(i);

               /* If this request has been handled already (and is thus waiting for a dequeue() and clear of user
               NEED_NOTIFY */
                if ( r.dispatched ) continue;
                try {
                    if ( r.requestType == Request.RequestType.LOGIN ) {

                            int ans = RMI.login((String)r.requestArguments.get(0), (String)r.requestArguments.get(1));
                            r.requestResult.add(ans);
                    }
                } catch (RemoteException e) {
                 //FIXME: talvez fazer isto 3 vezes!
                }

                r.dispatched = true;
                synchronized(r) {
                    r.notify();
                }
                try {
                    RMI.writeRequestQueueFile(requests);
                } catch (RemoteException e) {
                    //FIXME: Talvez fazer isto 3 vezes!
                }

            }
        }
    }

}
