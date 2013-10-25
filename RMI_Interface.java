import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.util.ArrayList;


////
//	This is the RMI Server Interface, that will allow the TCP Servers to interact with the RMI and execute its remote methods
////
public interface RMI_Interface extends Remote {
    public int login(String user, String pwd) throws  RemoteException;
    public int canLogin(String user, String pwd) throws  RemoteException;
    public boolean register(String user, String pass, String email, String date) throws RemoteException;
    public ServerTopic[] getTopics() throws RemoteException;
    public boolean createTopic(String nome, String descricao, int uid) throws  RemoteException;
    public Idea[] getIdeasFromTopic(int tid) throws RemoteException;
    public Idea[] getIdeasFromUser(int uid) throws RemoteException;
    public int removeIdea(Idea idea, int uid) throws  RemoteException;
    public Idea getIdeaByIID(int iid) throws RemoteException;
    public Idea[] getIdeaByIID(int iid, String title) throws RemoteException;;
    public ServerTopic getTopic(int tid, String name) throws RemoteException;
    public ServerTopic[] getIdeaTopics(int iid) throws RemoteException;
    public int createIdea(String title, String description, int uid) throws RemoteException;
    public boolean setTopicsIdea(int iid, String topicTitle, int uid) throws RemoteException;
    public String[] getHistory(int iid) throws RemoteException;
    public boolean setIdeasRelations(int iidpai,int idfilho, int tipo) throws RemoteException;
    public void writeRequestQueueFile(ArrayList<Request> queue) throws RemoteException;
    public ArrayList<Request> readRequestsFromQueueFile() throws RemoteException;
    public ArrayList<Share> getSharesIdea(int iid) throws RemoteException;
    public Share getSharesIdeaForUid(int iid, int uid) throws RemoteException;
    public void updateUserTime(int uid) throws RemoteException;
    public boolean addFile(int iid, NetworkingFile file) throws RemoteException;
    public NetworkingFile getFile(int iid) throws RemoteException;
    public void setSharesIdea(int uid, int iid, int nshares, int price, int numMinShares)throws RemoteException;
    boolean tryGetSharesIdea(int uid, int iid, int numShares, int targetPrice,
                             int minTargetShares) throws RemoteException;
    public ArrayList<Notification> readNotificationsFromQueueFile(int uid) throws RemoteException;
    public boolean writeNotificationsQueueFile(ArrayList<Notification> notifications, int uid) throws
            RemoteException;
    public Idea[] getIdeaRelations(int iid, int relationshipType) throws RemoteException;
    public String[] getIdeaShares(int iid,int uid) throws RemoteException;
    public boolean setPricesShares(int iid, int uid, int price) throws RemoteException;
    public int getSharesNotSell(int iid,int uid) throws RemoteException;
    public boolean setSharesNotSell(int iid, int uid, int numberShares)throws RemoteException;
    public Idea[] getFilesIdeas()throws RemoteException;

    public String getUsername(int uid) throws RemoteException;
    public boolean registerGetSharesRequest(int uid, int iid, int numShares, int targetPrice,
                                         int minTargetShares)  throws RemoteException;
    public ArrayList<Idea> getIdeasCanBuy(int uid) throws RemoteException;
}
