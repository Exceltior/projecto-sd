package actions.model;

import model.RMI.RMIConnection;
import model.data.BuySharesReturn;
import model.data.Idea;
import model.data.NetworkingFile;
import model.data.Topic;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Client Bean which acts as our Model. It stores the RMI, the uid and other useful variables associated with the
 * current user session. It is responsible for all interaction with the RMI.
 */
public class Client {
    private final static String RMI_HOST = "localhost";//FIXME: MUDAR ISTO?? NAO SEI SE O PROF QUER VER LOCALHOST NO CODIGO


    private RMIConnection rmi;
    private int           uid;
    private String        username;
    private float         coins;
    private int           numNotifications;
    private boolean       adminStatus;

    public Client() {
        this.rmi = new RMIConnection(RMI_HOST);
        this.uid = -1;
        this.coins = 0;
        this.numNotifications = 0; /* FIXME: On login, set this */
        this.adminStatus = true;
    }

    /**
     * Calls RMI's login safely. We have chosen to encapsulate it so that we can later on (FIXME) implement retry
     * mechanisms. We will need to indicate the calling function if the RMI fails. <-- FIXME
     * @param username User's username
     * @param password User's password
     * @return On success, returns the user's UID. On failure, -1 indicates an error logging in (no such user(pass).
     * FIXME: Possibly include other error codes to indicate RMI failure
     */
    private int doRMILogin(String username, String password) {
        int ret = 0;
        try {
            ret = rmi.getRMIInterface().login(username, password);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Call's RMI register to safely register a new user in the database.
     * @param username  The new user's username
     * @param password  The new user's password
     * @param email     The new user's email
     * @return          A boolean value, indicating the success or failure of the operation
     */
    private boolean doRMIRegister(String username, String password, String email){
        boolean ret = false;
        try{
            ret = rmi.getRMIInterface().register(username, password, email);
        } catch(RemoteException e){
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Calls RMITopics safely.
     * @return  On success, returns an Array of class Topic objects, containing all the topics stored in the database.
     *          On failure, returns null.
     */
    private Topic[] doRMIGetTopics(){
       Topic[] devolve = null;

        try{
           devolve = rmi.getRMIInterface().getTopics();
        }catch (RemoteException e){
            e.printStackTrace();
        }

        return devolve;
    }

    /**
     * Calls RMI's getTopicTitle safely.
     * @return  On success, The topic title. On failure, returns null
     */
    private String doRMIGetTopicTitle(int tid){
        String ret = null;

        try{
            ret = rmi.getRMIInterface().getTopicTitle(tid);
        }catch (RemoteException e){
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Gets all the ideas in a given topic safely, using RMI.
     * @param tid   The id of the given topic
     * @return  On success returns an Array of class Idea objects, containing all the ideas in the given topic.
     *          On failure, returns null.
     */
    private Idea[] doRMIGetTopicIdeas(int uid, int tid){
        Idea[] devolve = null;

        try{
            devolve=rmi.getRMIInterface().getIdeasFromTopic(uid, tid);
        }catch(RemoteException e){
            e.printStackTrace();
        }

        return devolve;
    }

    /**
     * Gets all the ideas owned by the user, safely and using RMI.
     * @return  An array of Idea objects, containing all the ideas owned by the user.
     */
    private Idea[] doRMIGetUserIdeas(){
        Idea[] devolve = null;

        uid = 1;
        //FIXME Eliminar isto!

        try{
            devolve=rmi.getRMIInterface().getIdeasFromUser(uid);
        }catch (RemoteException e){
            e.printStackTrace();
        }

        return devolve;
    }

    /**
     * Gets all the ideas stored in a user's watchlist, safely and using RMI.
     * @return  An array of Idea objects, conatining all the ideas stored in the user's watchlist.
     */
    private Idea[] doRMIGetUserWatchList(){
        Idea[] devolve = null;

        uid = 1;
        //FIXME: Eliminar isto!

        try{
            devolve=rmi.getRMIInterface().getIdeasFromWatchList(uid);
        }catch(RemoteException e){
            e.printStackTrace();
        }

        return devolve;
    }

    /**
     * Gets all the topics safely and using RMI, which titles contain the String specified in title.
     * @param title The title (or part of it) of the topic we want to search.
     * @return  An array of Topic objects, containing all the results for the search we performed.
     */
    private Topic[] doRMISearchTopic(String title){
        Topic[] devolve = null;

        try{
            devolve = rmi.getRMIInterface().getTopics(title);
        }catch (RemoteException e){
            e.printStackTrace();
        }

        return devolve;
    }

    /**
     * Gets all the ideas with the specified id and which title contains
     * @param id    The id of the idea we want to search
     * @param title The title (or part of it) of the idea we want to search
     * @return      An array of Idea objects, containing all the results for the search we performed.
     */
    private Idea[] doRMISearchIdea(int id, String title){
        Idea[] devolve = null;

        try{
            devolve = rmi.getRMIInterface().getIdeaByIID(id, title);
        }catch(RemoteException e){
            e.printStackTrace();
        }

        return devolve;
    }

    /**
     * Gets all the ideas with the specified id and whose title contains
     * @param id    The id of the idea we want to search
     * @return      An Idea object, containing the result for the search we performed.
     */
    private Idea doRMISearchIdea(int id){
        Idea devolve = null;

        try{
            devolve = rmi.getRMIInterface().getIdeaByIID(id, uid);
        }catch(RemoteException e){
            e.printStackTrace();
        }

        return devolve;
    }

    /**
     * Call's RMI createIdea safelly. Remote invocation of the createIdea method in the RMI server, which is going to
     * create a new idea in the database.
     * @param ideia         The idea we want to create
     * @param topicos       A list of topics in which we want to include the idea.
     * @param moneyInvested The money invested by the user in the idea
     * @param file          The file attached to the idea
     * @return              A boolean value, indicating the result of the operation (success/failure)
     */
    private boolean doRMISubmitIdea(Idea ideia,ArrayList<String> topicos,int moneyInvested,NetworkingFile file){
        boolean devolve = false;
        int result;

        try{
            result = rmi.getRMIInterface().createIdea(ideia.getTitle(), ideia.getBody(), getUid(), moneyInvested);

            if (result > 0){
                //Associar aos topicos
                for (String topico : topicos) {
                    rmi.getRMIInterface().setTopicsIdea(result,topico,getUid());
                }

                //Tratar do ficheiro
                if (file != null){
                    devolve = rmi.getRMIInterface().addFile(ideia.getId(),file);
                    ideia.setFile("Y");
                }
                else
                    devolve = true;
            }

        }catch (RemoteException e){
            e.printStackTrace();
        }

        return devolve;
    }

    /**
     * Safely set the selling price of each share of an idea owned by the user.
     * @param iid   The id of the idea whose shares' selling price we want to update
     * @param uid   The id of the user performing this operation
     * @param price The new shares' selling price
     * @return      A boolean value, indicating the result of the operation (success/failure)
     */
    private boolean doRMISetSharePrice(int iid, int uid, float price){
        boolean devolve = false;

        try{
            devolve = rmi.getRMIInterface().setPricesShares(iid,uid,price);
            System.out.println("Recebi " + devolve + " do rmi no set share price");
        }catch(RemoteException e){
            e.printStackTrace();
        }

        return devolve;
    }

    /**
     * Safely gets the money saved by the user, through a database connection.
     * @return  The money saved by the user.
     */
    private float doRMIGetUserCoins(){
        float devolve = -1;

        try{
            devolve = rmi.getRMIInterface().getUserMoney(uid);
        }catch(RemoteException e){
            e.printStackTrace();
        }
        return devolve;
    }

    /**
     * Safely gets the administration role of the given user, accessing the database.
     * @return  A boolean value, indicating if the user is an administrator (root) or just a simple user.
     */
    private boolean doRMIGetAdminStatus(){
        boolean devolve = false;

        try{
            devolve = rmi.getRMIInterface().getAdminStatus(uid);
        }catch (RemoteException e){
            e.printStackTrace();
        }

        return devolve;
    }

    /**
     * Safely add an idea to the current user's watchlist.
     * @param iid   The id of the idea to be added to the user's watchlist.
     * @return      A boolean value, indicating the success or failure of the operation.
     */
    private boolean doRMIAddToWatchList(int iid){
        try{
            rmi.getRMIInterface().addIdeaToWatchlist(iid, uid);
        }catch(RemoteException e){
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Safely remove an idea from the current user's watchlist.
     * @param iid   The id of the idea to be removed from the user's watchlist.
     * @return      A boolean value, indicating the success or failure of the operation.
     */
    private boolean doRMIRemoveFromWatchList(int iid){
        try{
            rmi.getRMIInterface().removeIdeaFromWatchlist(iid, uid);
        }catch(RemoteException e){
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Safely gets all the transactions performed by the user.
     * @return      An array of String objects, containing all the transactions performed by the user.
     */
    private String[] doRMIGetHistory(){
        String[] devolve = null;

        try{
           devolve = rmi.getRMIInterface().getHistory(uid);
        }catch(RemoteException e){
            e.printStackTrace();
        }

        return devolve;
    }

    /**
     * Safely deletes an idea as requested by the user.
     * @param iid   The id of the idea.
     * @return We have 4 possible return values:
     * -1 -> Idea has no children
     * -2 -> User is not the owner of the idea
     * 1 > Everything went well
     */
    private int doRMIRemoveIdea(int iid){
        int devolve = -1;
        Idea temp = new Idea();

        temp.setId(iid);

        try{
            devolve = rmi.getRMIInterface().removeIdea(temp,uid);
        }catch(RemoteException e){
            e.printStackTrace();
        }

        return devolve;
    }

    //TODO: Javadoc
    private BuySharesReturn doRMIBuyShares(int iid, float maxPricePerShare, int buyNumShares,
                                           boolean addToQueueOnFailure, float targetSellPrice) {
        BuySharesReturn ret = null;
        try {
            ret = rmi.getRMIInterface().buyShares(uid,iid,maxPricePerShare,buyNumShares,addToQueueOnFailure,
                                                  targetSellPrice);
        } catch (RemoteException e) {
            System.out.println("Exception in doRMIBUyShares");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println("Returning from doRMIBUyShares");
        return ret;
    }

    /**
     * Public interface to try to login a client. If successful, current state will be updated to indicate that this
     * Client represents the user given by this (username,password). Specifically, this.uid will be set to its uid
     * @param username User's username
     * @param password User's password
     * @return A boolean value, indicating the success or failure of the operation
     */
    public boolean doLogin(String username, String password) {
        if ( (this.uid = doRMILogin(username, password)) != -1 ) {
            this.username = username;

            //Gets client's admin status
            this.adminStatus = doRMIGetAdminStatus();

            return true;
        }

        return false;
    }

    /**
     * Public interface to try to get all the topics stored in the database
     * @return  An ArrayList of class Topic objects, containing all the topics in the database
     */
    public Topic[] doGetTopics(){
        return doRMIGetTopics();
    }

    /**
     * Public interface to search a topic by its title, or part of it.
     * @param title The title (or part of it) of the topic we want to search
     * @return  An array of Topic objects, containing all the topics found containing in their titles the String specified
     *          in title
     */
    public Topic[] doSearchTopic(String title){
        return doRMISearchTopic(title);
    }

    /**
     * Public interface to search an idea by its id and its title (or part of it).
     * @param iid   The id of the idea we want to search
     * @param title The title (or part of it) of the idea we want to search
     * @return      An array of Idea objects, containing all the ideas founded, based on the search we performed
     */
    public Idea[] doSearchIdea(int iid, String title){
        return doRMISearchIdea(iid, title);
    }

    /**
     * Public interface to search an idea by its id and its title (or part of it).
     * @param iid   The id of the idea we want to search
     * @return      An Idea object, containing the idea founded, based on the search we performed
     */
    public Idea doSearchIdea(int iid){
        return doRMISearchIdea(iid);
    }

    /**
     * Public interface to perform an idea creation in the database
     * @param ideia         The idea we want to create
     * @param topics        A list of topics in which we want to include the idea
     * @param moneyInvested The money invested in the idea by the user
     * @param file          The file attached to the idea
     * @return              A boolean value indicating the result of the operation (success/failure)
     */
    public boolean doSubmitIdea(Idea ideia,ArrayList<String> topics,int moneyInvested,NetworkingFile file){
        return doRMISubmitIdea(ideia, topics, moneyInvested, file);
    }

    /**
     * Public interface to try to register a client. If successful, we automatically perform the login for the given
     * client.
     * @param username User's username
     * @param password User's password
     * @param email User's password
     * @return A boolean value, indicating the success or failure of the operation
     */
    public boolean doRegister(String username, String password, String email){
        return doRMIRegister(username, password, email) && doLogin(username, password);
    }

    /**
     * Public interface to try to get all the ideas owned by the user.
     * @return  An array of Idea objects, containing all the ideas owned by the user.
     */
    public Idea[] doGetUserIdeas(){
        return doRMIGetUserIdeas();
    }

    /**
     *Public interface to try to get all the ideas in the user's watchlist.
     * @return An array of Idea objects, containing all the ideasin the user's watchlist.
     */
    public Idea[] doGetUserWatchList(){
        return doRMIGetUserWatchList();
    }

    /**
     * Public interface to try to get all the ideas in a given topic.
     * @param tid   The id of the topic in question.
     * @return  An array of Idea objects, containing all the ideas in the given topic
     */
    public Idea[] doGetTopicIdeas(int tid){
        return doRMIGetTopicIdeas(uid, tid);
    }

    /**
     * Public interface to try to set a given selling price to each share of a given idea.
     * @param iid   The id of the idea whose shares' selling price we want to define
     * @param price The target selling price for the user's shares of the given idea
     * @return      A boolean value, indicating the result of the operation (success/failure)
     */
    public boolean doSetSharePrice(int iid, float price){
        return doRMISetSharePrice(iid, uid, price);
    }

    /**
     * Public interface to try to get the user's transaction history.
     * @return      An array of String objects, containing all the transactions performed by the user.
     */
    public String[] doGetHistory(){
        return doRMIGetHistory();
    }

    /**
     * Gets the title of the supplied topic id.
     * @param tid The topic id
     * @return The topic title, or null in case of failure
     */
    public String doGetTopicTitle(int tid) {
        return doRMIGetTopicTitle(tid);
    }

    /**
     * Adds an idea to the user's watchlist
     * @param iid   The id of the idea to add to the user's watchlist
     */
    public boolean doAddToWatchList(int iid){
        return doRMIAddToWatchList(iid);
    }

    /**
     * Gets the id of the client.
     * @return  An Integer value, containing the ID of the client
     */
    public int getUid() {
        return uid;
    }

    /**
     * Gets the user's username.
     * @return  A String object, containing the user's username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the money saved by the user.
     * @return  The money saved by the user.
     */
    public float getCoins() {
        this.coins = doRMIGetUserCoins();
        return this.coins;
    }

    /**
     * Gets the number of pending notifications for the current user.
     * @return  The number of pending notifications for the current user.
     */
    public int getNumNotifications() {
        return numNotifications;
    }

    /**
     * Tells us if the current user is an administrator.
     * @return  A boolean value, telling us if the current user is an administrator
     */
    public boolean getAdminStatus() {
        return adminStatus;
    }

    /**
     * Public interface to remove an idea from the user's watchlist.
     * @param iid   The id of the idea.
     * @return      A boolean value, indicating the success or failure of the operation.
     */
    public boolean doRemoveFromWatchList(int iid) {
        return doRMIRemoveFromWatchList(iid);
    }

    /**
     * Public interface to remove an idea from the list of ideas.
     * @param iid   The id of the idea the user wants to remove.
     * @return We have 4 possible return values:
     * -1 -> Idea has no children
     * -2 -> User is not the owner of the idea
     * 1 > Everything went well
     */
    public int doRemoveIdea(int iid){
        return doRMIRemoveIdea(iid);
    }

    //TODO: Javadoc
    public BuySharesReturn doBuyShares(int iid, float maxPricePerShare, int buyNumShares,
                                       boolean addToQueueOnFailure, float targetSellPrice) {
        return doRMIBuyShares(iid, maxPricePerShare, buyNumShares, addToQueueOnFailure, targetSellPrice);
    }
}
