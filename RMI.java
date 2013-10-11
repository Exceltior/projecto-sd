import java.sql.*;

public class RMI {

    private String serverName;
    private String portNumber;
    private String sid;
    private String url;

    RMI(String servidor, String porto, String Sid){
        this.serverName = servidor;
        this.portNumber = porto;
        this.sid = Sid;
        this.url = "jdbc:oracle:thin:@" + this.serverName + ":" + this.portNumber + ":" + this.sid;
    }

    public String getURL(){
        return this.url;
    }

    ////
    //  Metodo responsavel por executar queries do tipo "Select ..." na tabela Topico
    ////
    public void ReceiveDataTopico(String query, Statement stmt, Connection conn){
        String nome, descricao;
        int uid, tid;

        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);//Executar a query
            while (rs.next()) {
                tid = rs.getInt(1);
                nome = rs.getString(2);
                descricao = rs.getString(3);
                uid = rs.getInt(4);

                System.out.println(tid + "; " + nome + "; " + descricao + "; " + uid);
            }
        } catch (SQLException e ) {
            System.out.println("Excepção a imprimir resultados da query: " + e);
        } finally {
            if (stmt != null)
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
        }
    }

    public void InsertData(String query, Statement stmt, Connection conn){

        int update;

        try{
            stmt = conn.createStatement();
            update = stmt.executeUpdate(query);
            if (update != 0)
                System.out.println("Dados inseridos com sucesso");
            else
                System.out.println("Erro ao inserir dados");

        } catch (SQLException e ) {
            System.out.println("Excepção a inserir dados: " + e);
        }
    }

    public static void main(String[] args) throws Exception {

        String username = "sd", password = "sd", query;
        Statement stmt = null;
        RMI servidor = new RMI("192.168.56.101","1521","XE");
        Connection conn = DriverManager.getConnection(servidor.getURL(), username, password);

        ////
        //  FAZER METODOS REMOTOS PARA INSERIR DADOS, OBTER DADOS, ETC
        ////


        //connect to database
        Class.forName("oracle.jdbc.driver.OracleDriver");

        if (conn == null) {
            System.out.println("Failed to make connection!");
            return ;
        }

        System.out.println("You made it, take control your database now!");

        query = "select * from Topico";
        servidor.ReceiveDataTopico(query, stmt, conn);

        conn.close();
    }
}
