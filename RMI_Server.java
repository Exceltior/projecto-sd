import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;

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
        //  FIXME: O QUE FAZER COM ESTAS EXCEPCOES NO CONSTRUTOR DO RMI SERVER???
        //
        //
        ////
    }

    ////
    //  Metodo responsavel por executar queries do tipo "Select ..."
    ////
    public String ReceiveData(String query) throws RemoteException, SQLException{
        String nome, descricao, devolve = new String();
        int columnsNumber;

        stmt = conn.createStatement();

        System.out.println("Vou executar o statement");
        ResultSet rs = stmt.executeQuery(query);//Executar a query
        ResultSetMetaData rsmd = rs.getMetaData();//Obter Metada do resultado da query
        columnsNumber = rsmd.getColumnCount();


        ////
        //  TO DO LIST NESTA FUNCAO:
        //      1- DECIDIR SE TRATAMOS AQUI AS EXCEPCOES (COM TRY-CATCH) OU SE AS DEIXAMOS A IR PARA O SERVIDOR
        //      2- VER COMO RECEBER RESULTADO DAS QUERIES (ASSUMIR QUE TUDO CORRE BEM): DESCOBRIR QUANTAS COLUNAS TEM
        //          A TABELA RESULTANTE E DEVOLVER O SEU CONTEUDO
        ////

        return devolve;
    }

    ////
    //  M\u00e9todo respons\u00e1vel por executar uma query do tipo "Insert..." . Com este m\u00e9todo podemos inserir entradas nas tabelas
    ////
    public int InsertData(String query) throws RemoteException, SQLException{

        int update;

        stmt = conn.createStatement();
        update = stmt.executeUpdate(query);
        if (update != 0)//Dados inseridos com sucesso
            return 1;
        else//Erro ao inserir os dados
            return -1;
    }

    public static void main(String[] args) {

        ////
        //  Meter o username da base de dados, a pass da base de dados e o IP da base de dados num ficheiro de texto ou naquela cena
        //  de properties que o prof disse na PL
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

            ////
            //  FIXME: VALE A PENA METER O PORTO DO REGISTRY NUMA VARIAVEL, OU NUM FICHEIRO DE TEXTO OU WHATEVER???
            ////

            r.rebind("benfica", servidor);
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
        //  FIXME: O QUE FAZER COM ESTAS EXCEPCOES NO CONSTRUTOR DO RMI SERVER???
        //
        //
        ////
    }
}
