import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


////
//	This is the RMI Server Interface, that will allow the TCP Servers to interact with the RMI and execute its remote methods
////
public interface RMI_Interface extends Remote {
    public int login(String user, String pwd) throws  RemoteException;
    public boolean register(String user, String pass, String email, String date) throws RemoteException;
    public ServerTopic[] getTopics() throws RemoteException;
    public boolean createTopic(String nome, String descricao, int uid) throws  RemoteException;
    public Idea[] getIdeasFromTopic(int tid) throws RemoteException;
    public Idea[] getIdeasFromUser(int uid) throws RemoteException;
    public boolean addParentTopicsToIdea(Idea idea) throws  RemoteException;
    public boolean addParentIdeasToIdea(Idea idea) throws  RemoteException;
    public boolean addChildrenIdeasToIdea(Idea idea) throws  RemoteException;
    public boolean removeIdea(Idea idea) throws  RemoteException;
    public Idea getIdeaByIID(int iid) throws RemoteException;
    public Idea[] getIdeaByIID(int iid, String title) throws RemoteException;
    public Idea getIdeaByTitle(String title) throws RemoteException;
    public ServerTopic getTopic(int tid, String name) throws RemoteException;
    public ServerTopic[] getIdeaTopics(int iid) throws RemoteException;
    public int createIdea(String title, String description, int uid) throws RemoteException;
    public boolean setSharesIdea(int uid, int iid,int nshares, int price, int numMinShares)throws RemoteException;
    public boolean setTopicsIdea(int iid, String topicTitle, int uid) throws RemoteException;
    public String[] getHistory(int iid) throws RemoteException;
    public boolean setIdeasRelations(int iidpai,int idfilho, int tipo) throws RemoteException;
    public void writeRequestQueueFile(ArrayList<Request> queue) throws RemoteException;
    public ArrayList<Request> readRequestsFromQueueFile() throws RemoteException;
}
