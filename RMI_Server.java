import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;

////
//  This is the RMI Server class, which will be responsible for interacting with the database, allowing the TCP Servers to commit
//  queries and other commands to the database
////
public class RMI_Server extends UnicastRemoteObject implements RMI_Interface{

    private Connection conn;
    private String url;
    static int num_users;
    static int num_topics;
    static int num_ideas;
    static int starting_money = 10000;

    ////
    //  Class constructor. Creates a new instance of the class RMI_Server
    ////
    public RMI_Server(String servidor, String porto, String sid) throws RemoteException {
        super();
        this.url = "jdbc:oracle:thin:@" + servidor + ":" + porto + ":" + sid;
    }

    public Connection getConn(){
        return this.conn;
    }

    public String getUrl(){
        return this.url;
    }

    public void setConn(Connection c){
        this.conn = c;
    }

    public void closeConnection(){
        try {
            conn.close();
        } catch (SQLException e) {
            //e.printStackTrace();
            System.err.println("Error closing the connection");
        }

        ////
        //
        //
        //  FIXME: WHAT TO DO WITH THIS EXCEPTIONS???
        //
        //
        ////
    }

    ////
    //  Method responsible for checking a given username and corresponding password, in order to check if the user is registered
    //  and if that is the case, confirm his (or hers) login.
    ////
    public int login(String user, String pwd) throws RemoteException {

        String query;
        ArrayList<String[]> result;

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
    //  Method responsible for validating an idea, before adding it to the database
    ////
    public boolean validateIdea(String description){
        String query = "Select * from Ideias i where i.descricao='" + description + "'";
        ArrayList<String[]> ideias = null;

        try{
            ideias = receiveData(query);
        }catch(RemoteException r){
            System.err.println("Remote Exception on the validateIdea method");
            //FIXME: Deal with this
        }

        return ideias == null || ideias.size() == 0;
    }

    ////
    //  Method responsile for insering a new user in the database
    ////
    public boolean register(String user, String pass, String email, String date) throws RemoteException{
        boolean check;

        if (! validateData(user)){
            System.err.println("O validate data devolveu false");
            return false;
        }

        num_users++;
        String query = "INSERT INTO Utilizadores VALUES (" + num_users + ",'" + email + "','" + user + "','" + pass +
                "'," + starting_money + ",to_date('" + date + "','yyyy.mm.dd'))";

        check = insertData(query);

        return check;
    }

    ////
    //  Method responsible for creating a new topic in the database
    ////
    public boolean createTopic(String nome, String descricao, int uid) throws  RemoteException{
        if (! validateTopic(nome)){
            System.err.println("Topico invalido");
            return false;
        }
        num_topics++;

        String query = "INSERT INTO Topicos VALUES (" + num_topics + ",'" + nome + "','" + descricao + "'," + uid + ")";

        return insertData(query);
    }

    ////
    //  Method responsible for creating a new idea in the database
    ////
    public int createIdea(String title, String description, int uid) throws RemoteException{
         String query;
        if (!validateIdea(description)){
             System.out.println("Invalid Idea!");
             return -1;
         }

        num_ideas++;

        query = "INSERT INTO Ideias VALUES (" + num_ideas + ",'" + title + "','" + description + "'," + uid + "," + "1)";

        if(insertData(query))
            return num_ideas;
        else
            return -1;
    }

    ////
    // Add the parent topics of this idea to it
    //
    public boolean addParentTopicsToIdea(Idea idea) throws  RemoteException{
        String query = "select * from TopicosIdeias t where t.iid = " + idea.getId();
        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult == null)
            return false;

        idea.addParentTopicsFromSQL(queryResult);

        return true;
    }

    ////
    // Add the parent ideas of this idea to it
    //
    public boolean addParentIdeasToIdea(Idea idea) throws RemoteException {
        String query = "select * from RelacaoIdeias r where r.iidfilho = " + idea.getId();
        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult == null)
            return false;

        idea.addParentIdeasFromSQL(queryResult);

