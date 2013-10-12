public class Client {
    static public void main(String[] args) {
        Connection conn = new Connection();
        conn.connect();
        System.out.println(conn.login("Hakuna","Matata"));
    }
}
