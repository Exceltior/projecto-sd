package model.RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import model.data.*;


////
//	This is the model.RMI Server Interface, that will allow the TCP Servers to interact with the model.RMI and execute its remote methods
////
public interface RMI_Interface extends Remote {
    public void invalidateFacebookToken(int uid)throws RemoteException;
    public void updateFacebookToken(int uid,String facebookToken)throws RemoteException;
    public String getFacebookUserIdFromToken(String token) throws RemoteException;
    public int login(String user, String pwd) throws  RemoteException;
    public int facebookLogin(String idFacebook) throws RemoteException;
    public String getIdeaFacebookId(int iid) throws RemoteException;
    //public int canLogin(String user, String pwd) throws  RemoteException;
    public boolean register(String user, String pass, String email) throws RemoteException;
    public int registerWithFacebook(String token) throws RemoteException;
    public boolean associateWithFacebook(int uid, String token) throws RemoteException;
    public ServerTopic[] getTopics() throws RemoteException;
    public ServerTopic[] getTopics(String title) throws RemoteException;
    public boolean createTopic(String nome, int uid) throws  RemoteException;
    public Idea[] getAllIdeas() throws RemoteException;
    public Idea[] getIdeasFromTopic(int uid, int tid) throws RemoteException;
    public Idea[] getIdeasFromUser(int uid) throws RemoteException;
    public int removeIdea(Idea idea, int uid) throws  RemoteException;
    public BuySharesReturn buyShares(int uid, int iid, float maxPricePerShare, int buyNumShares,
                                     boolean addToQueueOnFailure,
                                     float targetSellPrice) throws RemoteException;
    public Idea getIdeaByIID(int iid, int uid) throws RemoteException;
    public Idea[] searchIdeas(int uid, int iid, String title) throws RemoteException;
    public ServerTopic getTopic(int tid, String name) throws RemoteException;
    public ServerTopic[] getIdeaTopics(int iid) throws RemoteException;
    public int createIdea(String title, String description, int uid,float moneyInvested,ArrayList<String> topics,NetworkingFile file) throws RemoteException;
    public TransactionHistoryEntry[] getHistory(int iid) throws RemoteException;
    public Share getSharesIdeaForUid(int iid, int uid) throws RemoteException;
    public boolean updateUserTime(int uid) throws RemoteException;
    public boolean addFile(int iid, NetworkingFile file) throws RemoteException;
    public NetworkingFile getFile(int iid) throws RemoteException;
    boolean sayTrue() throws RemoteException;
    public boolean setPricesShares(int iid, int uid, float price) throws RemoteException;
    public String getUsername(int uid) throws RemoteException;
    public Idea[] getIdeasFromWatchList(int uid) throws RemoteException;
    public String getTopicTitle(int tid) throws RemoteException;
    public float getUserMoney(int uid) throws RemoteException;
    public boolean getAdminStatus(int uid) throws RemoteException;
    public boolean addIdeaToWatchlist(int iid, int uid) throws RemoteException;
    public Idea[] getHallOfFameIdeas() throws RemoteException;
    public String getFacebookUsernameFromToken(String token) throws RemoteException;
    public boolean removeIdeaFromWatchlist(int iid, int uid) throws RemoteException;
    public boolean isFacebookAccount(int uid) throws RemoteException;
    public void addCallbackToUid(int uid, RMINotificationCallbackInterface c) throws RemoteException;
    public float getMarketValue(int iid) throws RemoteException;
    public boolean takeOver(int iid) throws RemoteException;
    public String getFacebookUserId(int uid) throws RemoteException;
}
