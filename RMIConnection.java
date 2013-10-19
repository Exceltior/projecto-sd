import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * We have this class so that there is a common interface (RMIConnection) that all clients and threads can use to
 * access the RMI. We can add reconnect stuff and the likes right here.
 */
public class RMIConnection {
    private Registry RMIregistry = null;
    private RMI_Interface RMIInterface = null;

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
}
