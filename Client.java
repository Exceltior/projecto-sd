public class Client {
    static public void main(String[] args) {
        ClientConnection conn = new ClientConnection();
        conn.connect();
        System.out.println(conn.login("Hakuna","Matata"));

        ClientTopic[] topics = conn.getTopics();

        for (Topic t : topics)
            System.out.println(t);

        for (;;);
    }
}
