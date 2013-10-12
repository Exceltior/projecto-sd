import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.sql.SQLException;

public class RMI_Client {

	////
	//	This class will only be used for testing purposes and in the final version will be replaced by the TCP Servers!!
	////

	public static void main(String args[]) {
		try {
			//Start RMIRegistry programmatically
			RMI_Interface s = (RMI_Interface) LocateRegistry.getRegistry(7000).lookup("academica");

			String query = "Select * from Utilizadores", result;

			result = s.ReceiveData(query);

			System.out.println(result);
			
		} catch (RemoteException r) {
			System.out.println("RemoteException in main: " + r);
			//e.printStackTrace();
		} catch (SQLException s){
            System.out.println("SQL Exception in main " + s);
        } catch (NotBoundException n){
            System.out.println("NotBound Exception in main " + n);
        }
	}
}