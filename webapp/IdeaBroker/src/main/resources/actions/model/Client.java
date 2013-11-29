package actions.model;

import model.RMI.RMIConnection;
import model.data.Idea;
import model.data.Topic;

import java.rmi.RemoteException;

/**
 * Client Bean which acts as our Model. It stores the RMI, the uid and other useful variables associated with the
 * current user session. It is responsible for all interaction with the RMI.
 */
public class Client {
    private final static String RMI_HOST="localhost";//FIXME: MUDAR ISTO?? NAO SEI SE O PROF QUER VER LOCALHOST NO CODIGO


    private RMIConnection rmi;
    private int uid;

    public Client() {
        this.rmi = new RMIConnection(RMI_HOST);
        this.uid = -1;
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
     * Gets all the ideas in a given topic safely, using RMI.
     * @param tid   The id of the given topic
     * @return  On success returns an Array of class Idea objects, containing all the ideas in the given topic.
     *          On failure, returns null.
     */
    private Idea[] doRMIGetTopicIdeas(int tid){
        Idea[] devolve = null;

        try{
            devolve=rmi.getRMIInterface().getIdeasFromTopic(tid);
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
            devolve = rmi.getRMIInterface().getIdeaByIID(id,title);
        }catch(RemoteException e){
            e.printStackTrace();
        }

        return devolve;
    }

    /**
     * Gets all the ideas with the specified id and which title contains
     * @param id    The id of the idea we want to search
     * @return      An Idea object, containing the result for the search we performed.
     */
    private Idea doRMISearchIdea(int id){
        Idea devolve = null;

        try{
            devolve = rmi.getRMIInterface().getIdeaByIID(id);
        }catch(RemoteException e){
            e.printStackTrace();
        }

        return devolve;
    }

    private boolean doRMISubmitIdea(Idea ideia,String[] topicos,int moneyInvested){
        boolean devolve = false;
        int result;

        try{
            result = rmi.getRMIInterface().createIdea(ideia.getTitle(),ideia.getBody(),getUid(),moneyInvested);

            if (result > 0){
                //Associar aos topicos
                for (String topico : topicos) {
                    rmi.getRMIInterface().setTopicsIdea(result,topico,getUid());
                }

                devolve=true;
            }

        }catch (RemoteException e){
            e.printStackTrace();
        }

        return devolve;
    }

    /**
     * Public interface to try to login a client. If successful, current state will be updated to indicate that this
     * Client represents the user given by this (username,password). Specifically, this.uid will be set to its uid
     * @param username User's username
     * @param password User's password
     * @return A boolean value, indicating the success or failure of the operation
     */
    public boolean doLogin(String username, String password) {
        return (this.uid = doRMILogin(username, password)) != -1;
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

    public boolean doSubmitIdea(Idea ideia,String[] topics,int moneyInvested){
        return doRMISubmitIdea(ideia,topics,moneyInvested);
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
        return doRMIGetTopicIdeas(tid);
    }

    /**
     * Gets the id of the client.
     * @return
     */
    public int getUid() {
        return uid;
    }
}
