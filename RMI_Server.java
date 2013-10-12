import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;

////
//  This is the RMI Server class, which will be responsible for interacting with the database, allowing the TCP Servers to commit
//  queries and other commands to the database
////
public class RMI_Server extends UnicastRemoteObject implements RMI_Interface{

    private Statement stmt;
    private Connection conn;
    private String url;

    public RMI_Server(String servidor, String porto, String sid, String username, String password) throws RemoteException {
        super();
        this.url = "jdbc:oracle:thin:@" + servidor + ":" + porto + ":" + sid;
        this.stmt = null;
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


    public int Login(String user, String pwd) throws RemoteException, SQLException{

        String query, result;

        //query = "Select u.username, u.pass from Utilizadores u where u.username = " + user + " and u.pass = " + pwd;
        query = "Select * from Utilizadores";

        result = ReceiveData(query);

        System.out.println(result);

        return 1;
    }

    ////
    //  Method responsible for executing queries like "Select..."
    ////
    public String ReceiveData(String query) throws RemoteException, SQLException{
        String nome, descricao, devolve = new String();
        int columnsNumber;

        stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery(query);//Execute the query
        ResultSetMetaData rsmd = rs.getMetaData();//Obtain the query's result metadata
        columnsNumber = rsmd.getColumnCount();//Get number of columns

        while (rs.next()){
            for (int i=0;i<columnsNumber;++i)
                devolve = devolve + rs.getString(i) + " | ";
            if (rs.next())
                devolve = devolve + "\n";
        }

        System.out.println("Vou devolver " + devolve);

        ////
        //  TO DO LIST NESTA FUNCAO:
        //      1- Decide how to handle the exceptions (try-catch or just throw them)
        //      2- See how to check the queries' result: Find how how many columns it has and how to return its content
        ////

        return devolve;
    }

    ////
    //  This method will be responsible for executing a query like "Insert ...". With this method we can create new registries in the
    //  database's tables
    ////
    public int InsertData(String query) throws RemoteException, SQLException{

        int update;

        stmt = conn.createStatement();
        update = stmt.executeUpdate(query);
        if (update != 0)//Data were sucessfully stored in the database
            return 1;
        else//There was an error storing the data in the database
            return -1;
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
