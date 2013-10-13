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

    private Statement statement;
    private Connection conn;
    private String url;

    public RMI_Server(String servidor, String porto, String sid, String username, String password) throws RemoteException {
        super();
        this.url = "jdbc:oracle:thin:@" + servidor + ":" + porto + ":" + sid;
        this.statement = null;
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
            e.printStackTrace();
        }

        ////
        //
        //
        //  FIXME: WHAT TO DO WITH THIS EXCEPTIONS???
        //
        //
        ////
    }


    public int login(String user, String pwd) throws RemoteException {

        String query;
        ArrayList<String[]> result;

        query = "Select u.username, u.pass from Utilizadores u where u.username = '" + user + "' and u.pass = '" + pwd + "'";

        result = receiveData(query);

        System.out.println(result.size());

        if ( result.size() > 0 )
            return Integer.valueOf(result.get(0)[0]);
        else
            return -1;
    }

    public ServerTopic[] getTopics() throws RemoteException  {
        String query = "Select * from Topicos";

        ArrayList<String[]> result = receiveData(query);

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
    //  Method responsible for executing queries like "Select..."
    //
    // Returns: null on failure, Arraylist with all columns (as strings in an array), which may be empty if there query
    // produces an empty table.
    //
    ////
    public ArrayList<String[]> receiveData(String query){
        int columnsNumber, pos = 0;
        ArrayList<String[]> result = new ArrayList<String[]>();

        try {
            statement = conn.createStatement();
        } catch (SQLException e) {
            System.err.println("Error creating SQL statement '" + query + "'!");
            return null;
        }

        ResultSet rs = null;//Execute the query
        ResultSetMetaData rsmd = null;//Obtain the query's result metadata
        try {
            rs = statement.executeQuery(query);
            rsmd = rs.getMetaData();
            columnsNumber = rsmd.getColumnCount();//Get number of columns
            while (rs.next()){
                result.add(new String[columnsNumber]);
                for (int i=1;i<=columnsNumber;++i){
                    result.get(pos)[i] = rs.getString(i);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error executing SQL query '" + query + "'!");
            return null;
        }

        return result;
    }

    ////
    //  This method will be responsible for executing a query like "Insert ...". With this method we can create new registries in the
    //  database's tables
    ////
    public boolean insertData(String query) throws RemoteException, SQLException{

        int update;

        statement = conn.createStatement();
        update = statement.executeUpdate(query);

        return update != 0;
    }

    public static void main(String[] args) {

        ////
        //  Store the database ip, username and password in a file or store it in "the properties" stuff the teacher said???
        ////

        String username = "sd", password = "sd", query;
        RMI_Server servidor = null;

        try{
            servidor = new RMI_Server("192.168.56.101","1521","XE",username,password);
            servidor.setConn(DriverManager.getConnection(servidor.getUrl(), username, password));


            //connect to database
            Class.forName("oracle.jdbc.driver.OracleDriver");

            if (servidor.getConn() == null) {
                System.out.println("Failed to make connection!");
                return ;
            }

            System.out.println("You made it, take control your database now!");

            //Start RMIRegistry programmatically
            Registry r = LocateRegistry.createRegistry(7000);
            r.rebind("academica", servidor);
            //Sabes que nunca estaras so... na 1ª ou 2ª divisao... Porque tu es a briosa, o orgulho do nosso coração!!!!

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
        //  FIXME: WHAT TO DO WITH THIS EXCEPTIONS???
        //
        //
        ////
    }
}
