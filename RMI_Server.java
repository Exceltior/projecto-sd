import java.io.*;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.ParseException;
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
    private int starting_money = 10000;
    private int limit_time_active = 300;//5 minutes
    private ConnectionPool connectionPool;
    private int lastFile = 0; //FIXME: Update this dynamically

    /**
     * Hashes the password using MD5 and returns it.
     * @param pass The plaintext password to be hashed.
     * @return The hashed password.
     */
    //TODO: maybe change this
    private String hashPassword(String pass)
    {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Cannot find hashing algorithm:\n" + e);
            System.exit(-1); //FIXME?
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

    ////
    //  Class constructor. Creates a new instance of the class RMI_Server
    ////
    public RMI_Server(String servidor, String porto, String sid) throws RemoteException {
        super();
        this.url = "jdbc:oracle:thin:@" + servidor + ":" + porto + ":" + sid;
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

        if ( result.size() > 0 )//Return the user's id
            return Integer.valueOf(result.get(0)[0]);
        else
            return -1;
    }

    ////
    //  Method responsible for getting all the topics stored in the database.
    ////
    public ServerTopic[] getTopics() throws RemoteException{
        String query = "select * from Topicos";

        ArrayList<String[]> result = null;

        try{
            result = receiveData(query);
        } catch(RemoteException e){
            System.err.println("DEU MERDA");
        }


        if ( result == null )
            return null; //FIXME: We should do something about a query failing or something like that...

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
        ArrayList<String[]> queryResult = null;

        try{
            queryResult = receiveData(query);
        }catch(RemoteException r){
            System.err.println("Error accessing the database");
        }

        if (queryResult == null)
            return null; //FIXME: We should do something about a query failing or something like that...

        if (queryResult.size() == 0)
            return null;

        ideas = new Idea[queryResult.size()];

        for (int i = 0; i < queryResult.size(); i++)
            ideas[i] = new Idea(queryResult.get(i));


        return ideas;
    }

    ////
    // This returns an array of ideas which belong to this Topic.
    //
    // FIXME: We should decide if we pass the topic ID in here or the topic itself. It might be better to pass ideas
    public Idea[] getIdeasFromTopic(int tid) throws RemoteException{
        String query = "select e.iid, e.titulo, e.descricao, e.userid, e.activa from Ideias e, " +
                "TopicosIdeias t where t.iid = e.iid and t" +
                ".tid = "+tid+" and e.activa = 1";

        ArrayList<String[]> result = null;

        try{
            result = receiveData(query);
        } catch(RemoteException e){
            System.err.println("DEU MERDA");
        }

        if ( result == null )
            return null; //FIXME: We should do something about a query failing or something like that...

        if ( result.size() == 0 )
            return null;

        Idea[] ideas = new Idea[result.size()];

        for (int i = 0; i < result.size(); i++)
            ideas[i] = new Idea(result.get(i));

        return ideas;
    }

    ////
    // Method responsible for checking if there aren't any topics already created with the same name as the one we want
    //  to create
    ////
    public boolean validateTopic(String nome){
        String query = "Select * from Topicos t where t.nome = '" + nome + "'";
        ArrayList<String[]> topics = null;

        try{
            topics = receiveData(query);
        }catch(RemoteException r){
            System.err.println("Remote Exception on the validateData method");
            //FIXME: Deal with this
        }

        // NOTE: topics will only be null if the query failed. And we should assume that never happens...
        return topics == null || topics.size() == 0;
    }

    ////
    //  Method responsible for validating a user's username, before adding it to the database
    ///
    public boolean validateData(String username){
        String query = "Select * from Utilizadores u where u.username = '" + username + "'";
        ArrayList<String[]> users = null;

        try{
            users = receiveData(query);
        }catch(RemoteException r){
            System.err.println("Remote Exception on the validateData method");
            //FIXME: Deal with this
        }

        // NOTE: users will only be null if the query failed. And we should assume that never happens...
        return users == null || users.size() == 0;

    }

    ////
    //  Method responsile for insering a new user in the database
    ////
    synchronized public boolean register(String user, String pass, String email, String date) throws RemoteException{
        boolean check;

        if (! validateData(user)){
            System.err.println("O validate data devolveu false");
            return false;
        }
        pass = hashPassword(pass);

        String query = "INSERT INTO Utilizadores VALUES (" + (num_users+1) + ",'" + email + "','" + user + "'," +
                "'" + pass +
                "'," + starting_money + ",to_date('" + date + "','yyyy.mm.dd'), null)";

        check = insertData(query);

        if ( check )
            num_users++;

        return check;
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
        String path="./"+lastFile;

        try {
            file.writeTo(path);
        } catch (FileNotFoundException e) {
            System.err.println("Should never happen!");
            return false;
        }

        String query = "insert into IdeiasFicheiros values ("+iid+path+")";

        insertData(query); //IGNORE if it fails... FIXME: if it fails we should retry...it may be a DB transient                                                                                                    // failure

        lastFile++;
        return true;
    }

    public NetworkingFile getFile(int iid) throws RemoteException {
        String query = "select path fromIdeiasFicheiros where iid ="+iid;
        ArrayList<String[]> queryResult = receiveData(query);
        if ( queryResult == null || queryResult.size() == 0) //FIXME: Deal with this!
        return null;

        try {
            return new NetworkingFile(queryResult.get(0)[0]);
        } catch (FileNotFoundException e) {
            System.err.println("Shouldn't happen! DB corrupted?");
            return null;
        }
    }

    ////
    // Method responsible for getting all the ideas in favou, neutral or against a given idea
    ////
    public Idea[] getIdeRelations(int iid, int relationshipType) throws RemoteException{
        String query = "Select * from Ideais i, RelacaoIdeias r where r.iidpai = i.iid and r.iid = " + iid +
                " and r.tipo_relacao = " + relationshipType;
        ArrayList<String[]> queryResult = receiveData(query);
        Idea[] devolve = null;

        if (queryResult == null || queryResult.size()==0)
            return null;//FIXME Deal with this!

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

        if(insertData(query)) {
            ++num_topics; return true;
        }
        else
            return false;
    }

    ////
    //  Method responsible for creating a new idea in the database
    ////
    synchronized public int createIdea(String title, String description, int uid) throws RemoteException{
         String query;


        query = "INSERT INTO Ideias VALUES (" + (num_ideas+1) + ",'" + title + "','" + description + "'," +
                "" + uid + "," +
                "" + "1)";

        if(insertData(query))
            return ++num_ideas;
        else
            return -1;
    }

    private boolean ideaHasFiles(int iid) {
        String query = "select * from IdeiasFicheiros i where i.iid = " + iid;
        ArrayList<String[]> queryResult = null;
        try {
            queryResult = receiveData(query);
        } catch (RemoteException e) {
            System.err.println("should never happen!!!!!");
            return false;
        }

        if (queryResult == null)
            return false; //FIXME: We should handle the query getting all fucked up (NULL case)

        return !queryResult.isEmpty();
    }

    public boolean ideaHasChildren(int iid) throws RemoteException {
        String query = "select * from RelacaoIdeias t where t.iidpai = " + iid;
        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult == null)
            return false; //FIXME: We should handle the query getting all fucked up (NULL case)

        return !queryResult.isEmpty();
    }

    public boolean removeIdea(Idea idea) throws  RemoteException {
        if ( ideaHasChildren(idea.id) ) {
            return false;
        }

        String query = "update Ideias set activa = 0 where iid="+idea.id;
        boolean queryResult = insertData(query);

        if (!queryResult)
            return false; //FIXME: We should handle the query getting all fucked up (false case)

        return true;

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

        //FIXME: Maybe we should handle the case where the query fails in a different way
        if (queryResult == null || queryResult.isEmpty())
            return null;

        return new Idea(queryResult.get(0));
    }

    public Idea[] getIdeaByIID(int iid, String title) throws RemoteException{
        String query;
        Idea[] devolve;

        if (iid != -1 && !title.equals(""))
            query = "Select * from Ideias i where i.activa = 1 and i.iid = '" + iid +"' and i.titulo = " + title;
        else if(iid != -1)
            query = "Select * from Ideias i where i.activa = 1 and i.iid = " + iid;
        else if (!title.equals(""))
            query = "Select * from Ideias i where i.activa = 1 and i.titulo = '" + title;
        else
            return null;

        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult.size() == 0)
            return null;

        devolve = new Idea[queryResult.size()];
        for (int i=0;i<queryResult.size();i++)
            devolve[i] = new Idea(queryResult.get(i));

        return devolve;
    }

    ////
    // Build an idea from a title. Notice that this constructor does not give us parent topic and ideas, it only gathers
    // the title (which we already had), title and body. If one wants parent topics, ideas or children ideas, one must call
    // addChildrenIdeasToIdea(), addParentIdeasToIdea() and addParentTopicsToIdea()
    ////
    public Idea getIdeaByTitle(String title) throws RemoteException{
        String query = "select * from Ideias t where t.titulo = " + title + " and t.activa = 1";
        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult == null)
            return null;

        return new Idea(queryResult.get(0));
    }

    public ServerTopic getTopic(int tid, String name) throws RemoteException{
        String query;

        if (tid != -1 && !name.equals(""))
            query = "Select * from Topicos t where t.nome = '" + name +"' and t.tid = " + tid;
        else if(tid != -1)
            query = "Select * from Topicos t where t.tid = " + tid;
        else if (!name.equals(""))
            query = "Select * from Topicos t where t.nome = '" + name;
        else
            return null;

        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult.size() == 0)
            return null;

        ServerTopic topics = new ServerTopic(queryResult.get(0));

        return topics;
    }

    ////
    //  Send to the Server the history of transactions for a given client
    ////
    public String[] getHistory(int uid) throws RemoteException{
        String[] history = null;
        String query = "Select t.comprador, t.vendedor, t.valor, i.titulo from Transacoes t, Ideias i " +
                "where (t.comprador = " + uid + " or t.vendedor = " + uid + ") and t.iid = i.iid";

        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult == null)
            return null;

        history = new String[queryResult.size()];

        for (int i=0;i<queryResult.size();i++)
            history[i] = "Buyer id: " + queryResult.get(i)[0] + " Seller id: " +  queryResult.get(i)[1] + "Transaction Money: "
                    + queryResult.get(i)[2] + "Idea: " + queryResult.get(i)[3];

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
        String query = "select * from Shares where iid="+iid;

        ArrayList<String[]> result = receiveData(query);

        if ( result == null ) return null; //FIXME: Wh're not dealing with this!

        for ( String[] row : result)
            shares.add(new Share(row));

        return shares;
    }

    /**
     * Get the shares of this user for this idea
     * @param iid
     * @param uid
     * @return A Share object with the info of the shares, or null in case there are no shares
     * @throws RemoteException
     */
    public Share getSharesIdeaForUid(int iid, int uid) throws RemoteException {
        ArrayList<Share> shares = new ArrayList<Share>();
        String query = "select * from Shares where iid="+iid+" and uid="+uid;

        ArrayList<String[]> result = receiveData(query);

        if ( result == null ) return null; //FIXME: We're not dealing with this! ANd have to!

        if ( result.size() == 0 ) {
            return null;
        }

        return new Share(result.get(0));
    }

    private void sortByPriceRatio(ArrayList<Share> shares) {
        Collections.sort(shares);
    }

    /**
     * Gets the money for the given UID
     * @param uid
     * @return
     * @throws RemoteException
     */
    public int getUserMoney(int uid) throws RemoteException {
        String query = "select dinheiro from Utilizadores where uid="+uid;

        ArrayList<String[]> result = receiveData(query);

        if ( result == null || result.size() == 0) /* FIXME: Should NEVER NEVER happen! */
            return 0;

        return Integer.valueOf(result.get(0)[0]);
    }

    /**
     * Sets the money for UID. UID must exist
     * @param uid
     * @param money
     * @param conn The connection to use, for instance, for transactional operations. null if don't care which
     *             connection to use
     * @return
     * @throws RemoteException
     */
    public boolean setUserMoney(int uid, int money, Connection conn) throws RemoteException {
        String query = "update Utilizadores set dinheiro="+money+" where uid="+uid;

        return ((conn == null) ? insertData(query) : insertData(query, conn));
    }

    /**
     * This tries to GET numShare shares. Notice that it doesn't try to BUY. If the user already has them,
     * it just quits and errors out.
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
    synchronized public int tryGetSharesIdea(int uid, int iid, int numShares, int targetPrice,
                                              int minTargetShares) throws
            RemoteException {

        Share currentShares = getSharesIdeaForUid(iid, uid);
        int userMoney = getUserMoney(uid);

        if ( currentShares != null) {
            // User already has shares
            if ( targetPrice == -2 )
                targetPrice = currentShares.getPrice();
            if (minTargetShares == -2)
                minTargetShares = currentShares.getNumMin();

            numShares -= currentShares.getNum();

            if ( numShares <= 0) {
                //FIXME: Should we update the shares' targetPrice and min target shares here?!?!?! JOCA!

                ////
                //  Super Answer by Joca:
                //  Dude, if he already has them why do we need to update? Isn't that already in the database?
                //  Just tell them "Dude, I can't buy something you already have..."
                ////
                System.out.println("User tried to get X shares, but already has them!"); //FIXME?
                return 1; //Got them!
            }
        }

        ArrayList<Share> shares = getSharesIdea(iid);
        ArrayList<Share> sharesToBuy = new ArrayList<Share>();
        ArrayList<Integer> sharesToBuyNum = new ArrayList<Integer>();

        sortByPriceRatio(shares);

        // We have sorted them by price per share, so the best options are first

        /**
         * This is a version of the Knapsack problem, and it seems a little bit too farfetched for us to be solving
         * it here when we're worrying about distributed systems.
         *
         * Our solution is not the optimal solution. It is a greedy approximation algorithm which we later found out
         * to be George Dantzig's greedy algorithm. It does the job.
         */

        for (Share s : shares) {
            int availShares = s.getAvailableShares();
            if ( availShares > 0 ) {
                if ( availShares >= numShares ) {
                    // There are enough for us to finish!
                    int toBuy = availShares-numShares;

                    if ( s.getPriceForNum(toBuy) > userMoney ) {
                        // Not enough money...
                        double pricePerShare = s.getPricePerShare();

                        // See how many we can buy. Round down!

                        toBuy = (int)(((double)userMoney) / pricePerShare);
                    }
                    sharesToBuy.add(s);
                    sharesToBuyNum.add(toBuy);
                    numShares = 0; //No shares remaining to buy
                    userMoney -= s.getPriceForNum(toBuy);
                    break;
                } else {
                    sharesToBuy.add(s);
                    sharesToBuyNum.add(availShares);
                    numShares -= availShares; //Still some left to buy...
                }
            }
        }

        if ( numShares > 0 ) {
            //Can't buy shares!!!
            return 0;
        }

        //Okay, move on and let's buy them. this must be transactional
        Connection conn = getTransactionalConnection();
        for (int i = 0; i < sharesToBuy.size(); i++) {
            Share s = sharesToBuy.get(i);
            int num = sharesToBuyNum.get(i);
            int resultingShares = s.getNum()-num;

            setSharesIdea(s.getUid(),s.getIid(),resultingShares,s.getPrice(),s.getNumMin(),conn);
            insertIntoHistory(uid, iid, num,s.getPrice(),conn,iid);
            setUserMoney(s.getUid(), getUserMoney(uid) + s.getPriceForNum(num), conn);
        }


        setSharesIdea(uid,iid,numShares,targetPrice,minTargetShares,conn);
        setUserMoney(uid,userMoney, conn);

        // UNLEASH THE BEAST!
        returnTransactionalConnection(conn);
        return 1;
    }

    ////
    //  Set up the number of shares for a given idea, and the price of each share for that idea
    ////
    synchronized public boolean setSharesIdea(int uid, int iid,int nshares, int price,
                                       int numMinShares)throws RemoteException{
        /* null here means no transactional connection */
        return setSharesIdea(uid, iid, nshares,price,numMinShares,null);
    }

    ////
    //  Set up the number of shares for a given idea, and the price of each share for that idea
    ////
    private synchronized boolean setSharesIdea(int uid, int iid,int nshares, int price, int numMinShares,
                                  Connection conn)throws RemoteException{
        String query = "select * from Shares where uid="+uid+" and "+"iid="+iid;
        ArrayList<String[]> result = ((conn == null) ? receiveData(query) : receiveData(query, conn));

        if (result.size() > 0) {
            // This already exists, we should just update it

            if ( nshares == 0 ) {
                // Set to 0!! We should delete it! FIXME: is this right?
                query = "delete from shares where uid="+uid+" and iid="+iid;
            } else {
                query = "update shares set nshares="+nshares+" where iid="+iid+" and uid="+uid;
            }
        } else
            query = "INSERT INTO Shares VALUES (" + iid + "," + uid + "," + nshares + "," + price + "," + numMinShares + ")";

        return conn ==null ? insertData(query) : insertData(query, conn);
    }

    synchronized private boolean insertIntoHistory(int uidBuyer, int uidSeller, int nshares,int price, Connection conn,
                                         int iid) {
        //FIXME FIXME FIXME: Ver se a ordem dos values é esta (vê bem o nshares e o price!!); adicionar o campo da
        // data para o instante actual,
        // porque
        // isso está em FALTA!! JOCA JOCA JOCA

        ////
        //  FIXME FIXME FIXME MAXI Falta aqui o id da ideia que foi comprada!
        //  A partida o resto estará bem!
        ////

        //  First we are going to extract the system date, and them we are going to add it to the query. It appears to have
        //  to be like this, ORACLE SQL is a very good one, and does not allow us to select sysdate inside a query...
        String queryData = "Select to_char(sysdate, 'yyyy.mm.dd hh.mm.ss') from dual";
        String query = "insert into Transaccoes values(" + uidBuyer + "," + uidSeller + "," + price  + "," + nshares + "," + iid ;
        ArrayList<String[]> queryDataResult;
        boolean res = false;
        try {
            queryDataResult = receiveData(queryData, conn);
            query = query + ", to_date(" + queryDataResult.get(0)[0] + "'yyyy.mm.dd hh.mm.ss')";
            res = insertData(query, conn);
        } catch (RemoteException e) {
            System.err.println("Remote exception, wtf!"); //FIXME
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return res;
    }

    ////
    //  Mehtod responsible for updating the time when the user was logged in
    ////
    public boolean updateUserTime(int uid) throws RemoteException{
        String queryData = "Select to_char(sysdate, 'yyyy:mm:dd:hh:mm:ss') from dual";
        ArrayList<String[]> queryDataResult;
        boolean result = false;

        try{
            queryDataResult = receiveData(queryData);
            queryData = "Update Utilizadores set dataUltimoLogin = to_date(" + queryDataResult.get(0)[0] + "" +
                    "'yyyy:mm:dd:hh:mm:ss') where userid = " + uid;
            result = insertData(queryData);
        }catch(RemoteException r){
            System.err.println("RemoteException!");
            r.printStackTrace();//FIXME: Deal with this!
        }
        return result;
    }

    ////
    //  Method that returns true if the user has been actived in the last 5 minutes
    ////
    public boolean isUserIn(int uid) throws RemoteException{
        String query = "Select u.dataUltimoLogin from Utilizadores u where u.userid = " + uid;
        ArrayList<String[]> resultQuery = null;
        Date actualDate = new Date(),userDate;
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy:mm:dd:hh:mm:ss");
        String[] user_date;
        String finalUserDate = "";

        try{
            resultQuery = receiveData(query);
        }catch (RemoteException r){
            System.err.println("RemoteException!");
            r.printStackTrace();//FIXME: Deal with this!
        }

        if (resultQuery == null)
            return false;

        user_date = resultQuery.get(0)[0].split(":");

        for (int i=0;i<user_date.length;i++){
             finalUserDate = finalUserDate + user_date[i];
            if (i<user_date.length - 1)
                finalUserDate = finalUserDate + ":";
        }

        try{
            userDate = format1.parse(finalUserDate);
        }catch(ParseException p){
            System.err.println("Error while parsing the date");
            p.printStackTrace();
            return false;
        }

        long difference = actualDate.getTime() - userDate.getTime();
        long diffMinutes = difference / (60 * 1000) % 60;

        if (diffMinutes > limit_time_active)//Has been active in less than 5 minutes
            return true;
        return false;
    }

    ////
    //  Method responsible for creating the connection between an idea and one or more topics
    ////
    synchronized public boolean setTopicsIdea(int iid, String topicTitle, int uid) throws RemoteException{

        //String query = "Select i.iid from Ideias i where i.iid = '" + iid + "' and i.activa = 1";
        String query = null;
        ArrayList<String[]> topics = null;
        //ArrayList<String[]> ideias;
        int idea_id, topic_id;
        boolean check;

        try{
            //ideas = receiveData(query);
            query = "Select t.tid from Topicos t where t.nome='" + topicTitle + "'";
            topics = receiveData(query);

            ////
            //  There is no topic with the given title, so let's create it
            ////
            if (topics.size() == 0){

                ////
                //  FIXME: Decide which description to put in the topic when creating it
                ////

                check = createTopic(topicTitle,("Topic created by user " + uid),uid);
                if(!check){
                    System.err.println("Error creating topic " + topicTitle + " in the setTopicsIdea method!");
                    //FIXME: What to do in this situation???
                } else{
                    //Add the number of the topic to the ArrayList
                    query = "Select t.tid from Topicos t where t.nome='" + topicTitle + "'";
                    topics = receiveData(query);
                }
            }

            if ( (topics.size() > 0) ){
                topic_id = Integer.valueOf(topics.get(0)[0]);//Get topic id

                query = "INSERT INTO TopicosIdeias VALUES (" + topic_id + "," + iid + ")";

                return insertData(query);
            } //else FIXME: Shouldn't happen because there should always be a topic
        }catch(RemoteException r){
            System.err.println("Remote Exception on the setTopicsIdea method");
            //FIXME: Deal with this
        }

        return false;
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

        query = "INSERT INTO RelacaoIdeias Values(" + iidpai + ", " + iidfilho + ", " + tipo + ")";

        return insertData(query);
    }


    /**
     * This just picks any connection available from the pool and uses it. If you need transactional support you can
     * specify a connection in another overloaded method
     * @param query
     * @return
     * @throws RemoteException
     */
    private ArrayList<String[]> receiveData(String query) throws RemoteException{

        Connection conn = null;
        try {
            conn = connectionPool.checkOutConnection();
        } catch (SQLException e) {
            System.err.println("Error checking out connection for receiveData");
        }
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
    private ArrayList<String[]> receiveData(String query, Connection conn) throws RemoteException{
        int columnsNumber, pos = 0;
        ArrayList<String[]> result = new ArrayList<String[]>();
        Statement statement;

        System.out.println("\n-------------------------------\nRunning query: "+query);

        try {
            statement = conn.createStatement();
        } catch (SQLException e) {
            System.err.println("Error creating SQL statement '" + query + "'!");
            return null;
        }

        ResultSet rs;
        ResultSetMetaData rsmd;
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
            return null;
        }

        return result;
    }

    private Connection getTransactionalConnection() {
        //FIXME: Implement this well
        Connection connection = null;
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
     * @throws RemoteException
     */
    private boolean insertData(String query) throws RemoteException{
        Connection conn = null;
        try {
            conn = connectionPool.checkOutConnection();
        } catch (SQLException e) {
            System.err.println("Error checking out connection for insertData");
        }
        boolean ret = insertData(query, conn);
        if ( conn != null )
            connectionPool.returnConnection(conn);
        return ret;
    }

    ////
    //  This method will be responsible for executing a query like "Insert ...". With this method we can create new registries in the
    //  database's tables
    //
    // FIXME: We are going to have to do more than just print a pretty message to stderr. In fact, we should never let
    // execution go through or we may have unpredicteable results
    ////
    private boolean insertData(String query, Connection conn) throws RemoteException{
        Statement statement;
        int update = -1;

        System.out.println("\n-------------------------------\nRunning inseeeeeert query: "+query);

        try{
            statement = conn.createStatement();
            update = statement.executeUpdate(query);
        }catch(SQLException s){
            System.err.println("SQLException in the insertData method");

            return false;

        }

        //System.out.println("O resultado foi " + (update!=0));
        return update != 0;
    }

    private void execute(){

        ////
        //  Store the database ip, username and password in a file or store it in "the properties" stuff the teacher said???
        ////

        String username = "sd", password = "sd";

        try{

            connectionPool = new ConnectionPool(url, username, password);


            //connect to database
            //FIXME: Should this still be here? Should it be moved inside ConnectionPool?
            Class.forName("oracle.jdbc.driver.OracleDriver");

            if (connectionPool == null) {
                System.out.println("Failed to make connection!");
                return ;
            }

            //Get current number of users
            try{
                ArrayList<String[]> teste= receiveData("Select count(*) from Utilizadores");
                num_users = Integer.parseInt(teste.get(0)[0]);

                teste = receiveData("Select count(*) from Topicos");
                num_topics = Integer.parseInt(teste.get(0)[0]);

                teste = receiveData("Select count(*) from Ideias");
                num_ideas = Integer.parseInt(teste.get(0)[0]);
            }catch(RemoteException r){
                System.err.println("Remote Exception while trying to get the number of users....");
                //FIXME: HOW TO DEAL WITH THIS EXCEPTION????
            }

            System.out.println("You made it, take control your database now!");

            //Start RMIRegistry programmatically
            Registry r = LocateRegistry.createRegistry(7000);
            r.rebind("academica", this);

            ////
            //  FIXME: Is it worth to store the RMI Registry's port in some sort of a file or variable?
            ////

            System.out.println("Server ready :)");

        } catch(RemoteException e){
            e.printStackTrace();
        } catch (SQLException s){
            s.printStackTrace();
        } catch(ClassNotFoundException c){
            c.printStackTrace();
        }

        ////
        //
        //
        //  FIXME: WHAT TO DO WITH THESE EXCEPTIONS???
        //
        //
        ////
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

        try {
            out.close();
        } catch (IOException e) {
            //FIXME: What damn exception can we get here?
        }
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
        int size = 0;
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

    public static void main(String[] args) {
        try{
            RMI_Server servidor = new RMI_Server("192.168.56.101","1521","XE");
            servidor.execute();
        }catch(RemoteException r){
            System.out.println("RemoteException on the main method of the RMI Server");
        }
    }
}
