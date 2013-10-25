import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * We have this class so that there is a common interface (RMIConnection) that all clients and threads can use to
 * access the RMI. We can add reconnect stuff and the likes right here.
 */
public class RMIConnection extends Thread {
    private Registry RMIregistry = null;
    private RMI_Interface RMIInterface = null;
    private static final int RMILongReconnectSleepTime = 5000;
    boolean isDown = false;
    private final Object isDownLock = new Object();

    public void waitUntilRMIIsUp() {
        System.out.println("Waiting for RMI to be back up...");
        synchronized (isDownLock) {
            while ( isDown )
                try { isDownLock.wait(); } catch (InterruptedException e) {}
        }System.out.println("RMI up!!");

    }

    public void run() {
        for (;;) {
            synchronized (isDownLock) {
                while ( !isDown )
                    try { isDownLock.wait(); } catch (InterruptedException e) {}

                try { Thread.sleep(RMILongReconnectSleepTime); } catch (InterruptedException e) {}

                //It's down, try to fix it
                for(;;) {
                    System.out.println("Long RMI reconnect thread...");
                    if ( connect() )
                        break;
                    try { Thread.sleep(RMILongReconnectSleepTime); } catch (InterruptedException e) {}
                }

                System.out.println("RMI is back up!");
            }
        }
    }

    synchronized boolean establishConnectionToRegistry() {
        try {
            RMIregistry = LocateRegistry.getRegistry(7000);
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

    synchronized RMI_Interface getRMIInterface() {
        return this.RMIInterface;
    }

    synchronized boolean connect() {
        System.out.println("Trying connection to RMI...");
        boolean val = false;
        int count=0;
        do {
            val = establishConnectionToRegistry(); count++;
            System.out.println("Attempt "+count+"..."+(val ? "Success!" : "Failed!"));
            // sleep? FIXME
            try { Thread.sleep(count*1000); } catch (InterruptedException e) {}
        } while ( !val && count < 3);


        if ( !val ) {
            synchronized (isDownLock) {
                isDown = true;
                isDownLock.notifyAll();
            }
        }

        return val;
    }

    public void onRMIFailed() {
        connect();
    }
}