        return true;
    }

    ////
    // Add the children ideas of this idea to it
    //
    public boolean addChildrenIdeasToIdea(Idea idea) throws  RemoteException {
        String query = "select * from RelacaoIdeias r where r.iidpai = " + idea.getId();
        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult == null)
            return false;

        idea.addChildrenIdeasFromSQL(queryResult);

        return true;
    }

    ////
    // Build an idea from an IID. Notice that this constructor does not give us parent topic and ideas, it only gahters
    // IID (which we already had), title and body. If one wants parent topics, ideas or children ideas, one must call
    // addChildrenIdeasToIdea(), addParentIdeasToIdea() and addParentTopicsToIdea()
    //
    public Idea getIdeaByIID(int iid) throws RemoteException {
        String query = "select * from Ideias t where t.iid = " + iid + " and t.activa = 1";
        ArrayList<String[]> queryResult = receiveData(query);

        if (queryResult == null)
            return null;

        return new Idea(queryResult.get(0));
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

    ////
    //  Set up the number of shares for a given idea, and the price of each share for that idea
    ////
    public boolean setSharesIdea(int uid, int iid,int nshares, int price, int numMinShares)throws RemoteException{

        String query = "INSERT INTO Shares VALUES (" + iid + "," + uid + "," + nshares + "," + price + "," + numMinShares + ")";

        return insertData(query);
    }

    ////
    //  Method responsible for creating the connection between an idea and one or more topics
    ////
    public boolean setTopicsIdea(int iid, String topicTitle, int uid) throws RemoteException{

        String query = "Select i.iid from Ideias i where i.iid = '" + iid + "' and i.activa = 1";
        ArrayList<String[]> ideas = null, topics = null;
        int idea_id, topic_id;
        boolean check;

        try{
            ideas = receiveData(query);
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
                    String []temp = {"" + (num_topics-1)};
                    topics.add(temp);
                }
            }

            if ( (ideas.size() > 0) && (topics.size() > 0)){
                idea_id = Integer.valueOf(ideas.get(0)[0]);//Get idea id
                topic_id = Integer.valueOf(topics.get(0)[0]);//Get topic id

                query = "INSERT INTO TopicosIdeias VALUES (" + topic_id + "," + idea_id + ")";

                return insertData(query);
            }
        }catch(RemoteException r){
            System.err.println("Remote Exception on the setTopicsIdea method");
            //FIXME: Deal with this
        }

        return false;
    }

    ////
    //  Method responsible for creating the different relationships between ideas
    ////
    public boolean setIdeasRelations(int iidpai,int iidfilho, int tipo) throws RemoteException{
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

    ////
    //  Method responsible for executing queries like "Select..."
    //
    // Returns: null on failure, Arraylist with all columns (as strings in an array), which may be empty if there query
    // produces an empty table.
    //
    ////
    private ArrayList<String[]> receiveData(String query) throws RemoteException{
        int columnsNumber, pos = 0;
        ArrayList<String[]> result = new ArrayList<String[]>();
        Statement statement;

        //System.out.println("\n-------------------------------\nRunning query: "+query);

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

    ////
    //  This method will be responsible for executing a query like "Insert ...". With this method we can create new registries in the
    //  database's tables
    //
    // FIXME: We are going to have to do more than just print a pretty message to stderr. In fact, we should never let
    // execution go through or we may have unpredicteable results
    ////
    private boolean insertData(String query) throws RemoteException{
        Statement statement;
        int update = -1;

        try{
            statement = conn.createStatement();
            update = statement.executeUpdate(query);
        }catch(SQLException s){
            System.err.println("SQLException in the insertData method");
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

            conn = DriverManager.getConnection(url, username, password);


            //connect to database
            Class.forName("oracle.jdbc.driver.OracleDriver");

            if (conn == null) {
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

    public static void main(String[] args) {

        ////
        //  Store the database ip, username and password in a file or store it in "the properties" stuff the teacher said???
        ////

        try{
            RMI_Server servidor = new RMI_Server("192.168.56.101","1521","XE");
            servidor.execute();
        }catch(RemoteException r){
            System.out.println("RemoteException on the main method of the RMI Server");
        }
    }
}
