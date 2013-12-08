package model.RMI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * We have this class so that there is a common interface (RMIConnection) that all clients and threads can use to
 * access the model.RMI. We can add reconnect stuff and the likes right here.
 */
public class RMIConnection {
    private Registry RMIregistry = null;
    private RMI_Interface RMIInterface = null;
    private boolean isDown = false;
    private final Object isDownLock = new Object();
    private final String RMIHost;

    public synchronized void testRMINow() {
        if (RMIInterface == null) {
            isDown = true;
            return;
        }
        try {
            RMIInterface.sayTrue();
        } catch (RemoteException e) {
//            e.printStackTrace();
//            System.out.println(e.getMessage());
//            System.out.println(e.getCause());
            onRMIFailed();
        }
    }

    public synchronized boolean RMIIsDown() {
        return isDown;
    }

    public RMIConnection(String RMIHost) {
        this.RMIHost = RMIHost;
    }

    synchronized public void waitUntilRMIIsUp() {
        //System.out.println("Waiting for model.RMI to be up...");
        testRMINow();
        if ( isDown )
            while (!connect())
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        //System.out.println("model.RMI up!!");
    }

    synchronized boolean establishConnectionToRegistry() {
        try {
            RMIregistry = LocateRegistry.getRegistry(RMIHost, 7000);
            RMIInterface = (RMI_Interface) RMIregistry.lookup("academica");
        } catch (RemoteException e) {
            System.err.println("Remote Exception no RMIConnection!");
            return false;
        } catch (NotBoundException n) {
            System.err.println("NotBoundException no RMIConnection!");
            return false;
        }

        return true;
    }

    synchronized public RMI_Interface getRMIInterface() {
        waitUntilRMIIsUp();
        return this.RMIInterface;
    }

    synchronized boolean connect() {
        System.out.println("Trying connection to model.RMI...");
        boolean val;
        int count=0;
        do {
            val = establishConnectionToRegistry(); count++;
            System.out.println("Attempt "+count+"..."+(val ? "Success!" : "Failed!"));
            try { Thread.sleep(count*1000); } catch (InterruptedException ignored) {}
        } while ( !val && count < 3);


        isDown = !val;

        if ( isDown ) {

            // Kill the sockets, because it wasn't a transient failure
            //FIXME: What to do here? Probably get a new connection...
        }

        return val;
    }

    synchronized public void onRMIFailed() {
        connect();
    }
}
