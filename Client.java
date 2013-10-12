public class Client {
    static public void main(String[] args) {
        Connection2 conn = new Connection2();
        conn.connect();
        System.out.println(conn.login("Hakuna","Matata"));
    }
}
