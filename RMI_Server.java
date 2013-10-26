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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

////
//  This is the RMI Server class, which will be responsible for interacting with the database, allowing the TCP Servers to commit
//  queries and other commands to the database
////
public class RMI_Server extends UnicastRemoteObject implements RMI_Interface{

    private static final String requestsQueueFilePath = "requests.bin";
    private String url;
    private int num_users;
    private int num_topics;
    private int num_ideas;
    private int num_transactions;
    private final int starting_money = 10000;
    private ConnectionPool connectionPool;
    private int lastFile = 0;

    private static final long serialVersionUID = 1L;
    private final Object notificationsLock = new Object();
    private TransactionQueue transactionQueue;

    /**
     * Hashes the password using MD5 and returns it.
     * @param pass The plaintext password to be hashed.
     * @return The hashed password.
     */
    private String hashPassword(String pass)
    {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Cannot find hashing algorithm: " + e);
            System.exit(-1);
        }
        if(m == null)
        {
            System.out.println("Cannot find hashing algorithm.");
            System.exit(-1);
        }

        m.reset();
        m.update(pass.getBytes());

        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1,digest);
        String hashText = bigInt.toString(16);

        while(hashText.length() < 32)
            hashText = "0" + hashText;

        return hashText;
    }

    public boolean sayTrue() throws RemoteException {
        return true;
    }

    ////
    //  Class constructor. Creates a new instance of the class RMI_Server
    ////
    private RMI_Server(String servidor, String porto, String sid) throws RemoteException {
        super();
        this.url = "jdbc:oracle:thin:@" + servidor + ":" + porto + ":" + sid;
        readLastFile();
        transactionQueue = new TransactionQueue(this);
        //new Thread(transactionQueue).start();
    }

    ////
    //  Method responsible for checking a given username and corresponding password, in order to check if the user is registered
    //  and if that is the case, confirm his (or hers) login.
    ////
    public int login(String user, String pwd) throws RemoteException {

        String query;
        ArrayList<String[]> result;
        pwd = hashPassword(pwd);
        query = "select u.userid from Utilizadores u where u.username = '" + user + "' and u.pass = '" + pwd + "'";

        result = receiveData(query);

        System.out.println("Query: " + query + " has " + result.size() + " results");

        if ( !result.isEmpty() )//Return the user's id
            return Integer.valueOf(result.get(0)[0]);
        else
            return -1;
    }

    /**
     * This is the same as login, except it doesn't do anything to say we're online. It's meant to be used by the
     * notification server.
     * @param user
     * @param pwd
     * @return
     * @throws RemoteException
     */
    public int canLogin(String user, String pwd) throws  RemoteException {
        String query;
        ArrayList<String[]> result;
        pwd = hashPassword(pwd);
        query = "select u.userid from Utilizadores u where u.username = '" + user + "' and u.pass = '" + pwd + "'";

        result = receiveData(query);

        System.out.println("Query: " + query + " has " + result.size() + " results");

        if ( !result.isEmpty() )//Return the user's id
            return Integer.valueOf(result.get(0)[0]);
        else
            return -1;
    }

    ////
    //  Method responsible for getting all the topics stored in the database.
    ////
    public ServerTopic[] getTopics() throws RemoteException{
        String query = "select * from Topicos";

        ArrayList<String[]> result = receiveData(query);

        if ( result.size() == 0 )
            return null;

        ServerTopic[] topics = new ServerTopic[result.size()];

        for (int i = 0; i < result.size(); i++)
            topics[i] = new ServerTopic(result.get(i));

        return topics;
    }

    ////
    //  Returns all the ideas associated with a given user
    ////
    public Idea[] getIdeasFromUser(int uid) throws RemoteException{
        Idea[] ideas;
        String query = "Select i.iid,i.titulo,i.descricao,i.userid from Ideias i, Shares s where i.activa = 1 and s.iid = i.iid" +
                " and s.userid = " + uid;
        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult.size() == 0)
            return null;

        ideas = new Idea[queryResult.size()];

        for (int i = 0; i < queryResult.size(); i++){
            ideas[i] = new Idea(queryResult.get(i));
            if(getFile(Integer.parseInt(queryResult.get(i)[0])) != null)
                ideas[i].setFile("Y");
        }

        return ideas;
    }

    ////
    // This returns an array of ideas which belong to this Topic.
    //
    public Idea[] getIdeasFromTopic(int tid) throws RemoteException{
        String query = "select e.iid, e.titulo, e.descricao, e.userid, e.activa from Ideias e, " +
                "TopicosIdeias t where t.iid = e.iid and t" +
                ".tid = "+tid+" and e.activa = 1";

        ArrayList<String[]> result = receiveData(query);

        if ( result.size() == 0 )
            return null;

        Idea[] ideas = new Idea[result.size()];

        for (int i = 0; i < result.size(); i++){
            ideas[i] = new Idea(result.get(i));

            if(getFile(Integer.parseInt(result.get(i)[0])) != null)
                ideas[i].setFile("Y");
        }

        return ideas;
    }

    ////
    // Method responsible for checking if there aren't any topics already created with the same name as the one we want
    //  to create
    ////
    boolean validateTopic(String nome){
        String query = "Select * from Topicos t where t.nome = '" + nome + "'";
        ArrayList<String[]> topics = receiveData(query);

        // NOTE: topics will only be null if the query failed. And we should assume that never happens...
        return topics == null || topics.size() == 0;
    }

    ////
    //  Method responsible for validating a user's username, before adding it to the database
    ///
    boolean validateData(String username){
        String query = "Select * from Utilizadores u where u.username = '" + username + "'";
        ArrayList<String[]> users = receiveData(query);

        // NOTE: users will only be null if the query failed. And we should assume that never happens...
        return users == null || users.size() == 0;

    }

    /**
     *  Method responsible for insering a new user in the database
     * @param user Username
     * @param pass Password
     * @param email User's email address
     * @param date Date of the registration
     * @return  Boolean value, indicating if the operation went well
     * @throws RemoteException
     */
    synchronized public boolean register(String user, String pass, String email, String date) throws RemoteException {
        if (! validateData(user)){
            System.err.println("O validate data devolveu false");
            return false;
        }
        pass = hashPassword(pass);

        String query = "INSERT INTO Utilizadores VALUES (" + (num_users+1) + ",'" + email + "','" + user + "'," +
                "'" + pass +
                "'," + starting_money + ",to_date('" + date + "','yyyy.mm.dd'), null)";

        insertData(query);

        num_users++;

        return true;
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
            System.err.println("Should never happen!");
            return false;
        }

        String query = "Insert into IdeiasFicheiros values (" + iid + ",'" + path + "','"+file.getName()+"')";

        insertData(query);

        updateLastFile();
        return true;
    }

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
            System.err.println("IO Exception while reading lastFile filefile!");
        } finally {
            try { f.close(); } catch (IOException ignored) {}
        }
    }

    /**
     * Gets the file associated with a given idea
     * @param iid The id of the idea in the database
     * @return NetworkingFile, containing the file associated with the idea
     * @throws RemoteException
     */
    public NetworkingFile getFile(int iid) throws RemoteException {
        String query = "select path, OriginalFile from IdeiasFicheiros where iid ="+iid;
        ArrayList<String[]> queryResult = receiveData(query);
        if ( queryResult.isEmpty() )
            return null;

        try {
            return new NetworkingFile(queryResult.get(0)[0],queryResult.get(0)[1]);
        } catch (FileNotFoundException e) {
            System.err.println("Shouldn't happen! DB corrupted?");
            return null;
        }
    }

    /**
     * Gets all the ideas with files attached
     * @return An array of objects "Idea" with the ideas that have fles attached
     * @throws RemoteException
     */
    public Idea[] getFilesIdeas()throws RemoteException{
        String query = "Select i.iid, f.titulo, f.descricao, f.userid from IdeiasFicheiros i, Ideias f where i.iid = f.iid";
        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult == null || queryResult.size() == 0)
            return null;

        Idea[] listIdeasFiles = new Idea[queryResult.size()];

        for (int i=0;i<queryResult.size();i++){
            listIdeasFiles[i] = new Idea(queryResult.get(i));
        }

        return listIdeasFiles;
    }

    ////
    //  Method responsible for getting all the information about all the shares of a given idea
    ////
    synchronized public String[] getIdeaShares(int iid, int uid) throws RemoteException{
        String query = "Select s.iid, s.numshares, s.valor, s.numMin from Shares s where s.iid = " + iid +
                " and s.userid = " + uid;
        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult == null || queryResult.size()==0 )
            return null;

        return queryResult.get(0);
    }

    ////
    // Method responsible for getting all the ideas in favour, neutral or against a given idea
    ////
    public Idea[] getIdeaRelations(int iid, int relationshipType) throws RemoteException{
        String query = "Select * from Ideias i, RelacaoIdeias r where r.iidfilho = i.iid and r.iidpai = " + iid +
                " and r.tipo_relacao = " + relationshipType;
        ArrayList<String[]> queryResult = receiveData(query);
        Idea[] devolve;

        if (queryResult.isEmpty())
            return null;

        devolve = new Idea[queryResult.size()];

        for (int i=0;i<queryResult.size();i++)
            devolve[i] = new Idea(queryResult.get(i));

        return devolve;
    }

    ////
    //  Method responsible for creating a new topic in the database
    ////
    synchronized public boolean createTopic(String nome, String descricao, int uid) throws  RemoteException{
        if (! validateTopic(nome)){
            System.err.println("Topico invalido");
            return false;
        }

        String query = "INSERT INTO Topicos VALUES (" + (num_topics+1) + ",'" + nome + "','" + descricao + "'," +
                "" + uid + ")";

        insertData(query);
        ++num_topics;
        return true;
    }

    ////
    //  Method responsible for creating a new idea in the database
    ////
    synchronized public int createIdea(String title, String description, int uid) throws RemoteException{
         String query;


        query = "INSERT INTO Ideias VALUES (" + (num_ideas+1) + ",'" + title + "','" + description + "'," +
                "" + uid + "," +
                "" + "1)";

        insertData(query);
        return ++num_ideas;
    }

    private boolean ideaHasFiles(int iid) {
        String query = "select * from IdeiasFicheiros i where i.iid = " + iid;
        ArrayList<String[]> queryResult = receiveData(query);

        return !queryResult.isEmpty();
    }

    private void deleteIdeaFiles(int iid) {
        String query = "select path from IdeiasFicheiros i where i.iid = " + iid;
        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult.size() > 0) {
            for (String[] row : queryResult ) {
                String filepath = row[0];
                File f = new File(filepath);
                f.delete();
            }
        }

    }

    boolean ideaHasChildren(int iid) throws RemoteException {
        String query = "select * from RelacaoIdeias t where t.iidpai = " + iid;
        ArrayList<String[]> queryResult = receiveData(query);

        return !queryResult.isEmpty();
    }

    /**
     * Removes an idea
     * @param idea Object "Idea" with the idea to be removed
     * @param uid  User that wants to remove the idea
     * @return We have 4 possible return values:
     * -1 -> Idea has no children
     * -2 -> User is not the owner of the idea
     * 1 > Everything went well
     * @throws RemoteException
     */
    public int removeIdea(Idea idea, int uid) throws  RemoteException {
        if ( ideaHasChildren(idea.id) ) {
            return -1;//Idea has children
        }

        //Check if user is owner of the idea
        String query = "Select s.userid from Shares s where s.iid = " + idea.id;
        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult.size() != 1)
            return -2;//User is not owner of the idea

        //Only has one owner
        else if (Integer.parseInt(queryResult.get(0)[0]) != uid )
                return -2;//User is not owner of the idea

        //Here we know that the user is the owner of the idea

        if ( ideaHasFiles(idea.id) ) {
            deleteIdeaFiles(idea.id);
        }

        query = "update Ideias set activa = 0 where iid="+idea.id;
        insertData(query);

        return 1;//Everything ok

    }

    ////
    //  Get the given idea's list of topics
    ////
    public ServerTopic[] getIdeaTopics(int iid) throws RemoteException{
        String query = "Select * from TopicosIdeias t where t.iid = " + iid;
        ArrayList<String[]> queryResult = receiveData(query), topic;
        ServerTopic[] listTopics;

        if (queryResult == null)
            return null;

        listTopics = new ServerTopic[queryResult.size()];
        for (int i=0;i<queryResult.size();i++){
            query = "Select * from Topicos t where t.tid = " + queryResult.get(i)[0];
            topic = receiveData(query);
            listTopics[i] = new ServerTopic(topic.get(0));
        }

        return listTopics;
    }

    ////
    // Build an idea from an IID. Notice that this constructor does not give us parent topic and ideas, it only gathers
    // IID (which we already had), title and body. If one wants parent topics, ideas or children ideas, one must call
    // addChildrenIdeasToIdea(), addParentIdeasToIdea() and addParentTopicsToIdea()
    //
    public Idea getIdeaByIID(int iid) throws RemoteException {
        String query = "select * from Ideias t where t.iid = " + iid + " and t.activa = 1";
        ArrayList<String[]> queryResult = receiveData(query);
        Idea devolve;

        if (queryResult.isEmpty())
            return null;

        devolve = new Idea(queryResult.get(0));

        if (getFile(iid) != null)
            devolve.setFile("Y");

        return devolve;
    }

    ////
    //  Indica se o primeiro array list tem alguma ideia com o referido id
    ////
    private int hasElement(ArrayList<Idea> listIdeas,String[] queryResult){
        int i;

        for (i=0;i<listIdeas.size();i++){
            if ( Integer.parseInt(queryResult[0]) == listIdeas.get(i).getId() )
                return i;
        }
        return -1;

    }

    synchronized public ArrayList<Idea> getIdeasCanBuy(int uid) throws RemoteException{
        ArrayList<Idea> devolve = new ArrayList<Idea>();
        String query = "Select i.iid, i.titulo, i.descricao, i.userid, s.numShares, s.numMin from Shares s, " +
                "Ideias i where s.userid != " +  uid + " and s.nummin < s.numshares and s.iid = i.iid";
        ArrayList<String[]> queryResult = receiveData(query);
        Idea temp;
        int index;

        if ( queryResult.isEmpty() )
            return null;

        else{
            for (String[] row : queryResult) {
                index = hasElement(devolve, row);
                if (index == -1) {
                    temp = new Idea(row);
                    temp.setSharesBuy(Integer.parseInt(row[4]) - (Integer.parseInt(row[5])));
                    devolve.add(temp);
                }
                else{
                    System.out.println("AQUI " + index);
                   devolve.get(index).addSharesToBuy(Integer.parseInt(row[4]) - (Integer.parseInt(row[5])));
                }
            }
        }
        return devolve;
    }

    public Idea[] getIdeaByIID(int iid, String title) throws RemoteException{
        String query;
        Idea[] devolve;

        if (iid != -1 && !title.equals(""))
            query = "Select * from Ideias i where i.activa = 1 and i.iid = " + iid +" and i.titulo LIKE '%" + title + "%'";
        else if(iid != -1)
            query = "Select * from Ideias i where i.activa = 1 and i.iid = " + iid;
        else if (!title.equals(""))
            query = "Select * from Ideias i where i.activa = 1 and i.titulo LIKE '%" + title + "%'";
        else
            return null;

        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult.size() == 0)
            return null;

        devolve = new Idea[queryResult.size()];
        for (int i=0;i<queryResult.size();i++){
            devolve[i] = new Idea(queryResult.get(i));
            if (getFile(devolve[i].getId()) != null)
                devolve[i].setFile("Y");
        }

        return devolve;
    }

    public ServerTopic getTopic(int tid, String name) throws RemoteException{
        String query;

        if (tid != -2 && !name.equals(""))
            query = "Select * from Topicos t where t.nome LIKE '%" + name +"%' and t.tid = " + tid;
        else if(tid != -2)
            query = "Select * from Topicos t where t.tid = " + tid;
        else if (!name.equals(""))
            query = "Select * from Topicos t where t.nome LIKE '%" + name + "%'";
        else
            return null;

        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult.size() == 0)
            return null;

        return new ServerTopic(queryResult.get(0));
    }

    ////
    //  Get the number of shares that a user doesnt want to sel for a given idea
    ////
    public int getSharesNotSell(int iid,int uid) throws RemoteException{
        String query = "Select s.numMin from Shares s where s.userid = " + uid + " and s.iid = " + iid;
        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult.size() == 0)
            return -2;

        return Integer.parseInt(queryResult.get(0)[0]);
    }

    /**
     * Sets the price of the shares of the user for a given idea to a given value
     * @param iid The id of the idea
     * @param uid The id of the user that performs this operation
     * @param price The new price per share
     * @return a boolean value indicating if the operation went well or not
     * @throws RemoteException
     */
    synchronized public boolean setPricesShares(int iid, int uid, int price) throws RemoteException{
        if ( getSharesIdeaForUid(iid,uid) == null)
            return false; // You have no shares!
        String query = "Update Shares set valor = " + price + " where userid = " + uid + " and iid = " + iid;
        Connection conn = getTransactionalConnection();
        insertData(query,conn);
        returnTransactionalConnection(conn);

        transactionQueue.checkQueue();

        return true;
    }

    /**
     * Sets the number of shares for a given idea that the user doesnt want to sell
     * @param iid Id of the idea in question
     * @param uid Id of the user requesting the operation
     * @param numberShares Number of shares that the user doesnt want to seel
     * @return A boolean value, indicating if the operation went well, or not
     * @throws RemoteException
     */
    synchronized public boolean setSharesNotSell(int iid, int uid, int numberShares)throws RemoteException{
        if ( getSharesIdeaForUid(iid,uid) == null)
            return false; // You have no shares!
        String query = "Update Shares set numMin = " + numberShares + " where userid = " + uid + " and iid = " + iid;
        Connection conn = getTransactionalConnection();
        insertData(query,conn);
        returnTransactionalConnection(conn);

        transactionQueue.checkQueue();

        return true;
    }

    /**
     * Send to the Server the history of transactions for a given client
     * @param uid The id of the user
     * @return an array of objects of type String containing the transactional history for the given user
     * @throws RemoteException
     */
    public String[] getHistory(int uid) throws RemoteException{
        String[] history;
        String query = "Select t.comprador, t.vendedor, t.valor*t.numShares, t.valor, " +
                "t.numShares, i.titulo, t.data from Transacoes t, Ideias i " +
                "where (t.comprador = " + uid + " or t.vendedor = " + uid + ") and t.iid = i.iid order by t.data";

        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult == null)
            return null;

        history = new String[queryResult.size()];



        for (int i=0;i<queryResult.size();i++)
            history[i] = queryResult.get(i)[5] + ": ID " + queryResult.get(i)[0] + " bought "+ queryResult.get(i)[4] +
                    " " +
                "shares from " + "ID "
                    + queryResult.get(i)[1] + " at " + queryResult.get(i)[3] + " DEI Coins per share, " +
                    "for a total of " + queryResult.get(i)[2] + " DEI Coins. Transaction date: " + queryResult.get(i)[6];

        return  history;
    }

    /**
     * Get all the shares that are about this idea
     * @param iid The IID
     * @return The shares associated with the idea
     * @throws RemoteException
     */
    public ArrayList<Share> getSharesIdea(int iid) throws RemoteException {
        ArrayList<Share> shares = new ArrayList<Share>();
        if ( getIdeaByIID(iid) == null)
            return shares;
        String query = "select * from Shares where iid="+iid;

        ArrayList<String[]> result = receiveData(query);

        for ( String[] row : result)
            shares.add(new Share(row));

        return shares;
    }

    /**
     * Get the shares of this user for this idea
     * @param iid  The id of the idea
     * @param uid  The id of the user whose number of shares of the given idea we want to know
     * @return A Share object with the info of the shares, or null in case there are no shares
     * @throws RemoteException
     */
    public Share getSharesIdeaForUid(int iid, int uid) throws RemoteException {
        if ( getIdeaByIID(iid) == null)
            return null;
        String query = "select * from Shares where iid="+iid+" and userid="+uid;

        ArrayList<String[]> result = receiveData(query);

        if ( result.isEmpty() ) {
            return null;
        }

        return new Share(result.get(0));
    }

    private void sortByPrice(ArrayList<Share> shares) {
        System.out.println("DUmping share owners:");
        for (Share s : shares) {
            System.out.println(s);
        }
        Collections.sort(shares);
    }

    /**
     * Gets the money for the given UID
     * @param uid
     * @return
     * @throws RemoteException
     */
    int getUserMoney(int uid) throws RemoteException {
        String query = "select dinheiro from Utilizadores where userid="+uid;

        ArrayList<String[]> result = receiveData(query);

        if ( result.isEmpty() ) {
            System.err.println("DB consistency has gone crazy!");
            return 0;
        }

        return Integer.valueOf(result.get(0)[0]);
    }

    /**
     * Sets the money for UID. UID must exist
     *
     * @param uid
     * @param money
     * @param conn The connection to use, for instance, for transactional operations. null if don't care which
     *             connection to use
     * @return
     * @throws RemoteException
     */
    private void setUserMoney(int uid, int money, Connection conn) {
        String query = "update Utilizadores set dinheiro="+money+" where userid="+uid;

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
        String query = "select username from  Utilizadores where userid="+uid;

        ArrayList<String[]> result = receiveData(query);
        if ( result== null || result.isEmpty())
            return null;

        return result.get(0)[0];
    }

    public boolean registerGetSharesRequest(int uid, int iid, int numShares, int targetPrice,
                                                    int minTargetShares)  throws RemoteException {
        System.out.println("registerGetSharesRequest called with uid="+uid+", iid="+iid+", numShares="+numShares+", " +
                ", targetPrice="+targetPrice+", minTargetShares="+minTargetShares);
        boolean ret = tryGetSharesIdea(uid, iid, numShares, targetPrice, minTargetShares);

        if ( !ret )
            transactionQueue.enqueue(new Transaction(uid, iid, numShares, targetPrice, minTargetShares));

        return ret;
    }

    /**
     * This tries to GET numShare shares. Notice that it doesn't try to BUY. If the user already has them,
     * it just quits and errors out.
     *
     * @param uid
     * @param iid
     * @param numShares
     * @param targetPrice  The desired target price of the shares of this user after buying. If the user already has
     *                     shares, then -2 means we should keep the price already set
     * @param minTargetShares The desired number of minimum shares that the user wants to keep after buying. If the
     *                        user already has shares, then -2 means we should keep the minimum already set
     * @return 1 on success, 0 on error (can't buy because there aren't any appropriate sellers....)
     * @throws RemoteException
     */
    synchronized public boolean tryGetSharesIdea(int uid, int iid, int numShares, int targetPrice,
                                                 int minTargetShares) throws
            RemoteException {
        System.out.println("tryGetSharesIdea called with uid="+uid+", iid="+iid+", numShares="+numShares+", " +
                ", targetPrice="+targetPrice+", minTargetShares="+minTargetShares);
        Share currentShares = getSharesIdeaForUid(iid, uid);
        int userMoney = getUserMoney(uid);
        int sharesAlvo = numShares;

        if ( currentShares != null) {
            // User already has shares
            if ( targetPrice == -2 )
                targetPrice = currentShares.getPrice();
            if (minTargetShares == -2)
                minTargetShares = currentShares.getNumMin();

            numShares -= currentShares.getNum();

            if ( numShares <= 0) {
                System.out.println("User tried to get X shares, but already has them!");
                return true; //Got them!
            }
        }

        ArrayList<Share> shares = getSharesIdea(iid);
        ArrayList<Share> sharesToBuy = new ArrayList<Share>();
        ArrayList<Integer> sharesToBuyNum = new ArrayList<Integer>();

        sortByPrice(shares);

        // We have sorted them by price per share, so the best options are first

        for (int i1 = 0; i1 < shares.size() && numShares > 0; i1++) {
            Share s = shares.get(i1);
            System.out.println("processing share: " + s);
            if ( s.getUid() == uid ) {
                System.out.println("Skipping, its mine");
                continue;
            }
            int availShares = s.getAvailableShares();
            if (availShares > 0) { //Should always happen
                System.out.println("ALready, available shares!: " + availShares);

                int toBuy = Math.min(availShares, numShares);

                if (s.getPriceForNum(toBuy) > userMoney) {
                    System.out.println("Not enough money...:" + userMoney + ", " + s.getPriceForNum(toBuy));
                    // Not enough money...
                    int pricePerShare = s.getPrice();

                    // See how many we can buy. Round down!
                    toBuy = (int) (((double) userMoney) / pricePerShare);
                    if (toBuy == 0)
                        break;
                    System.out.println("Will still try to buy " + toBuy);
                }
                System.out.println("Buying " + toBuy +" shares.");
                sharesToBuy.add(s);
                sharesToBuyNum.add(toBuy);
                numShares -= toBuy;
                userMoney -= s.getPriceForNum(toBuy);
            }
        }

        if ( numShares > 0 ) {
            //Can't buy shares!!!
            System.out.println("Failed to buy shares!!");
            return false;
        }

        //Okay, move on and let's buy them. this must be transactional
        Connection conn = getTransactionalConnection();
        for (int i = 0; i < sharesToBuy.size(); i++) {
            Share s = sharesToBuy.get(i);
            int num = sharesToBuyNum.get(i);
            int resultingShares = s.getNum()-num;
            System.out.println("Buying "+num+"from "+s.getUid()+"!!");
            System.out.println("^That menas that s.getPriceForNum(num) = "+s.getPriceForNum(num));
            setSharesIdea(s.getUid(),s.getIid(),resultingShares,s.getPrice(),s.getNumMin(),conn);
            insertIntoHistory(uid, s.getUid(), num,s.getPrice(),conn,iid);
            setUserMoney(s.getUid(), getUserMoney(uid) + s.getPriceForNum(num), conn);
        }


        setSharesIdea(uid,iid,sharesAlvo,targetPrice,minTargetShares,conn);
        setUserMoney(uid,userMoney, conn);

        // UNLEASH THE BEAST!
        returnTransactionalConnection(conn);

        for (int i = 0; i < sharesToBuy.size(); i++) {
            Share s = sharesToBuy.get(i);
            System.out.println("To buy: "+s);
            System.out.println("(Buying): "+sharesToBuyNum.get(i));
            new NotificationQueue(this, s.getUid()).enqueue(new Notification(uid, s.getUid(), sharesToBuyNum.get(i),
                    s.getPrice(), getUsername(uid), getUsername(s.getUid()), iid));
            new NotificationQueue(this, uid).enqueue(new Notification(uid, s.getUid(), sharesToBuyNum.get(i),
                    s.getPrice(), getUsername(uid), getUsername(s.getUid()), iid));
        }
        return true;
    }

    ////
    //  Set up the number of shares for a given idea, and the price of each share for that idea
    ////
    synchronized public void setSharesIdea(int uid, int iid, int nshares, int price,
                                            int numMinShares)throws RemoteException{
        /* null here means no transactional connection */
        setSharesIdea(uid, iid, nshares,price,numMinShares,null);
    }

    /**
     * Set up the number of shares for a given idea, and the price of each share for that idea
     * @param uid   The id of the user who requested the operation
     * @param iid   The id of the idea whose number of shares we are going to edit
     * @param nshares The number of shares we are going to assign to the given idea
     * @param price   The price of each share of the idea
     * @param numMinShares The minimum number of shares the user doesnt want to sell
     * @param conn Connection to the RMI Server
     * @return A boolean value, indicating if the operation went well, or not
     * @throws RemoteException
     */
    private synchronized void setSharesIdea(int uid, int iid,int nshares, int price, int numMinShares,
                                  Connection conn)throws RemoteException{
        String query = "select * from Shares where userid="+uid+" and "+"iid="+iid;
        ArrayList<String[]> result = ((conn == null) ? receiveData(query) : receiveData(query, conn));

        if ( !result.isEmpty() ) {
            // This already exists, we should just update it

            if ( nshares == 0 ) {
                // Set to 0!! We should delete it!
                query = "delete from shares where userid="+uid+" and iid="+iid;
            } else {
                query = "update shares set numshares="+nshares+" where iid="+iid+" and userid="+uid;
            }
        } else
            query = "INSERT INTO Shares VALUES (" + iid + "," + uid + "," + nshares + "," + price + "," + numMinShares + ")";

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
    synchronized private void insertIntoHistory(int uidBuyer, int uidSeller, int nshares, int price, Connection conn,
                                                int iid) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        Date transactionDate = new Date();
        String query = "insert into Transacoes values(" + (num_transactions+1) + "," + uidBuyer + "," + uidSeller + "," + price
                + "," + nshares + "," + iid,  sDate = format1.format(transactionDate);

        query = query + ", to_date('" +  sDate + "','yyyy:mm:dd:hh24:mi:ss'))";
        if ( conn == null )
            insertData(query);
        else
            insertData(query,conn);
        num_transactions++;
    }

    /**
     * Method responsible for updating the time when the user was logged in
     *
     * @param uid The id of the user
     * @return A boolean value, indicating if the operation went well, or not
     * @throws RemoteException
     */
    public void updateUserTime(int uid) throws RemoteException{
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        Date transactionDate = new Date();
        String sDate= format1.format(transactionDate), queryData;
        ArrayList<String[]> queryDataResult;

        queryData = "Update Utilizadores set dataUltimoLogin = to_date('" + sDate +
                "','yyyy:mm:dd:hh24:mi:ss') where userid = " + uid;
        insertData(queryData);
    }

    ////
    //  Method responsible for creating the connection between an idea and one or more topics
    ////
    synchronized public boolean setTopicsIdea(int iid, String topicTitle, int uid) throws RemoteException{

        String query ;
        ArrayList<String[]> topics;
        int topic_id;

        //ideas = receiveData(query);
        query = "Select t.tid from Topicos t where t.nome='" + topicTitle + "'";
        topics = receiveData(query);

        ////
        //  There is no topic with the given title, so let's create it
        ////
        if (topics.isEmpty()){
            createTopic(topicTitle,("Topic created by user " + uid),uid);

            //Add the number of the topic to the ArrayList
            query = "Select t.tid from Topicos t where t.nome='" + topicTitle + "'";
            topics = receiveData(query);
        }

        topic_id = Integer.valueOf(topics.get(0)[0]);//Get topic id

        query = "INSERT INTO TopicosIdeias VALUES (" + topic_id + "," + iid + ")";

        insertData(query);
        return true; //Change this to return....nothing?
    }

    ////
    //  Method responible for checking if there ins't already a relationship between two ideas of a different
    //  type of the relation we want to create
    ////
    synchronized private boolean checkOtherRelations(int iidpai, int iidfilho, int tipo) throws RemoteException{
        String query, query2;

        if (tipo == 1){
            query = "Select * from RelacaoIdeias where iidpai = " + iidpai + " and iidfilho = " + iidfilho +
                    " and tipo_relacao = " + 0;

            query2 = "Select * from RelacaoIdeias where iidpai = " + iidpai + " and iidfilho = " + iidfilho +
                    " and tipo_relacao = " + -1;
        }else if (tipo == -1){
            query = "Select * from RelacaoIdeias where iidpai = " + iidpai + " and iidfilho = " + iidfilho +
                    " and tipo_relacao = " + 0;

            query2 = "Select * from RelacaoIdeias where iidpai = " + iidpai + " and iidfilho = " + iidfilho +
                    " and tipo_relacao = " + 1;
        }else{ //tipo == 0
            query = "Select * from RelacaoIdeias where iidpai = " + iidpai + " and iidfilho = " + iidfilho +
                    " and tipo_relacao = " + 1;

            query2 = "Select * from RelacaoIdeias where iidpai = " + iidpai + " and iidfilho = " + iidfilho +
                    " and tipo_relacao = " + -1;
        }

        //Return if there is a relationship of other type between the two ideas
        return receiveData(query).size() == 0 && receiveData(query2).size() == 0;

    }

    ////
    //  Method responsible for creating the different relationships between ideas
    ////
    synchronized public boolean setIdeasRelations(int iidpai,int iidfilho, int tipo) throws RemoteException{
        String query;

        //  Tipo = 1 -> For
        //  Tipo = -1 -> Against
        //  Tipo = 0 -> Neutral

        //Check if the id of the "children" idea is valid
        query = "Select * from Ideias i where i.iid = " + iidfilho;
        if (receiveData(query).size() == 0)
            return false;

        //Check if there isn't already a relationship in the database between these two ideas, but of a different type
        if (!checkOtherRelations(iidpai,iidfilho,tipo))
            return false;

        //Check if the relationship we want to add is already in the database
        query = "Select * from RelacaoIdeias where iidpai = " + iidpai + " and iidfilho = " + iidfilho +
                " and tipo_relacao = " + tipo;
        if (receiveData(query).size() > 0)
            return true;


        //Check if there isn't already the same relation in the database. If there is we just don't insert that relation
        query = "Select * from RelacaoIdeias where iidpai = " + iidpai + " and iidfilho = " + iidfilho +
                " and tipo_relacao = " + tipo;
        if (receiveData(query).size() > 0)
            return true;

        query = "INSERT INTO RelacaoIdeias Values(" + iidpai + ", " + iidfilho + ", " + tipo + ")";

        insertData(query);
        return true;
    }


    /**
     * This just picks any connection available from the pool and uses it. If you need transactional support you can
     * specify a connection in another overloaded method
     * @param query
     * @return
     */
    private ArrayList<String[]> receiveData(String query) {

        Connection conn = null;

        boolean cont;
        do {
            cont = false;
            try {
                conn = connectionPool.checkOutConnection();
            } catch (SQLException e) {
                System.err.println("Error checking out connection for receiveData");
                cont = true;
            }
        } while ( cont );
        ArrayList<String[]> ret = receiveData(query, conn);
        if ( conn != null )
            connectionPool.returnConnection(conn);
        return ret;
    }
    ////
    //  Method responsible for executing queries like "Select..."
    //
    // Returns: null on failure, Arraylist with all columns (as strings in an array), which may be empty if there query
    // produces an empty table.
    //
    ////
    private ArrayList<String[]> receiveData(String query, Connection conn) {
        int columnsNumber, pos = 0;
        ArrayList<String[]> result = new ArrayList<String[]>();
        Statement statement = null;

        System.out.println("\n-------------------------------\nRunning query: "+query);

        boolean cont;
        do {
            cont = false;
            try {
                statement = conn.createStatement();
            } catch (SQLException e) {
                System.err.println("Error creating SQL statement '" + query + "'!");
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
                System.err.println("Error executing SQL query '" + query + "'!");
                cont = true;
            }
        } while ( cont );

        return result;
    }

    private Connection getTransactionalConnection() {
        Connection connection;
        try {
            connection = connectionPool.checkOutConnection();
        } catch (SQLException e) {
            System.err.println("Error checkout out a new connection for transactions!");
            return null;
        }

        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            System.err.println("Error setting autocommit!");
        }

        return connection;
    }

    private void returnTransactionalConnection(Connection c) {
        try {
            c.commit();
            c.setAutoCommit(true);
        } catch (SQLException e) {
            System.err.println("Error setting autocommit to true!");
        }
        connectionPool.returnConnection(c);
    }

    /**
     * This is used for most queries because it grabs any available connection. One might want to use the specialized
     * version which can operate on a given connection (for things such as transactions...)
     * @param query
     * @return
     */
    private void insertData(String query) {
        Connection conn = null;
        try {
            conn = connectionPool.checkOutConnection();
        } catch (SQLException e) {
            System.err.println("Error checking out connection for insertData");
        }
        insertData(query, conn);
        if ( conn != null )
            connectionPool.returnConnection(conn);
    }

    ////
    //  This method will be responsible for executing a query like "Insert ...". With this method we can create new registries in the
    //  database's tables
    //
    ////
    private void insertData(String query, Connection conn) {
        System.out.println("\n-------------------------------\nRunning inseeeeeert query: "+query);
        boolean cont;

        do {
            cont = false;
            try{
                conn.createStatement().executeUpdate(query);
            }catch(SQLException s){
                System.err.println("SQLException in the insertData method");
                cont = true;

            }
        } while ( cont );
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
                System.out.println("Failed to make connection!");
                return ;
            }

            //Get current number of users
            ArrayList<String[]> teste= receiveData("Select count(*) from Utilizadores");
            num_users = Integer.parseInt(teste.get(0)[0]);

            teste = receiveData("Select count(*) from Topicos");
            num_topics = Integer.parseInt(teste.get(0)[0]);

            teste = receiveData("Select count(*) from Ideias");
            num_ideas = Integer.parseInt(teste.get(0)[0]);

            teste = receiveData("Select count(*) from Transacoes");
            num_transactions = Integer.parseInt(teste.get(0)[0]);

            System.out.println("You made it, take control your database now!");

            //Start RMIRegistry programmatically
            Registry r = LocateRegistry.createRegistry(7000);
            r.rebind("academica", this);

            System.out.println("Server ready :)");

        } catch(RemoteException e){
            e.printStackTrace();
        } catch (SQLException s){
            s.printStackTrace();
        } catch(ClassNotFoundException c){
            c.printStackTrace();
        }
    }

    public void writeRequestQueueFile(ArrayList<Request> queue) throws RemoteException {
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(new FileOutputStream(requestsQueueFilePath));
        } catch (IOException e) {
            System.err.println("Error opening Queue file for writing!");
            return;
        }

        try {
            out.writeInt(queue.size());
            for (Request r : queue)
                r.writeToStream(out);
        } catch (IOException e) {
            System.err.println("Error writing Queue to file!!");
        }

        try { out.close(); } catch (IOException ignored) {}
    }

    public ArrayList<Request> readRequestsFromQueueFile() throws RemoteException {
        ObjectInputStream in;
        try {
            in = new ObjectInputStream(new FileInputStream(requestsQueueFilePath));
        } catch (IOException e) {
            System.err.println("Error opening Queue file for reading!");
            return null;
        }

        ArrayList<Request> requests = new ArrayList<Request>();
        int size;
        try {
            size = in.readInt();
        } catch (IOException e) {
            System.err.println("Error reading size from Queue File!");
            return null;
        }
        for (int i = 0; i < size; i++)
            requests.add(new Request(in));

        return requests;
    }

    public ArrayList<Notification> readNotificationsFromQueueFile(int uid) throws RemoteException {
        synchronized ( notificationsLock ) {
            ObjectInputStream in;
            try {
                String path = "./"+uid+"notifications.bin";
                in = new ObjectInputStream(new FileInputStream(path));
            } catch (IOException e) {
                //System.err.println("Error opening Queue file for reading!");
                return null;
            }

            ArrayList<Notification> notifications = new ArrayList<Notification>();
            int size;
            try {
                size = in.readInt();
            } catch (IOException e) {
                System.err.println("Error reading size from Notification Queue File!");
                return null;
            }
            for (int i = 0; i < size; i++)
                try {
                    notifications.add((Notification)in.readObject());
                } catch (IOException e) {
                    System.err.println("Error reading from Notification Queue File!");
                    return null;
                } catch (ClassNotFoundException e) {
                    System.err.println("Error reading from Notification Queue File! (Class not found)");
                    return null;
                }


            if ( notifications.size()>0 ) {
                System.out.println("Just found notifications!");
                for (Notification n : notifications)
                    System.out.println("SUch as: "+n);
            }

            return notifications;
        }
    }

    public boolean writeNotificationsQueueFile(ArrayList<Notification> notifications, int uid) throws
            RemoteException {
        synchronized ( notificationsLock ) {
            String path = "./"+uid+"notifications.bin";
            ObjectOutputStream out;
            try {
                out = new ObjectOutputStream(new FileOutputStream(path));
            } catch (IOException e) {
                System.err.println("Error opening Notification Queue file for writing!");
                return false;
            }

            try {
                out.writeInt(notifications.size());
                for (Notification r : notifications)
                    r.writeToStream(out);
            } catch (IOException e) {
                System.err.println("Error writing Notification Queue to file!!");
                return false;
            }

            try { out.close(); } catch (IOException ignored) { }

            return true;
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
            System.out.println("RemoteException on the main method of the RMI Server");
        }
    }
}
