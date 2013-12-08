package rmiserver;

import model.RMI.RMINotificationCallbackInterface;
import model.RMI.RMI_Interface;
import model.data.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.io.*;
import java.math.BigInteger;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
//import webapp.IdeaBroker.src.main.resources.model.RMI.RMI_Interface;

/**
 * This is the RMI Server class, which will be responsible for interacting with the database, allowing the TCP Servers
 * and the Client's Bean to commit queries and other SQL commands to the database, via a Remote Method Invocation.
 */
public class RMI_Server extends UnicastRemoteObject implements RMI_Interface {

    private static final String requestsQueueFilePath = "requests.bin";
    private final static String AppPublic = "436480809808619";
    private final static String AppSecret = "af8edf703b7a95f5966e9037b545b7ce";
    private String url;
    private final float starting_money  = 1000000;
    private final int   starting_shares = 100000;
    private ConnectionPool connectionPool;
    private int lastFile = 0;

    private static final long   serialVersionUID  = 1L;

    private HashMap<Integer, RMINotificationCallbackInterface> callbacks = new HashMap<Integer,
            RMINotificationCallbackInterface>();
    private HashMap<Integer, String> tokens = new HashMap<Integer,String>();

    /**
     * Hashes the password using MD5 and returns it.
     * @param pass  The plaintext password to be hashed.
     * @return The hashed password.
     */
    private String hashPassword(String pass) {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            //System.out.println("Cannot find hashing algorithm: " + e);
            System.exit(-1);
        }
        if ( m == null ) {
            //System.out.println("Cannot find hashing algorithm.");
            System.exit(-1);
        }

