import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;

////
//	This is the RMI Server Interface, that will allow the TCP Servers to interact with the RMI and execute its remote methods
////
public interface RMI_Interface extends Remote {
    public ArrayList<String[]> receiveData(String query) throws RemoteException;
    public boolean insertData(String query) throws RemoteException, SQLException;
    public int login(String user, String pwd) throws  RemoteException;
    public ServerTopic[] getTopics() throws RemoteException;
}
