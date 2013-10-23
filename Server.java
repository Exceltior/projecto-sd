import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server {
    static ServerSocket acceptSocket;
    RequestQueue queue;
    boolean primary = true;

    private RMIConnection connection;

    public RMIConnection getConnection() {
        return connection;
    }

    void goSecondary() {
        System.out.println("Becoming SECONDARY server...");
        primary = false;
    }

    void goPrimary() {
        System.out.println("Becoming PRIMARY server...");
        primary = true;
    }

    // For a client to know if we're primary or not
    boolean isPrimary() {
        return primary;
    }

    void notifyConnectionToOtherServerDead() {
        //FIXME
        System.out.println("Lost connection to remote server...pinging RMI");


        InetAddress inet = null;
        //FIXME: RMI IP right here!
        try {
            inet = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            //We can't even resolve the server? It must be us!!!
            System.out.println("Can't resolve RMI IP. Assuming we've lost outer-world connection...");
            goSecondary();
            return;
        }

        // Means we wait 2 seconds while testing RMI...
        try {
            if ( inet.isReachable(2000) ) {
                System.out.println("Other server is dead, but we can ping RMI. Assuming primary role...");
                goPrimary();
            }
        } catch (IOException e) {
            System.err.println("Network error while trying to reach RMI server! Assuming we've lost outer-world " +
                    "connection...");
            goSecondary();
        }
    }

    void notifyConnectionToOtherServerBack() {
        System.out.println("Server is back!");
    }

    void execute(String[] args) throws IOException {
        int port;
        if ( args.length == 2 )
            port = Integer.valueOf(args[1]);
        else
            port = 1234;
        try {
            acceptSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Socket clientSocket = null;

        connection = new RMIConnection();
        connection.establishConnectionToRegistry();

        // Start the notification server
        new NotificationServer(this).start();


        // Port 1235 is where we will wait for the other server's ping. If we don't get it, means we can't reach it, or
        // it can't reach us...
        new UDPReceiver(1235, this, 2000).start();

        //FIXME: 'localhost' should be the IP of the other server. Also fix port
        new UDPTransmitter("localhost", 1235, 1000).start();

        queue = new RequestQueue(connection.getRMIInterface());
        queue.start();

        for(;;) {
            try {
                clientSocket = acceptSocket.accept();
                new Thread(new ServerClient(clientSocket, connection, this)).start();
            }
            catch (IOException e) {
                System.err.println("Accept failed!");
                e.printStackTrace();
            }
        }
    }

    static public void main(String[] args) throws IOException {
        Server server = new Server();
        server.execute(args);
    }
}