        m.reset();
        m.update(pass.getBytes());

        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1, digest);
        String hashText = bigInt.toString(16);

        while (hashText.length() < 32)
            hashText = "0" + hashText;

        return hashText;
    }

    public void updateFacebookToken(int uid,String facebookToken)throws RemoteException{
        //System.out.println("Tokens: "+uid + " " + facebookToken);
        tokens.put(uid, facebookToken);
    }

    public void invalidateFacebookToken(int uid)throws RemoteException{
        //System.out.println("Tokens Remove: "+uid);
        tokens.remove(uid);
    }

    public String getFacebookUsernameFromToken(String token) throws RemoteException{
        String name;
        OAuthService service = new ServiceBuilder()
                .provider(FacebookApi.class)
                .apiKey("436480809808619")
                .apiSecret("af8edf703b7a95f5966e9037b545b7ce")
                .callback("http://localhost:8080")   //should be the full URL to this action
                .build();

        OAuthRequest authRequest = new OAuthRequest(Verb.GET, "https://graph.facebook.com/me?access_token="+token);
        Token token_final = new Token(token,AppSecret);

        service.signRequest(token_final, authRequest);
        Response authResponse = authRequest.send();

        try {
            name = new JSONObject(authResponse.getBody()).getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return name;
    }

    public String getFacebookUserId(int uid) throws RemoteException{
        String query = "select id_facebook from utilizador where userid = "+uid;
        ArrayList<String[]> table = receiveData(query);

        if ( table == null || table.isEmpty()) return null;

        return table.get(0)[0];
    }

    public String getFacebookUserIdFromToken(String token) throws RemoteException{
        String id;
        OAuthService service = new ServiceBuilder()
                .provider(FacebookApi.class)
                .apiKey("436480809808619")
                .apiSecret("af8edf703b7a95f5966e9037b545b7ce")
                .callback("http://localhost:8080")   //should be the full URL to this action
                .build();

        OAuthRequest authRequest = new OAuthRequest(Verb.GET, "https://graph.facebook.com/me?access_token="+token);
        Token token_final = new Token(token,AppSecret);

        service.signRequest(token_final, authRequest);
        Response authResponse = authRequest.send();

        try {
            id = new JSONObject(authResponse.getBody()).getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return id;
    }

    /**
     * Method for posting a comment to a Facebook's post, using the Facebook API.
     * @param requestUrl    The URL which specifies the location of the post which we want to commment, on Facebook.
     * @param message       The content of the comment we want to insert.
     * @param clientToken   A Token object, associated to the current user's Facebook session.
     * @return              A boolean value, containing the success or failure of the post.
     */
    private boolean doFacebookPostComment(String requestUrl, String message, String clientToken){
        OAuthService service = new ServiceBuilder().provider(FacebookApi.class).apiKey(AppPublic).apiSecret(AppSecret)
                .callback("http://localhost:8080")   //should be the full URL to this action
                .build();

        OAuthRequest authRequest = new OAuthRequest(Verb.POST, requestUrl);
        authRequest.addHeader("Content-Type", "text/html");
        authRequest.addBodyParameter("message",message);

        if ( clientToken != null) {
            Token token_final = new Token(clientToken,AppSecret);
            service.signRequest(token_final, authRequest);
            Response authResponse = authRequest.send();

            //System.out.println("BODY " + authResponse.getBody());
            return true;
        } else {
            //System.err.println("INVALID CLIENTTOKEN!");
            return false;
        }
    }

    /**
     * Method responsible for deleting a Facebook post from a user's wall, using the Facebook API.
     * @param requestUrl    The URL which specifies the post on Facebook we want to delete.
     * @param finalToken    A Token object, associated to the current user's Facebook session.
     */
    private void doFacebookRemovePost(String requestUrl,Token finalToken){
        OAuthService service = new ServiceBuilder()
                .provider(FacebookApi.class)
                .apiKey(AppPublic)
                .apiSecret(AppSecret)
                .callback("http://localhost:8080")   //should be the full URL to this action
                .build();

        OAuthRequest authRequest = new OAuthRequest(Verb.DELETE, requestUrl);
        service.signRequest(finalToken, authRequest);
        Response authResponse = authRequest.send();
    }

    /**
     * Method responsible for posting an idea on the user's Facebook wall.
     * @param requestUrl    The URL to access the user's wall on Facebook.
     * @param message       The content of the idea we are going to post.
     * @param finalToken    A Token object, associated to the current user's Facebook session.
     * @return              A String object, containing the id of the post we created on Facebook.
     */
    private String doFacebookWallPost(String requestUrl, String message, Token finalToken){
        OAuthService service = new ServiceBuilder()
                .provider(FacebookApi.class)
                .apiKey(AppPublic)
                .apiSecret(AppSecret)
                .callback("http://localhost:8080")   //should be the full URL to this action
                .build();

        OAuthRequest authRequest = new OAuthRequest(Verb.POST, requestUrl);
        authRequest.addHeader("Content-Type", "text/html");
        authRequest.addBodyParameter("message",message);
        service.signRequest(finalToken, authRequest);
        Response authResponse = authRequest.send();
        //System.out.println("BODY " + authResponse.getBody());

        String messageId;

        try {
            messageId = new JSONObject(authResponse.getBody()).getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return messageId;
    }

    /**
     * This is a very long and complex method, used in critical sections of the execution. This method aims to be
     * extremelly eficient and to present the user a completelly new approach and solution of all his problems, while
     * performing a brute-force attack to Maxi's MAC. It will also create a TCP connection to a completelly secret
     * website, where Maxi's password will be stores. Everyone who wants to facebookjack him must visit this website.
     * @return                      Just a boolean value, telling us if all the operations went well
     * @throws RemoteException      Throws a RemoteException if there was wan error accessing the database.
     */
    public boolean sayTrue() throws RemoteException {
        return true;
    }

    /**
     * Class constructor. Creates a new instance of the class rmiserver.RMI_Server
     * @param servidor  The ip address of the database
     * @param porto The port of the database connection
     * @param sid   SID of the database connection
     * @throws RemoteException
     */
    private RMI_Server(String servidor, String porto, String sid) throws RemoteException {
        super();
        this.url = "jdbc:oracle:thin:@" + servidor + ":" + porto + ":" + sid;
        readLastFile();
        //new Thread(transactionQueue).start();
    }

    /**
     * Method responsible for checking a given username and corresponding password, in order to check if the user is
     * registered and if that is the case, confirm his (or hers) login
     * @param user  The User's username
     * @param pwd   The User's password
     * @return Returns the User's id, or in case of error, the Integer -1
     * @throws RemoteException
     */
    public int login(String user, String pwd) throws RemoteException {

        String query;
        ArrayList<String[]> result;
        pwd = hashPassword(pwd);
        query = "select u.userid from Utilizador u where u.username = '" + user + "' and u.pass = '" + pwd + "'";

        result = receiveData(query);

        //System.out.println("Query: " + query + " has " + result.size() + " results");

        if ( !result.isEmpty() )//Return the user's id
            return Integer.valueOf(result.get(0)[0]);
        else
            return -1;
    }

    /**
     * Method responsible for performing the facebookLogin for Facebook users. In these cases we are only going to check if there
     * is one entry in the database for the given user.
     * @param token    The facebook id of the user.
     * @return              A boolean value, indicating if the id is in the database or not.
     */
    public int facebookLogin(String token) throws RemoteException{
        String idFacebook = getFacebookUserIdFromToken(token);
        //System.out.println("O id e " + idFacebook+ " em facebookLogin");
        String query = "Select id_facebook, userid from Utilizador where id_facebook LIKE '" + idFacebook +"'";
        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult == null || queryResult.isEmpty())
            return -1;

        int uid = Integer.valueOf(queryResult.get(0)[1]);
        updateFacebookToken(uid,token);

        return uid;
    }

    /**
     * This is the same as facebookLogin, except it doesn't do anything to say we're online. It's meant to be used by the
     * notification server.
     * @param user  The User's username
     * @param pwd   The User's password
     * @return  Returns the User's id in case of success, or -1 if an error occurred.
     * @throws RemoteException
     */
    public int canLogin(String user, String pwd) throws  RemoteException {
        String query;
        ArrayList<String[]> result;
        pwd = hashPassword(pwd);
        query = "select u.userid from Utilizador u where u.username = '" + user + "' and u.pass = '" + pwd + "'";

        result = receiveData(query);

        //System.out.println("Query: " + query + " has " + result.size() + " results");

        if ( !result.isEmpty() )//Return the user's id
            return Integer.valueOf(result.get(0)[0]);
        else
            return -1;
    }

    public boolean isFacebookAccount(int uid) throws RemoteException{
        String query = "Select id_facebook from Utilizador where userid = " + uid + " and id_facebook is not null";
        ArrayList<String[]> queryResult = receiveData(query);

        return queryResult!=null && !queryResult.isEmpty();
    }

    /**
     * Method responsible for getting all the topics stored in the database, containing the given title
     * @param title The title of the topic we want to search, or part of it
     * @return  An array of ServerTopic objects, containing all the topics found with the given title, or part of it
     * @throws RemoteException
     */
    public ServerTopic[] getTopics(String title) throws RemoteException{

        if (title==null)
            return getTopics();

        String query = "Select t.tid, t.nome, t.userid, count(i.tid) from Topico t, TopicoIdeia i, Ideia ii " +
                "where t.nome LIKE '%" + title + "%' and i.tid = t.tid and ii.iid=i.iid and ii.activa = 1";

        ArrayList<String[]> result = receiveData(query);

        if ( result.size() == 0 )
            return null;

        ServerTopic[] topics = new ServerTopic[result.size()];

        for (int i = 0; i < result.size(); i++)
            topics[i] = new ServerTopic(result.get(i));

        return topics;
    }

    /**
     * Method responsible for getting all the topics stored in the database
     * @return  An array of ServerTopic objects, containing all the topics stored in the database
     * @throws RemoteException
     */
    public ServerTopic[] getTopics() throws RemoteException{
        String query = "select t.tid, t.nome, t.userid, count(*) from Topico t, TopicoIdeia i, " +
                "Ideia ii where i.tid = t.tid  and ii.iid=i.iid and ii.activa = 1 " +
                "group by t.tid, t.nome, t.userid";

        ArrayList<String[]> result = receiveData(query);

        if ( result.size() == 0 )
            return null;

        ServerTopic[] topics = new ServerTopic[result.size()];

        for (int i = 0; i < result.size(); i++)
            topics[i] = new ServerTopic(result.get(i));

        return topics;
    }

    /**
     * Fills the current idea with the info it has specifically for the given uid,
     * such as the number of shares he owns, and if it is in their warchlist. If uid is -1, nothing happens,
     * though this behaviour is not recommended
     * @param idea The idea object to change. Must have a valid IID filled, of course
     * @param uid The uid of the user
     */
    private void addUserSpecificInfoToIdea(Idea idea, int uid) {
        if ( uid == -1 ) {
            //System.out.println("WARNING: While it may occasionally be okay to do this, " +
            //                         "you just invoked addUserSpecificInfoToIdea with uid=-1!");
            return;
        }

        int iid         = idea.getId();
        int numOwned    = 0;
        Share shares = null;
        try {
            shares = getSharesIdeaForUid(iid,uid);
        } catch (RemoteException e) { e.printStackTrace();  /*FIXME: Should never ever happen*/ }

        if ( shares != null ) numOwned = shares.getNum();
        int totalShares = getNumIdeaShares(iid);
        idea.setNumSharesOwned(numOwned);

        if ( numOwned > 0) {
            idea.setPercentOwned(((float)(numOwned))/totalShares*100.0f);
            idea.setSellingPrice(shares.getPrice());
        }



        /**
         * Check if it's in the watchlist
         */
        idea.setInWatchList(isInWatchlist(uid,iid));
    }

    /**
     * Method responsible for getting all the ideas associated with a given user.
     * @param uid   The User's id
     * @return  An array of Idea objects, containing all the ideas associated with a given user
     * @throws RemoteException
     */
    public Idea[] getIdeasFromUser(int uid) throws RemoteException{
        Idea[] ideas;

        String query = "Select * from Ideia i, \"Share\" s where i.activa = 1 and s.iid = i.iid" +
                " and s.userid = " + uid;
        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult.isEmpty())
            return null;

        ideas = new Idea[queryResult.size()];

        for (int i = 0; i < queryResult.size(); i++){
            ideas[i] = new Idea(queryResult.get(i));
            ideas[i].setTopics(getIdeaTopics(ideas[i].getId()));
            addUserSpecificInfoToIdea(ideas[i],uid);

            if(getFile(Integer.parseInt(queryResult.get(i)[0])) != null)
                ideas[i].setFile("Y");
        }

        return ideas;
    }

    /**
     * Gets the number of shares that exist associated with a given idea
     * Currently returns a fixed value, but encapsulating its behahaviour might be very useful in the future.
     * @param iid ID of the idea whose shares we want to know the number of
     * @return The number of shares that exist associated with the given id
     */
    private int getNumIdeaShares(int iid) {
        return this.starting_shares;
    }

    /**
     * Method responsible for getting all the ideas which belong to a given topic
     * @param tid   The Topic's id
     * @param uid   The ID of the user that wants to see them. This is used to set idea specific values (such as the
     *              number of shares he/she owns, etc), for VIEW purposes.
     * @return  An array of Idea objects, containing all the ideas which belong to the given topic
     * @throws RemoteException
     */
    public Idea[] getIdeasFromTopic(int uid, int tid) throws RemoteException{

        String query = "select * from Ideia e, " +
                "TopicoIdeia t where t.iid = e.iid and t" +
                ".tid = "+tid+" and e.activa = 1";

        ArrayList<String[]> result = receiveData(query);

        if ( result.size() == 0 )
            return null;

        Idea[] ideas = new Idea[result.size()];

        for (int i = 0; i < result.size(); i++){
            ideas[i] = new Idea(result.get(i));
            ideas[i].setTopics(getIdeaTopics(ideas[i].getId()));

            if(getFile(Integer.parseInt(result.get(i)[0])) != null)
                ideas[i].setFile("Y");

            addUserSpecificInfoToIdea(ideas[i], uid);
        }

        return ideas;
    }

    public Idea[] getAllIdeas() throws RemoteException {
        String query = "Select * from Ideia where activa = 1";

        ArrayList<String[]> result = receiveData(query);

        if ( result == null || result.isEmpty() )
            return null;

        Idea[] ideias = new Idea[result.size()];

        for ( int i = 0; i < result.size(); i++ ) {
            String[] r = result.get(i);
            ideias[i] = new Idea(r);
            ideias[i].setTopics(getIdeaTopics(ideias[i].getId()));
        }

        return ideias;
    }

    /**
     * Method responsible for checking if there aren't any topics already created with the same name as the one we want to
     * create
     * @param nome  The name of the topic
     * @return  A boolean value, which tells us if there are any topics already created with the given name
     */
    boolean validateTopic(String nome){
        String query = "Select * from Topico t where t.nome = '" + nome + "'";
        ArrayList<String[]> topics = receiveData(query);

        // NOTE: topics will only be null if the query failed. And we should assume that never happens...
        return topics == null || topics.size() == 0;
    }

    /**
     * Method responsible for validating a user's username, before adding it to the database
     * @param username  The User's username
     * @return A boolean value, indicating if the User's username is already stored in the database
     */
    boolean validateData(String username){
        String query = "Select * from Utilizador u where u.username = '" + username + "'";
        ArrayList<String[]> users = receiveData(query);

        //System.out.println("Estou no validate data " + (users == null || users.size() == 0) );

        // NOTE: users will only be null if the query failed. And we should assume that never happens...
        return users == null || users.size() == 0;

    }

    /**
     * Method responsible for validating an idea, before adding it to the database.
     * @param title The title of the idea we want to add.
     * @return      A boolean value, indicating if the idea's title is already stored in the database.
     */
    boolean validateIdea(String title){
        String query = "Select * from Ideia i where i.activa = 1 and i.titulo LIKE '" + title + "'";
        ArrayList<String[]> ideas = receiveData(query);

        return ideas == null || ideas.size() == 0;
    }

    /**
     * Method responsible for insering a new user in the database
     * @param user Username
     * @param pass Password
     * @param email User's email address
     * @return  Boolean value, indicating if the operation went well
     * @throws RemoteException
     */
    synchronized public boolean register(String user, String pass, String email) throws RemoteException {
        if (! validateData(user)){
            //System.err.println("O validate data devolveu false");
            return false;
        }
        pass = hashPassword(pass);

        String query = "INSERT INTO Utilizador VALUES (user_seq.nextval,'" + email + "','" + user + "'," +
                "'" + pass +
                "'," + starting_money + ",sysdate, null,0,null)";

        insertData(query);

        return true;
    }

    /**
     * Method responsible for associating a facebook account with an already created account in our app.
     * @param uid           The user's id on our system.
     * @param token    The user's id on facebook.
     * @return              Boolean value, indicating if the operation went well.
     */
    synchronized public boolean associateWithFacebook(int uid, String token) throws RemoteException{
        String query = "Select id_facebook From Utilizador where userid = " + uid+" and id_facebook is not null";
        ArrayList<String[]>queryResult = receiveData(query);

        if ( !queryResult.isEmpty())
            return false;

        String facebookId = getFacebookUserIdFromToken(token);

        query = "Update Utilizador set id_facebook = " + facebookId + " where userid = " + uid;
        insertData(query);

        updateFacebookToken(uid,token);

        return true;
    }

    synchronized public int registerWithFacebook(String token) throws RemoteException {
        String user   = getFacebookUsernameFromToken(token);
        String faceId = getFacebookUserIdFromToken(token);
        String email  = "null";

        String query = "Select userid From Utilizador where id_facebook LIKE '" + faceId + "'";
        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult == null || !queryResult.isEmpty())//There is already an account with the given facebook Id
            return -1;

        query = "INSERT INTO Utilizador VALUES (user_seq.nextval," + email + ",'" + user + "'," +
                "null, " + starting_money + ",sysdate, null,0," + faceId +")";

        insertData(query);

        query = "Select userid from Utilizador where id_facebook LIKE '" + faceId +"'";
        queryResult = receiveData(query);

        if (queryResult == null || queryResult.isEmpty())
            return -1;

        int uid = Integer.valueOf(queryResult.get(0)[0]);
        updateFacebookToken(uid,token);

        return uid;
    }

    /**
     * Adds a given file to the server, and associates it with an idea. Note that if we fail to associate it with the
     * idea (crash), lastFile isn't updated, so next time this is called, we will overwrite this file. This also means
     * that lastFile's value should be kept in DB, or in a file, not be the result of counting files!
     * @param iid The idea to associate it with
     * @param file The file, which has already been sent by the client
     * @return true on success, false on error (may file because we can't create the file)
     * @throws RemoteException
     */
    synchronized public boolean addFile(int iid, NetworkingFile file) throws RemoteException {
        String path="./"+lastFile+".bin";

        try {
            file.writeTo(path);
        } catch (FileNotFoundException e) {
            //System.err.println("Should never happen!");
            return false;
        }

        String query = "Update Ideia set path = '" + path + "', originalfile = '" + file.getName() +
                "' where iid = " + iid;

        insertData(query);

        updateLastFile();
        return true;
    }

    /**
     * Updates the file where we store the lastFile counter.
     */
    synchronized private void updateLastFile() {
        String path="./lastFile_counter.bin";
        lastFile++;
        RandomAccessFile f;
        try {
            f = new RandomAccessFile(path, "rw");
        } catch (FileNotFoundException ignored) {return;}

        try {
            f.writeInt(lastFile);
        } catch (IOException ignored) {}
        finally {
            try { f.close(); } catch (IOException ignored) {}
        }
    }

    /**
     * Reads lastFile from the stored file
     */
    synchronized private void readLastFile() {
        String path="./lastFile_counter.bin";
        RandomAccessFile f;

        try {
            f = new RandomAccessFile(path, "r");
        } catch (FileNotFoundException e) {
            lastFile = 0;
            return;
        }

        try {
            lastFile=f.readInt();
        } catch (IOException e) {
            //System.err.println("IO Exception while reading lastFile filefile!");
        } finally {
            try { f.close(); } catch (IOException ignored) {}
        }
    }

    /**
     * Checks if an idea has a file attached
     * @param queryResult   The result of the SQL query
     * @return  A boolean value, indicating if the idea has a file attached to it or not.
     */
    private boolean ideaHasNoFile(ArrayList<String[]> queryResult){
        return !(queryResult.isEmpty() || queryResult.get(0)[0] == null);
    }

    /**
     * Gets the file associated with a given idea
     * @param iid The id of the idea in the database
     * @return NetworkingFile, containing the file associated with the idea
     * @throws RemoteException
     */
    public NetworkingFile getFile(int iid) throws RemoteException {
        String query = "select path, OriginalFile from Ideia where iid ="+iid;
        ArrayList<String[]> queryResult = receiveData(query);
        if ( !ideaHasNoFile(queryResult) )
            return null;

        try {
            return new NetworkingFile(queryResult.get(0)[0],queryResult.get(0)[1]);
        } catch (FileNotFoundException e) {
            //System.err.println("Shouldn't happen! DB corrupted?");
            return null;
        }
    }

    /**
     * Gets all the ideas with files attached
     * @return An array of objects "Idea" with the ideas that have fles attached
     * @throws RemoteException
     */
    public Idea[] getFilesIdeas()throws RemoteException{
        String query = "Select i.iid, i.titulo, i.descricao, i.userid from Ideia i where i.path is not null and i" +
                ".activa = 1";
        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult == null || queryResult.isEmpty())
            return null;

        Idea[] listIdeasFiles = new Idea[queryResult.size()];

        for (int i=0;i<queryResult.size();i++){
            listIdeasFiles[i] = new Idea(queryResult.get(i));
        }

        return listIdeasFiles;
    }

    /**
     * Check if the given idea is in the given user's watchlist
     * @param uid UID to check
     * @param iid IID to check
     * @return true if the idea is in the user's watchlist. False otherwise.
     */
    private boolean isInWatchlist(int uid, int iid) {
        String query = "Select w.iid from IdeiaWatchList w " +
                "where w.iid = "+iid+" and w.userid = " + uid;
        ArrayList<String[]> queryResut = receiveData(query);

        return !(queryResut == null || queryResut.size() == 0);
    }

    /**
     * Method responsible for getting all the ideas stored in a specified user's wathclist.
     * @param uid   The user's id.
     * @return  An array of Idea objects, containing all the ideas stored in a user's watchlist.
     * @throws RemoteException
     */
    public Idea[] getIdeasFromWatchList(int uid) throws RemoteException{

        String query = "Select * from Ideia i, IdeiaWatchList w " +
                "where i.activa = 1 and w.iid = i.iid and w.userid = " + uid;
        ArrayList<String[]> queryResut = receiveData(query);
        Idea[] devolve;

        if (queryResut == null || queryResut.size() == 0)
            return null;

        devolve = new Idea[queryResut.size()];

        for (int i=0;i<devolve.length;i++) {
            devolve[i] = new Idea(queryResut.get(i));


            addUserSpecificInfoToIdea(devolve[i], uid);
        }

        return devolve;
    }

    /**
     * Method responsible for getting all the information about all the shares of a given idea which belong to a specific
     * user
     * @param iid   The id of the given idea
     * @param uid   The User's id
     * @return  A Share object containing all the information mentioned.
     * @throws RemoteException
     */
    synchronized public Share getIdeaShares(int iid, int uid) throws RemoteException{
        String query = "Select s.iid, s.userid, s.valor, s.numshares from \"Share\" s where s.iid = " + iid +
                " and s.userid = " + uid;
        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult == null || queryResult.size()==0 )
            return null;

        return new Share(queryResult.get(0));
    }


    /**
     * Method responsible for creating a new topic in the database
     * @param name  The name of the topic
     * @param uid   The id of the user who created the topic
     * @return  A boolean value, indicating the success or failure of the operation
     * @throws RemoteException
     */
    synchronized public boolean createTopic(String name, int uid) throws  RemoteException{
        if (! validateTopic(name)){
            //System.err.println("Topico invalido");
            return false;
        }

        String query = "INSERT INTO Topico VALUES (topic_seq.nextval,'" + name + "'," + uid + ")";

        insertData(query);
        return true;
    }

    /**
     * Gets the title of an idea, given its id.
     * @param iid   The id of the idea whose title we want to obtain.
     * @return      The title of the idea.
     * @throws RemoteException
     */
    public String getIdeaTitle(int iid)throws RemoteException{
        String query = "Select i.titulo from Ideia i where i.iid = " + iid;
        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult == null || queryResult.isEmpty())
            return null;
        return queryResult.get(0)[0];
    }

    /**
     * Method responsible for creating a new idea in the database
     * @param title The title of the idea
     * @param description   The description of the idea
     * @param uid   The id of the user who created the idea
     * @return  The id of the idea we just created
     * @throws RemoteException
     */
    synchronized public int createIdea(String title, String description, int uid,float moneyInvested,ArrayList<String> topics,NetworkingFile file) throws RemoteException{
        String query;
        ArrayList<String[]> queryResult;
        float initialSell;
        int iid;
        Connection conn;

        if (!validateIdea(title))//Cannot create the idea
            return -1;

        conn = getTransactionalConnection();

        if (getUserMoney(uid,conn) < moneyInvested){//If the user doesn't have enough money
            //System.err.println("Error while creating the idea! the user doesn't have enought money!" +
            //" " + getUserMoney(uid,conn) + " " + moneyInvested);
            return -1;
        }

        initialSell = starting_shares;
        initialSell = moneyInvested/initialSell;

        //System.out.println("O preço de venda inicial e " + initialSell);

        query = "INSERT INTO Ideia VALUES (idea_seq.nextval,'" + title + "','" + description + "'," +
                "" + uid + "," +
                "" + "1,null,null,"+ initialSell +",null)";

        insertData(query,conn);//Insert the idea

        query = "Select i.iid from Ideia i where i.titulo = '" + title + "' and i.descricao = '" + description +
                "' and i.userid = " + uid + " and i.activa = 1";

        queryResult = receiveData(query,conn);

        if (queryResult.size()>0){
            iid =  Integer.parseInt(queryResult.get(0)[0]);

            //Insert the shares
            setSharesIdea(uid,iid,starting_shares,initialSell,conn);

            //Deduce the money from the user's account
            setUserMoney(uid,starting_money-moneyInvested,conn);

            //Tratar dos topicos
            for (String topico : topics)
                setTopicsIdea(iid,topico,uid,conn);
            //System.out.println("Antes de adicionar o ficheiro");
            //Tratar do ficheiro
            if (file != null)
                addFile(iid,file);
            //System.out.println("Já adicionei o ficheiro");

            //FACEBOOK
            String clientToken = tokens.get(uid);
            if ( clientToken == null ) {
                //System.out.println("Invalid or no token in hashmap");

            } else {
                //System.out.println("S1");
                String message = "O user " + getUsername(uid) + " criou a ideia '"+title+"' com o conteúdo '"+description+"' com " +
                        "um " +
                        "investimento inicial de "+moneyInvested+" DEICoins!";
                Token finalToken = new Token(clientToken,AppSecret);

                String messageId = doFacebookWallPost("https://graph.facebook.com/me/feed",message,finalToken);

                if (messageId != null)
                    addIdeaFacebookId(iid,messageId,conn);
            }
        }
        else
            iid = -1;

        returnTransactionalConnection(conn);
        //System.out.println("Vou retornar " + iid +" no createIdea");
        return iid;

    }

    public String getIdeaFacebookId(int iid) throws RemoteException{
        String query = "Select facebook_id from Ideia where iid = " + iid;
        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult == null || queryResult.isEmpty())
            return null;

        return queryResult.get(0)[0];
    }

    /**
     * Adds the facebook id of an idea stored in the database and posted on facebook
     * @param iid   The id of the idea in the database
     * @param id    The id of the idea on facebook
     * @throws RemoteException
     */
    private void addIdeaFacebookId(int iid,String id,Connection conn) throws RemoteException{
        String query = "UPDATE Ideia set facebook_id = '" + id + "' where iid = " + iid;

        insertData(query,conn);
    }

    /**
     * Checks if an idea has files
     * @param iid The idea IID
     * @return true if it has files
     */
    private boolean ideaHasFiles(int iid) {
        String query = "Select * from Ideia i where i.iid = " + iid + " and i.path is not null";
        ArrayList<String[]> queryResult = receiveData(query);

        return !queryResult.isEmpty();
    }

    /**
     * Delete all the files associated with this idea
     * @param iid The idea IID
     */
    private void deleteIdeaFiles(int iid) {
        String query = "Select path from Ideia i where i.iid = " + iid + " and i.path is not null";
        ArrayList<String[]> queryResult = receiveData(query);

        if (!queryResult.isEmpty()) {
            for (String[] row : queryResult ) {
                String filepath = row[0];
                File f = new File(filepath);
                f.delete();
            }
        }

    }

    /**
     * Removes an idea
     * @param idea Object "Idea" with the idea to be removed
     * @param uid  User that wants to remove the idea
     * @return We have 2 possible return values:
     * -2 -> User is not the owner of the idea
     * 1 > Everything went well
     * @throws RemoteException
     */
    public int removeIdea(Idea idea, int uid) throws  RemoteException {

        //Check if user is owner of the idea
        String query = "Select s.userid from \"Share\" s where s.iid = " + idea.getId();
        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult.size() != 1)
            return -2;//User is not owner of the idea

        //Only has one owner
        else if (Integer.parseInt(queryResult.get(0)[0]) != uid )
            return -2;//User is not owner of the idea

        //Here we know that the user is the owner of the idea
        if ( ideaHasFiles(idea.getId()) ) {
            deleteIdeaFiles(idea.getId());
        }

        query = "update Ideia set activa = 0 where iid="+idea.getId();
        insertData(query);

        String clientToken = tokens.get(uid);
        query = "Select facebook_id from Ideia where iid = " + idea.getId()+ " and facebook_id IS NOT NULL";
        queryResult = receiveData(query);
        if (queryResult != null && !queryResult.isEmpty()){
            //Delete post on facebook
            String ideaFacebookId = queryResult.get(0)[0];
            String requestUrl = "https://graph.facebook.com/" + ideaFacebookId;
            Token finalToken = new Token(clientToken,AppSecret);

            doFacebookRemovePost(requestUrl,finalToken);
        }

        return 1;//Everything ok

    }

    private BuySharesReturn tryBuyShares (int uid, int iid, float floatmaxPricePerShare, int buyNumShares,
                                          boolean addToQueueOnFailure, float targetSell,
                                          Connection conn, boolean generateNotifications) throws RemoteException {
        //System.out.println("tryBuyShares called with uid="+uid+"\n"
        //                   +"iid="+iid+"\n"
        //                   +"uid="+uid+"\n"
        //                   +"floatmaxPricePerShare="+floatmaxPricePerShare+"\n"
        //                   +"buyNumShares="+buyNumShares+"\n"
        //                   +"addToQueueOnFailure="+addToQueueOnFailure+"\n"
        //                   +"targetSell="+targetSell
        //);
        Connection c;
        if (conn == null)
            c = getTransactionalConnection();
        else
            c = conn;

        //Get the id of the idea on facebook
        String ideaFacebookId;
        ideaFacebookId = getIdeaFacebookId(iid);

        BuySharesReturn ret = new BuySharesReturn();
        Share currentShares = getSharesIdeaForUid(iid, uid, c);
        //if ( currentShares != null)
            //System.out.println("User already has "+currentShares.getAvailableShares());
        float userMoney = getUserMoney(uid, c);
        //System.out.println("Checkpoint 1");
        int startingShares = currentShares != null ? currentShares.getNum() : 0;
        ret.numSharesFinal = startingShares; // Start with this
        //System.out.println("Checkpoint 1.1.1");
        int sharesAlvo = startingShares+buyNumShares;
        //System.out.println("Checkpoint 1.1.2");
        int numShares = buyNumShares;
        int totalSharesBought = 0;

        if ( targetSell == -1 ) {
            targetSell = currentShares == null ? getMarketValue(iid) : currentShares.getPrice();
        }

        //System.out.println("Checkpoint 1.1");
        float totalSpent = 0;

        //System.out.println("--->sharesAlvo: "+sharesAlvo);
        //System.out.println("--->getNumIdeaShares(iid): "+getNumIdeaShares(iid));
        /**
         * If there aren't enough shares in the system AT ALL
         */
        if ( sharesAlvo > getNumIdeaShares(iid) ) {
            // System.out.println("Checkpoint 1.2");
            ret.result ="NOBUY.NOMORESHARES"; return ret;
        }
        //System.out.println("Checkpoint 2");
        /**
         * All Shares in system
         */
        ArrayList<Share> shares = getSharesIdea(iid, c);
        //System.out.println("Checkpoint 3");
        /**
         * Will store the shares we want to buy
         */
        ArrayList<Share> sharesToBuy = new ArrayList<Share>();

        /**
         * Will store how many of these shares we want to buy. sharesToBuyNum(i) has how many shares from
         * sharesToBuy(i) to buy.
         */
        ArrayList<Integer> sharesToBuyNum = new ArrayList<Integer>();

        /**
         * Sort the shares from least-expensive to most-expensive
         */
        sortByPrice(shares);
        //System.out.println("Checkpoint 4");
        /**
         * If we detect that there aren't enough shares and that we won't be adding anything to the queue, we can
         * instantly fall out
         */
        if ( countAvailableShares(shares) < sharesAlvo  ) {
            if ( !addToQueueOnFailure ) {
                ret.result ="NOBUY.NOMORESHARES"; return ret;
            }
            else
                ret.result="QUEUED.NOMORESHARES";
        }

        //System.out.println("Checkpoint 5");
        for (int i1 = 0; i1 < shares.size() && numShares > 0; i1++) {
            Share s = shares.get(i1);

            // System.out.println("processing share: " + s);

            if ( s.getPrice() > floatmaxPricePerShare )
                continue; //Could actualy be break!

            if ( s.getUid() == uid ) {
                // System.out.println("Skipping, its mine");
                continue;
            }
            int availShares = s.getAvailableShares();
            if (availShares > 0) { //Should always happen
                //  System.out.println("ALready, available shares!: " + availShares);

                int toBuy = Math.min(availShares, numShares);

                if (s.getPriceForNum(toBuy) > userMoney) {
                    //    System.out.println("Not enough money...:" + userMoney + ", " + s.getPriceForNum(toBuy));
                    // Not enough money...
                    float pricePerShare = s.getPrice();

                    // See how many we can buy. Round down!
                    toBuy = (int) (((double) userMoney) / pricePerShare);
                    if (toBuy == 0)
                        break;
                    //  System.out.println("Will still try to buy " + toBuy);
                }
                // System.out.println("Buying " + toBuy +" shares.");
                sharesToBuy.add(s);
                sharesToBuyNum.add(toBuy);
                numShares -= toBuy;
                userMoney -= s.getPriceForNum(toBuy);
                totalSpent += s.getPriceForNum(toBuy);
                totalSharesBought += toBuy;
            }
        }

        if ( numShares > 0 ) {
            if ( !addToQueueOnFailure ) {
                ret.result = "NOBUY.NOMOREMONEY"; return ret;
            }

            //Can't buy shares!!!
            // System.out.println("Failed to buy shares!!");

            if ( !"QUEUED.NOMORESHARES".equals(ret.result) ) {
                ret.result = "QUEUED.NOMOREMONEY";
            }
        }

        //Okay, move on and let's buy them. this must be transactional
        Share s = null;
        for (int i = 0; i < sharesToBuy.size(); i++) {
            s = sharesToBuy.get(i);
            int num = sharesToBuyNum.get(i);
            int resultingShares = s.getNum()-num;
            //  System.out.println("Buying "+num+"from "+s.getUid()+"!!");
            // System.out.println("^That menas that s.getPriceForNum(num) = "+s.getPriceForNum(num));
            // System.out.println("H 1!");
            setSharesIdea(s.getUid(),s.getIid(),resultingShares,s.getPrice(),c);
            //System.out.println("H 2!");
            insertIntoHistory(uid, s.getUid(), num,s.getPrice(),c,iid);
            //System.out.println("H 3!");
            setUserMoney(s.getUid(), getUserMoney(s.getUid()) + s.getPriceForNum(num), c);
            //System.out.println("H 4!");
        }

        //System.out.println("Before setSharesIdea");
        setSharesIdea(uid,iid,startingShares+totalSharesBought,targetSell,c);


        //System.out.println("Before setUserMoney");
        setUserMoney(uid,userMoney, c);

        //Set it to the last transaction price that happened
        if (s != null)
            setMarketValue(iid,s.getPrice(),c);

        ret.numSharesBought = totalSharesBought;
        ret.numSharesFinal  = startingShares+totalSharesBought;
        ret.totalSpent      = totalSpent;

        // Set the selling price
        //System.out.println("Going to setPricesSharesInternal!");
        if ( ret.numSharesBought > 0)
            setPricesSharesInternal(iid, uid, targetSell, c, false);
        //System.out.println("After setPricesSharesInternal!");

        // UNLEASH THE BEAST!
        if ( conn == null )
            returnTransactionalConnection(c);
        //System.out.println("CC!");
        //Handle notifications here
        //if ( generateNotifications ) FIXME

        //System.out.println("Our callbacks: ");
        //      for ( int i : callbacks.keySet())
//        System.out.println("--->" + i+ " "+callbacks.get(i));
        //System.out.println("END CALLBACKS");

        for (int i = 0; i < sharesToBuy.size(); i++) {
            s = sharesToBuy.get(i);
            //          System.out.println("To buy: "+s);
//            System.out.println("(Buying): "+sharesToBuyNum.get(i));

            //     System.out.println("CC2");
            try {
                if ( callbacks.containsKey(s.getUid()) )
                    callbacks.get(s.getUid()).notify(getUsername(uid),
                                                 "SOLD",
                                                 getUserMoney(s.getUid()),
                                                 s.getPrice(),
                                                 sharesToBuyNum.get(i),
                                                 iid,
                                                 s.getAvailableShares()-sharesToBuyNum.get(i),
                                                 s.getPrice());
            } catch (Exception e) {
                //      System.err.println("EXCEPTION REMOTE: "+e+" "+e.getMessage()+"\n"+e.getCause());
                e.printStackTrace();
            }
            //System.out.println("CC3");
            if ( callbacks.containsKey(uid) ) {

                try {
                callbacks.get(uid).notify(getUsername(s.getUid()),
                                          "BOUGHT",
                                          getUserMoney(uid),
                                          s.getPrice(),
                                          ret.numSharesBought,
                                          iid,
                                          ret.numSharesFinal,
                                          targetSell);
                } catch (Exception e) {
                    //  System.err.println("EXCEPTION REMOTE2: "+e+" "+e.getMessage()+"\n"+e.getCause());
                    e.printStackTrace();
                }
            }
            //   System.out.println("CC4");
        }


        if ( ideaFacebookId != null ) {
            String url = "https://graph.facebook.com/" + ideaFacebookId + "/comments";
            String message = getUsername(uid)+ " comprou " + ret.numSharesBought + " shares da ideia "
                    + iid + " por " + ret.totalSpent + " DEICoins!";
            String clientToken = tokens.get(uid);

            doFacebookPostComment(url, message, clientToken);
        }
        if ( ret.result.isEmpty() )
            ret.result = "OK";
        // System.out.println("I'm leaving! " + ret);
        return ret;

    }

    private int countAvailableShares(ArrayList<Share> shares) {
        int ret = 0;
        for (Share s : shares)
            ret += s.getAvailableShares();

        //System.out.println("-->countAvailableShares: "+ret);
        return ret;
    }


    public BuySharesReturn buyShares(int uid, int iid, float maxPricePerShare, int buyNumShares,
                                     boolean addToQueueOnFailure, float targetSellPrice) throws RemoteException {
        BuySharesReturn ret = tryBuyShares(uid,iid,maxPricePerShare,buyNumShares,addToQueueOnFailure,targetSellPrice,
                                           null, false);
        //System.out.println("Got out of tryBuyShares");
        if ( ret.result.contains("QUEUED.") ) {
            // Need to queue! But how many? we can calculate them
            //System.out.println("Need to add to queue!");
            Connection conn = getTransactionalConnection();
            checkQueue(conn);
            insertIntoQueue(uid,iid,buyNumShares-ret.numSharesBought,maxPricePerShare,targetSellPrice,conn);
            returnTransactionalConnection(conn);
        } else if ( ret.result.equals("OK") ) {
            Connection conn = getTransactionalConnection();
            checkQueue(conn);
            returnTransactionalConnection(conn);
        }
        return ret;
    }

    /**
     * Method responsible for getting the list of topics for a given idea
     * @param iid   The id of the idea
     * @return  An array of ServerTopic objects, containing all the topics where the given idea is present
     * @throws RemoteException
     */
    public ServerTopic[] getIdeaTopics(int iid) throws RemoteException{
        String query = "Select * from TopicoIdeia t where t.iid = " + iid;
        ArrayList<String[]> queryResult = receiveData(query), topic;
        ServerTopic[] listTopics;

        if (queryResult == null)
            return null;

        listTopics = new ServerTopic[queryResult.size()];
        for (int i=0;i<queryResult.size();i++){
            query = "Select t.tid, t.nome, t.userid, count(i.tid) from Topico t, TopicoIdeia i " +
                    "where i.tid = t.tid and t.tid = " + queryResult.get(i)[0] + " group by t.tid, t.nome, t.userid";
            topic = receiveData(query);
            listTopics[i] = new ServerTopic(topic.get(0));
        }

        return listTopics;
    }

    /**
     * Build an idea from an IID. Notice that this constructor only gathers IID (which we already had), title and body.
     * If one wants parent topics, ideas or children ideas, one must call
     * addChildrenIdeasToIdea(), addParentIdeasToIdea() and addParentTopicsToIdea()
     * @param iid   The id of the idea we want to build
     * @return  An Idea object, with the idea we just built
     * @throws RemoteException
     */
    public Idea getIdeaByIID(int iid, int uid) throws RemoteException {
        String query = "Select * from Ideia t where t.iid = " + iid + " and t.activa = 1";
        ArrayList<String[]> queryResult = receiveData(query);
        Idea devolve;

        if (queryResult.isEmpty())
            return null;

        devolve = new Idea(queryResult.get(0));
        devolve.setTopics(getIdeaTopics(devolve.getId()));

        if (getFile(iid) != null)
            devolve.setFile("Y");

        addUserSpecificInfoToIdea(devolve, uid); //Even if uid == -1, it's okay.
        return devolve;
    }


    synchronized public ArrayList<Idea> getIdeasCanBuy(int uid) throws RemoteException{
        // CAGUEI. E ANDEI.
        return null;
    }

    /**
     * Gets all the ideas with the specified id and title
     * @param iid   The id of the idea
     * @param title The title of the idea
     * @return An array of Idea objects, with all the ideas with the specified id and title
     * @throws RemoteException
     */
    public Idea[] searchIdeas(int uid, int iid, String title) throws RemoteException{
        String query;
        Idea[] devolve;

        if (iid > 0 && !title.equals(""))
            query = "Select * from Ideia i where i.activa = 1 and i.iid = " + iid +" and i.titulo LIKE '%" + title + "%'";
        else if(iid > 0)
            query = "Select * from Ideia i where i.activa = 1 and i.iid = " + iid;
        else if (!title.equals(""))
            query = "Select * from Ideia i where i.activa = 1 and i.titulo LIKE '%" + title + "%'";
        else
            return null;

        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult.isEmpty())
            return null;

        devolve = new Idea[queryResult.size()];
        for (int i=0;i<queryResult.size();i++){
            devolve[i] = new Idea(queryResult.get(i));
            devolve[i].setTopics(getIdeaTopics(devolve[i].getId()));
            addUserSpecificInfoToIdea(devolve[i],uid);
            if (getFile(devolve[i].getId()) != null)
                devolve[i].setFile("Y");
        }

        return devolve;
    }

    /**
     * Gets a topic, with a given id and name
     * @param tid   The id of the topic
     * @param name  The name of the topic
     * @return  The topic with the specified id and name
     * @throws RemoteException
     */
    public ServerTopic getTopic(int tid, String name) throws RemoteException{
        String query;

        if (tid != -2 && !name.equals(""))
            query = "Select t.tid, t.nome, t.userid, count(i.tid) from Topico t, TopicoIdeia i " +
                    "where t.nome LIKE '%" + name +"%' and t.tid = " + tid + " and i.tid = t.tid" +
                    " group by t.tid, t.nome, t.userid";
        else if(tid != -2)
            query = "Select t.tid, t.nome, t.userid, count(i.tid) from Topico t, TopicoIdeia i " +
                    "where t.tid = " + tid + " and t.tid = i.tid group by t.tid, t.nome, t.userid";
        else if (!name.equals(""))
            query = "Select t.tid, t.nome, t.userid, count(i.tid) from Topico t, TopicoIdeia i " +
                    "where t.nome LIKE '%" + name + "%' and t.tid = i.tid group by t.tid, t.nome, t.userid";
        else
            return null;

        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult.isEmpty())
            return null;

        return new ServerTopic(queryResult.get(0));
    }

    /**
     * Given a topic id, return its title
     * @param tid The topic id
     * @return The name, or null if no such topic exists
     * @throws RemoteException
     */
    public String getTopicTitle(int tid) throws RemoteException {
        String query = "select nome from Topico where tid = "+tid;

        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult.isEmpty())
            return null;

        return queryResult.get(0)[0];
    }

    synchronized private boolean setPricesSharesInternal(int iid, int uid, float price, Connection conn,
                                                         boolean checkQueue
                                                         ) throws RemoteException{
        //System.out.println("setPricesSharesInternal iid="+iid+"\n"+
        //"uid="+uid+"\n"+
        //"price="+price+"\n"+
        //"conn="+conn+"\n"+
        //"checkQueue"+checkQueue);
        if ( getSharesIdeaForUid(iid,uid) == null)
            return false; // You have no shares!
        Connection c;
        if ( conn != null) c = conn;
        else c = getTransactionalConnection();

        String query = "Update \"Share\" set valor = " + price + " where userid = " + uid + " and iid = " + iid;
        insertData(query,c);
        //System.out.println("OKAY, updated!");
        if ( checkQueue )
            checkQueue(c);
        //System.out.println("OKAY, updated right after!");
        if ( conn == null )
            returnTransactionalConnection(c);
        //System.out.println("Leaving setpricesshares!");
        return true;
    }

    /**
     * Sets the price of the shares of the user for a given idea to a given value
     * @param iid The id of the idea
     * @param uid The id of the user that performs this operation
     * @param price The new price per share
     * @return a boolean value indicating if the operation went well or not
     * @throws RemoteException
     */
    synchronized public boolean setPricesShares(int iid, int uid, float price) throws RemoteException{
        return setPricesSharesInternal(iid, uid, price, null, true);
    }

    /**
     * Send to the Server the history of transactions for a given client
     *
     * @param uid The id of the user
     * @return an array of objects of type String containing the transactional history for the given user
     * @throws RemoteException
     */
    public TransactionHistoryEntry[] getHistory(int uid) throws RemoteException{
        TransactionHistoryEntry[] history;
        String query = "Select u.username, u2.username, t.valor, " +
                       "t.numShares, i.titulo, t.data, t.comprador from Transacao t, Ideia i, Utilizador u, " +
                       "Utilizador u2 " +
                       "where (t.comprador = " + uid + " or t.vendedor = " + uid + ") and t.iid = i.iid and " +
                       "u.userid=t.comprador and u2.userid=t.vendedor order by t.data DESC";
        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult == null)
            return null;

        history = new TransactionHistoryEntry[queryResult.size()];



        for (int i=0;i<queryResult.size();i++) {
            history[i] =new TransactionHistoryEntry(queryResult.get(i),uid);
        }

        return  history;
    }

    /**
     * Private method to check if an idea with the given IID exists
     * @param iid The ID of the idea whose existance we want to check
     * @return True if the given idea exists. False otherwise
     */
    private boolean ideaExists(int iid) {
        try {
            return getIdeaByIID(iid,-1) != null;
        } catch (RemoteException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return false; //FIXME: Should never happen
        }
    }

    /**
     * Get all the shares that are about this idea
     * @param iid The IID
     * @return The shares associated with the idea
     * @throws RemoteException
     */
    public ArrayList<Share> getSharesIdea(int iid) throws RemoteException {
        return getSharesIdea(iid,null);
    }

    private ArrayList<Share> getSharesIdea(int iid, Connection c) throws RemoteException {
        ArrayList<Share> shares = new ArrayList<Share>();
        if ( !ideaExists(iid) )
            return shares;
        String query = "select * from \"Share\" where iid="+iid;

        ArrayList<String[]> result = receiveData(query,c);

        for ( String[] row : result)
            shares.add(new Share(row));

        return shares;
    }


    private Share getSharesIdeaForUid(int iid, int uid, Connection c) throws RemoteException {
        if ( !ideaExists(iid) )
            return null;
        String query = "select * from \"Share\" where iid="+iid+" and userid="+uid;

        ArrayList<String[]> result = receiveData(query,c);

        if ( result.isEmpty() ) {
            return null;
        }

        return new Share(result.get(0));
    }

    /**
     * Get the shares of this user for this idea
     * @param iid  The id of the idea
     * @param uid  The id of the user whose number of shares of the given idea we want to know
     * @return A Share object with the info of the shares, or null in case there are no shares
     * @throws RemoteException
     */
    public Share getSharesIdeaForUid(int iid, int uid) throws RemoteException {
        return getSharesIdeaForUid(iid,uid,null);
    }

    private void sortByPrice(ArrayList<Share> shares) {
        //System.out.println("DUmping share owners:");
        for (Share s : shares) {
            //System.out.println(s);
        }
        Collections.sort(shares);
    }

    /**
     * Gets the money for the a given user
     * @param uid   The if of the user
     * @return  The money of the specified user
     * @throws RemoteException
     */
    public float getUserMoney(int uid) throws RemoteException {
        return getUserMoney(uid,null);
    }

    private float getUserMoney(int uid, Connection c) throws RemoteException {
        String query = "select dinheiro from Utilizador where userid="+uid;

        ArrayList<String[]> result = receiveData(query, c);

        if ( result.isEmpty() ) {
            //System.err.println("DB consistency has gone crazy!");
            return 0;
        }

        return Float.valueOf(result.get(0)[0]);
    }

    /**
     * Adds an idea to a user's watchlist.
     * @param iid   The id of the idea to be added to the user's watchlist.
     * @param uid   The user's id.
     * @throws RemoteException
     */
    public void addIdeaToWatchlist(int iid, int uid) throws RemoteException{
        String query = "Insert INTO IdeiaWatchList Values(" + uid + ", " + iid +")";

        insertData(query);
    }

    /**
     * Removes an idea from a user's watchlist.
     * @param iid   The id of the idea to be removed from the user's watchlist.
     * @param uid   The user's id.
     * @throws RemoteException
     */
    public void removeIdeaFromWatchlist(int iid, int uid) throws RemoteException{
        String query = "delete from IdeiaWatchList where userid = "+uid+" and iid="+iid;

        insertData(query);
    }

    @Override
    public void addCallbackToUid(int uid, RMINotificationCallbackInterface c) throws RemoteException {
        //System.out.println("Adding a new callback for "+uid+" "+getUsername(uid));
        callbacks.put(uid,c);
    }

    /**
     * Gets the administration role of a given user.
     * @param uid   The user's id.
     * @return      A boolean value, indicating if the user is as administrator (root) or just a simple user.
     * @throws RemoteException
     */
    public boolean getAdminStatus(int uid) throws RemoteException{
        boolean devolve = false;
        String query = "Select funcao from Utilizador where userid=" + uid;
        ArrayList<String[]> result = receiveData(query);


        if ( result.isEmpty() || result.get(0).length == 0 )
            return false; //WTF

        if ( result.get(0)[0].equals("1") )
            devolve = true;

        return devolve;
    }

    /**
     * Gets all the ideas of the Hall of Fame, stored in the database.
     * @return  An array of Idea objects, containing all the ideas of the Hall of Fame, stored in the database.
     */
    public Idea[] getHallOfFameIdeas() throws RemoteException{
        Idea[] devolve;
        String query = "Select * from Ideia i, HallFame h where i.iid = h.iid";
        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult == null || queryResult.isEmpty())
            return new Idea[0]; //FIXME: Never goes wrong

        devolve = new Idea[queryResult.size()];
        for (int i=0;i<queryResult.size();i++) {
            devolve[i] = new Idea(queryResult.get(i));
            devolve[i].setTopics(getIdeaTopics(devolve[i].getId()));
        }

        return devolve;
    }

    /**
     * Sets the money for a given user. The user must exist
     * @param uid   The id of the user
     * @param money The money we are going to set for the user
     * @param conn The connection to use, for instance, for transactional operations. null if don't care which
     *             connection to use
     */
    private void setUserMoney(int uid, float money, Connection conn) {
        String query = "update Utilizador set dinheiro="+money+" where userid="+uid;

        if ( conn == null )
            insertData(query);
        else
            insertData(query, conn);
    }

    /**
     * Gets the username of a given user, stored in the database
     * @param uid The id of the user
     * @return The username of the user specified by "uid"
     * @throws RemoteException
     */
    public String getUsername(int uid) throws RemoteException {
        String query = "select username from  Utilizador where userid="+uid;

        ArrayList<String[]> result = receiveData(query);
        if ( result== null || result.isEmpty())
            return null;

        return result.get(0)[0];
    }

    synchronized private boolean setMarketValue(int iid, float value, Connection c) {
        String query = "UPDATE Ideia set ultimatransacao = "+value+" where iid="+iid;

        insertData(query,c);
        Set<Integer> keys = callbacks.keySet();
        ArrayList<Integer> toRemove = new ArrayList<Integer>();
        for (int id : keys) {
            //System.out.println("setMarketValue id: "+id);
            try {
                callbacks.get(id).notifyNewMarketValue(iid, value);
            } catch (Exception e) {
                //System.err.println("OPa client probably dead");
                toRemove.add(id);
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                //System.out.println("Continuing");
            }
            //System.out.println("cccc2");
        }
        //System.out.println("For out!");
        for (int id : toRemove)
            callbacks.remove(id);
        //System.out.println("Leaving setMarketValue!");
        return true;
    }
    public float getMarketValue(int iid) throws RemoteException {
        String query = "select ultimatransacao from Ideia where iid="+iid;
        Connection c;
        try {
            c = connectionPool.checkOutConnection();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return -1;
        }
        ArrayList<String[]> ans = receiveData(query,c);
        connectionPool.returnConnection(c);
        return Float.valueOf(ans.get(0)[0]);
    }

    /**
     * Insert a buying order into the buySharesQueue.
     * @param userid            The id of the user requesting the transaction
     * @param iid               The id of the idea being transactionated
     * @param num               The number of shares in the transaction
     * @param maxpricepershare  The maximum price the buyer wants to pay for the shares
     * @param conn              Connection to the database
     * @return                  In case of success, returns the id of the transaction inserted in the database. If an
     *                          error occurred returns -1;
     */
    synchronized private int insertIntoQueue(int userid, int iid, int num, float maxpricepershare,
                                             float targetSellPrice, Connection conn){
        String query = "INSERT INTO Compra VALUES (fila_seq.nextval," + userid + "," + iid + "," + num + "," +
                maxpricepershare + "," + targetSellPrice + ")";

        insertData(query,conn);

        query = "Select fila_seq.currval from DUAL";
        ArrayList<String[]>queryResult = receiveData(query,conn);

        if (queryResult == null || queryResult.isEmpty())
            return -1;

        return Integer.valueOf(queryResult.get(0)[0]);
    }

    synchronized private void updateQueueEntry(int id, float num, Connection conn){
        String query = "UPDATE Compra SET num="+num+" where compra_id="+id;

        insertData(query,conn);
    }

    /**
     * Remove a buying order from the buyShareQueue.
     * @param id    The id of the user requesting the transaction
     * @param conn  Connection to the database
     */
    synchronized private void removeFromQueue(int id,Connection conn){
        String query = "Delete from Compra where compra_id = " + id;

        insertData(query,conn);
    }

    /**
     * Returns all the queued transactions in the database.
     * @param conn  The connection to the database.
     * @return      An array of String objects, containing the fields correspondent to the Transaction Queue, stored
     *              in the database.
     */
    private synchronized ArrayList<String[]> getQueue(Connection conn) {
        String query = "Select * from Compra order by compra_id ASC";
        ArrayList<String[]> queryResult = receiveData(query,conn);

        if (queryResult == null || queryResult.isEmpty())
            return null;
        return queryResult;
    }


    private synchronized void checkQueue(Connection conn) {
        ArrayList<String[]> queue = getQueue(conn);

        if ( queue == null )
            return;

        for ( int i = 0; i < queue.size(); i++ ) {
            //System.out.println("Iteration "+i);
            String[] row = queue.get(i);

            int id = Integer.valueOf(row[0]);
            int uid = Integer.valueOf(row[1]);
            int iid = Integer.valueOf(row[2]);
            int num = Integer.valueOf(row[3]);
            float maxpriceshare = Float.valueOf(row[4]);
            float sellingPrice = Float.valueOf(row[5]);
            //System.out.println("cc "+i);
            BuySharesReturn ret;
            try {
                ret = tryBuyShares(uid, iid, maxpriceshare, num, true, sellingPrice,conn,true);
            } catch (RemoteException ignored) {
                return;
            }

            //System.out.println("cc2 "+i);
            if ( ret.result.equals("OK") ) {
                //System.out.println("We did it! " + ret);
                // Remove it from the queue, we've processed it
                removeFromQueue(id, conn);
                queue.remove(i);

                //Start again, as some of the other queued transactions may potentially take place now
                i = 0;
            } else if (ret.result.contains("QUEUE")) {
                updateQueueEntry(id, num-ret.numSharesBought,conn);
            }
        }
}

    /**
     * Set up the number of shares for a given idea, and the price of each share for that idea
     * @param uid   The User's id
     * @param iid   The id of the idea
     * @param nshares   The number of shares to set for the given idea
     * @param price The price of each share for the given idea
     * @throws RemoteException
     */
    synchronized public void setSharesIdea(int uid, int iid, int nshares, float price)throws RemoteException{
        /* null here means no transactional connection */
        setSharesIdea(uid, iid, nshares,price,null);
    }

    /**
     * Set up the number of shares for a given idea, and the price of each share for that idea
     * @param uid   The id of the user who requested the operation
     * @param iid   The id of the idea whose number of shares we are going to edit
     * @param nshares The number of shares we are going to assign to the given idea
     * @param price   The price of each share of the idea
     * @param conn Connection to the RMI Server
     * @throws RemoteException
     */
    private synchronized void setSharesIdea(int uid, int iid,int nshares, float price,
                                            Connection conn)throws RemoteException{
        String query = "select * from \"Share\" where userid="+uid+" and "+"iid="+iid;
        //System.out.println("SSI");
        ArrayList<String[]> result = ((conn == null) ? receiveData(query) : receiveData(query, conn));
        //System.out.println("SSII");
        if ( !result.isEmpty() ) {
            // This already exists, we should just update it

            if ( nshares == 0 ) {
                // Set to 0!! We should delete it!
                query = "delete from \"Share\" where userid="+uid+" and iid="+iid;
            } else {
                query = "update \"Share\" set numshares="+nshares+" where iid="+iid+" and userid="+uid;
            }
        } else
            query = "INSERT INTO \"Share\" VALUES (" + iid + "," + uid + "," + nshares + "," + price + ")";

        //System.out.println("SSIII");

        if ( conn == null )
            insertData(query);
        else
            insertData(query,conn);

    }

    /**
     * Method responsible for registering a transaction in the database
     * @param uidBuyer Id of the user who bought shares
     * @param uidSeller Id of the user who sold shares
     * @param nshares  Number of shares involved in the transaction
     * @param price  Price of the shares bought
     * @param conn  Connection to the RMI Server
     * @param iid Id of the idea whose shares were involved in the transaction
     */
    synchronized private void insertIntoHistory(int uidBuyer, int uidSeller, int nshares, float price, Connection conn,
                                                int iid) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        Date transactionDate = new Date();
        String query = "Insert Into Transacao values(transaction_seq.nextval," + uidBuyer + "," + uidSeller + "," + price
                + "," + nshares + "," + iid,  sDate = format1.format(transactionDate);

        query = query + ", to_date('" +  sDate + "','yyyy:mm:dd:hh24:mi:ss'))";
        if ( conn == null )
            insertData(query);
        else
            insertData(query,conn);
    }

    /**
     * Method responsible for updating the time when the user was logged in
     * @param uid The id of the user
     * @throws RemoteException
     */
    public void updateUserTime(int uid) throws RemoteException{
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        Date transactionDate = new Date();
        String sDate= format1.format(transactionDate), queryData;
        ArrayList<String[]> queryDataResult;

        queryData = "Update Utilizador set dataUltimoLogin = to_date('" + sDate +
                "','yyyy:mm:dd:hh24:mi:ss') where userid = " + uid;
        insertData(queryData);
    }

    synchronized private boolean setTopicsIdea(int iid, String topicTitle, int uid,
                                               Connection conn) throws RemoteException{
        Connection c;
        if ( conn == null ) c = getTransactionalConnection();
        else c = conn;

        String query ;
        ArrayList<String[]> topics;
        int topic_id;

        //ideas = receiveData(query);
        query = "Select t.tid from Topico t where t.nome='" + topicTitle + "'";
        topics = receiveData(query,c);

        ////
        //  There is no topic with the given title, so let's create it
        ////
        if (topics.isEmpty()){
            createTopic(topicTitle,uid);

            //Add the number of the topic to the ArrayList
            query = "Select t.tid from Topico t where t.nome='" + topicTitle + "'";
            topics = receiveData(query,c);
        }

        topic_id = Integer.valueOf(topics.get(0)[0]);//Get topic id

        query = "INSERT INTO TopicoIdeia VALUES (" + topic_id + "," + iid + ")";
        //System.out.println("Antes do insert");
        insertData(query,c);
        // System.out.println("Antes do true");

        if ( conn == null )
            returnTransactionalConnection(c);

        return true; //Change this to return....nothing?
    }

    /**
     * Method responsible for creating the connection between an idea and one or more topics
     * @param iid   The id of the idea
     * @param topicTitle    The title of the topics
     * @param uid   The id of the user
     * @return A boolean value, indicating the success or failure of the operation
     * @throws RemoteException
     */
    synchronized public boolean setTopicsIdea(int iid, String topicTitle, int uid) throws RemoteException{
        return setTopicsIdea ( iid, topicTitle, uid, null);
    }

    synchronized private void addToHallOfFame(int iid, Connection c) {
        String query = "INSERT INTO HallFame VALUES (fame_seq.nextval," + iid + ")";
        insertData(query, c);
        query = "update Ideia set activa=0 where iid = "+iid;
        insertData(query, c);
    }

    synchronized public void takeOver(int iid) throws RemoteException {
        Connection c = getTransactionalConnection();
        takeOver(iid, c);
        returnTransactionalConnection(c);
    }

    synchronized private void takeOver(int iid, Connection c) throws RemoteException {
        ArrayList<Share> currentShares = getSharesIdea(iid, c);
        float marketPrice = getMarketValue(iid);

        for ( Share s : currentShares ) {
            //System.out.println("Taking over "+s.getAvailableShares()+"from "+s.getUid()+"!!");
            //System.out.println("T 1!");
            setSharesIdea(s.getUid(),s.getIid(),0,s.getPrice(),c);
            //System.out.println("T 3!");
            setUserMoney(s.getUid(), getUserMoney(s.getUid()) + s.getAvailableShares()*marketPrice, c);
            //System.out.println("T 4!");

            try {
                if ( callbacks.containsKey(s.getUid()) )
                    callbacks.get(s.getUid()).notifyTakenOver(iid, marketPrice, s.getAvailableShares()*marketPrice);
            } catch (Exception e) {
                //System.err.println("EXCEPTION REMOTE TAKEOVER: "+e+" "+e.getMessage()+"\n"+e.getCause());
                e.printStackTrace();
            }
        }

        addToHallOfFame(iid, c);
        //Need to check the queue, as we may have given enough money to a user...
        checkQueue(c);
    }


    /**
     * This just picks any connection available from the pool and uses it. If you need transactional support you can
     * specify a connection in another overloaded method
     * @param query The query to execute
     * @return  An ArrayList of String[] objects (Array of String objects) with the result obtained for the given query
     */
    private ArrayList<String[]> receiveData(String query) {

        Connection conn = null;

        boolean cont;
        do {
            cont = false;
            try {
                conn = connectionPool.checkOutConnection();
            } catch (SQLException e) {
                //System.err.println("Error checking out connection for receiveData");
                cont = true;
            }
        } while ( cont );
        ArrayList<String[]> ret = receiveData(query, conn);
        if ( conn != null )
            connectionPool.returnConnection(conn);
        return ret;
    }

    /**
     * Method responsible for executing queries like "Select..."
     * @param query The query to execute.
     * @param conn  The connection to the database.
     * @return      Returns null on failure, and on success returns an Arraylist with all columns (as strings in an array)
     *              which may be empty if there query produces an empty table.
     */
    private ArrayList<String[]> receiveData(String query, Connection conn) {
        if ( conn == null ) return receiveData(query);
        int columnsNumber, pos = 0;
        ArrayList<String[]> result = new ArrayList<String[]>();
        Statement statement = null;

        //System.out.println("\n-------------------------------\nRunning query: "+query);

        boolean cont;
        do {
            cont = false;
            try {
                statement = conn.createStatement();
            } catch (SQLException e) {
                //System.err.println("Error creating SQL statement '" + query + "'!");
                cont = true;
            }
        } while ( cont );

        ResultSet rs;
        ResultSetMetaData rsmd;
        do {
            cont = false;
            try {
                rs = statement.executeQuery(query);
                rsmd = rs.getMetaData();
                columnsNumber = rsmd.getColumnCount();//Get number of columns
                //System.out.println("Query's result has " + columnsNumber + " columns");
                while (rs.next()){
                    result.add(new String[columnsNumber]);
                    for (int i=1;i<=columnsNumber;++i){
                        result.get(pos)[i-1] = rs.getString(i);
                    }
                    pos++;
                }
                statement.close();
            } catch (SQLException e) {
                //System.err.println("Error executing SQL query '" + query + "'!");
                cont = true;
            }
        } while ( cont );
        //System.out.println("-------------------------------DONE");
        return result;
    }

    /**
     * Get a transactional connection from the pool, ready to use.
     * @return The transactional connection
     */
    private Connection getTransactionalConnection() {
        Connection connection;
        try {
            connection = connectionPool.checkOutConnection();
        } catch (SQLException e) {
            //System.err.println("Error checkout out a new connection for transactions!");
            return null;
        }

        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            //System.err.println("Error setting autocommit!");
        }

        return connection;
    }

    /**
     * Return and effectively commit the changes on a transactional connection
     * @param c The connection to the database
     */
    private void returnTransactionalConnection(Connection c) {
        try {
            c.commit();
            c.setAutoCommit(true);
        } catch (SQLException e) {
            //System.err.println("Error setting autocommit to true!");
        }
        connectionPool.returnConnection(c);
    }

    /**
     * This is used for most queries because it grabs any available connection. One might want to use the specialized
     * version which can operate on a given connection (for things such as transactions...)
     * @param query The query being executed in the database.
     */
    private void insertData(String query) {
        Connection conn = null;
        try {
            conn = connectionPool.checkOutConnection();
        } catch (SQLException e) {
            //System.err.println("Error checking out connection for insertData");
        }
        insertData(query, conn);
        if ( conn != null )
            connectionPool.returnConnection(conn);
    }

    /**
     * This method will be responsible for executing a query like "Insert ...". With this method we can create new
     * registries in the database's tables
     * @param query The query we want to execute in the database
     * @param conn  The connection to the database
     */
    private void insertData(String query, Connection conn) {
        if ( conn == null ) {insertData(query);return;}
        //System.out.println("\n-------------------------------\nRunning inseeeeeert query: "+query);
        boolean cont;

        do {
            cont = false;
            try{
                conn.createStatement().executeUpdate(query);
            }catch(SQLException s){
                //System.err.println("SQLException in the insertData method: "+s.getMessage());

                cont = true;
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }

        } while ( cont );

        //System.out.println("-------------------------------DONE");
    }

    private void execute(){

        ////
        //  Store the database ip, username and password in a file or store it in "the properties" stuff the teacher said???
        ////

        String username = "sd", password = "sd";

        try{

            connectionPool = new ConnectionPool(url, username, password);
            Class.forName("oracle.jdbc.driver.OracleDriver"); //This could be moved to the connectionPool but we had
            // some issues so right now we're just praying that it
            // works.

            if (connectionPool == null) {
                //System.out.println("Failed to make connection!");
                return ;
            }

            //System.out.println("You made it, take control your database now!");

            //Start RMIRegistry programmatically
            Registry r = LocateRegistry.createRegistry(7000);
            r.rebind("academica", this);

            //System.out.println("Server ready :)");

        } catch(RemoteException e){
            e.printStackTrace();
        } catch (SQLException s){
            s.printStackTrace();
        } catch(ClassNotFoundException c){
            c.printStackTrace();
        }
    }


    public static void main(String[] args) {
        System.getProperties().put("java.security.policy", "policy.all");
        System.setSecurityManager(new RMISecurityManager());
        String db = "192.168.56.120";
        if ( args.length == 1)
            db = args[0];
        try{
            RMI_Server servidor = new RMI_Server(db,"1521","XE");
            servidor.execute();
        }catch(RemoteException r){
            //System.out.println("RemoteException on the main method of the RMI Server");
        }
    }
}
