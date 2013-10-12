import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;

////
//	This is the RMI Server Interface, that will allow the TCP Servers to interact with the RMI and execute its remote methods
////
public interface RMI_Interface extends Remote {
    public ArrayList<String[]> ReceiveData(String query) throws RemoteException, SQLException;
    public boolean InsertData(String query) throws RemoteException, SQLException;
    public boolean Login(String user, String pwd) throws  RemoteException, SQLException;
}
