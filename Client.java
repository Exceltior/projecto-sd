public class Client {
    static public void main(String[] args) {
        ClientConnection conn = new ClientConnection();
        conn.connect();
        System.out.println(conn.login("Hakuna","Matata"));
    }
}
