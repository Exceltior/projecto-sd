import java.rmi.Remote;
import java.rmi.RemoteException;


////
//	This is the RMI Server Interface, that will allow the TCP Servers to interact with the RMI and execute its remote methods
////
public interface RMI_Interface extends Remote {
    public int login(String user, String pwd) throws  RemoteException;
    public boolean register(String user, String pass, String email, String date) throws RemoteException;
    public ServerTopic[] getTopics() throws RemoteException;
    public boolean createTopic(String nome, String descricao, int uid) throws  RemoteException;
    public Idea[] getIdeasFromTopic(int tid) throws RemoteException;
    public boolean addParentTopicsToIdea(Idea idea) throws  RemoteException;
    public boolean addParentIdeasToIdea(Idea idea) throws  RemoteException;
    public boolean addChildrenIdeasToIdea(Idea idea) throws  RemoteException;
    public Idea getIdeaByIID(int iid) throws RemoteException;
}
