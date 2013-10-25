import java.rmi.RemoteException;
import java.util.ArrayList;


public class RequestQueue extends OrderedTimestampQueue<Request> implements Runnable {
    private RMI_Interface RMI;

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
                queue.add(i);
        }
    }

    synchronized void enqueueRequest(Request request) {
        int i;

        /* Look for the right place to put it */
        synchronized (queue) {
            super.enqueue(request);

            try {
                RMI.writeRequestQueueFile(queue);
            } catch (RemoteException e) {
                //FIXME: Retry 3 times here!
            }

            queue.notify();
        }
    }

    /**
     * Find the first request that exists in the queue given by this UID
     * @param uid
     * @return The Request, or null if no such request was found
     */
    synchronized Request getFirstRequestByUID(int uid) {
        synchronized (queue) {
            for (Request r : queue)
                if ( r.uid == uid )
                    return r;
        }

        return null;
    }

    /**
     * Find the first request that exists in the queue given by this UID and type
     * @param uid
     * @paam type
     * @return The Request, or null if no such request was found
     */
    synchronized Request getFirstRequestByUIDAndType(int uid, Request.RequestType type) {
        return getNthRequestByUIDAndType(uid, type, 1);
    }

    /**
     * Find the first request that exists in the queue given by this UID and type
     * @param uid
     * @paam type
     * @return The Request, or null if no such request was found
     */
    synchronized Request getNthRequestByUIDAndType(int uid, Request.RequestType type, int n) {
        synchronized (queue) {
            for (Request r : queue)
                if ( r.uid == uid && r.requestType == type)
                    if ( --n == 0 ) return r;
        }

        return null;
    }

    /**
     * Use this to aknowledge a request (right after you chang the NEED_NOTIFY variable of a user's request)
     * @param r
     */
    synchronized void dequeue(Request r) {
        synchronized (queue) {
            super.dequeue(r);
            try {
                RMI.writeRequestQueueFile(queue);
            } catch (RemoteException e) {
                //FIXME: Talvez fazer isto 3 vezes!
            }
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
        boolean autoDequeue = false;
        for(;;) {


            // Wait until there's at least one request
            synchronized (queue) {
                while ( queue.isEmpty() ) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e){}
                }

                //There is at least one request, process them
               for (int i = 0; i < queue.size(); i++) {
                    Request r = queue.get(i);

                   /* If this request has been handled already (and is thus waiting for a dequeue() and clear of user
                   NEED_NOTIFY, skip it */
                    if ( r.dispatched ) continue;
                    try {
                        if ( r.requestType == Request.RequestType.LOGIN ) {
                                int ans = RMI.login((String)r.requestArguments.get(0), (String)r.requestArguments.get(1));
                                r.requestResult.add(ans);
                        } else if ( r.requestType == Request.RequestType.HISTORY ) {
                                String[] ans = RMI.getHistory((Integer) r.requestArguments.get(0));
                                r.requestResult.add(ans);
                        } else if ( r.requestType == Request.RequestType.DELETE_IDEA ) {
                            int ans = RMI.removeIdea((Idea) r.requestArguments.get(0),
                                    (Integer) r.requestArguments.get(1));
                            r.requestResult.add(ans);
                        } else if ( r.requestType == Request.RequestType.CREATE_IDEA ) {
                            int ans = RMI.createIdea((String)r.requestArguments.get(0),
                                    (String)r.requestArguments.get(1),
                                    (Integer)r.requestArguments.get(2));
                            r.requestResult.add(ans);
                        } else if ( r.requestType == Request.RequestType.SET_SHARES_IDEA ) {
                            boolean ans = RMI.setSharesIdea((Integer) r.requestArguments.get(0),
                                    (Integer) r.requestArguments.get(1),
                                    (Integer) r.requestArguments.get(2),
                                    (Integer) r.requestArguments.get(3),
                                    (Integer) r.requestArguments.get(4));
                            r.requestResult.add(ans);
                        } else if ( r.requestType == Request.RequestType.SET_TOPICS_IDEA ) {
                            boolean ans = RMI.setTopicsIdea((Integer) r.requestArguments.get(0),
                                    (String) r.requestArguments.get(1),
                                    (Integer) r.requestArguments.get(2));
                            r.requestResult.add(ans);
                        } else if ( r.requestType == Request.RequestType.SET_IDEAS_RELATIONS ) {
                            boolean ans = RMI.setIdeasRelations((Integer) r.requestArguments.get(0),
                                    (Integer) r.requestArguments.get(1),
                                    (Integer) r.requestArguments.get(2));
                            r.requestResult.add(ans);
                        } else if ( r.requestType == Request.RequestType.CREATE_TOPIC ) {
                            boolean ans = RMI.createTopic((String) r.requestArguments.get(0),
                                    (String) r.requestArguments.get(1),
                                    (Integer) r.requestArguments.get(2));
                            r.requestResult.add(ans);
                        } else if ( r.requestType == Request.RequestType.REGISTER_USER ) {
                            boolean ans = RMI.register((String) r.requestArguments.get(0),
                                    (String) r.requestArguments.get(1),
                                    (String) r.requestArguments.get(2),
                                    (String) r.requestArguments.get(3));
                            r.requestResult.add(ans);
                            autoDequeue = true; // We instantly remove it as soon as we process it
                        }  else if (r.requestType == Request.RequestType.ADD_FILE){
                            boolean ans = RMI.addFile((Integer) r.requestArguments.get(0),
                                    (NetworkingFile) r.requestArguments.get(1));
                            r.requestResult.add(ans);
                            autoDequeue = true; // We instantly remove it as soon as we process it
                        }
                    } catch (RemoteException e) {
                     //FIXME: talvez fazer isto 3 vezes!
                    }


                   /* Mark the request as dispatched, notify anyone waiting for it,
                    *  and continue working (the others are responsible for removing the request from the queue
                    */
                    r.dispatched = true;
                    synchronized(r) {
                        r.notify();
                    }

                   if ( autoDequeue ) {
                       autoDequeue = false;
                       dequeue(r);
                   }

                    try {
                        RMI.writeRequestQueueFile(queue);
                    } catch (RemoteException e) {
                        //FIXME: Talvez fazer isto 3 vezes!
                    }

                }
            }
        }
    }

}
