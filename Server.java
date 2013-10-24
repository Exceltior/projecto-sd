import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server {
    static ServerSocket acceptSocket;
    RequestQueue queue;
    private boolean primary = true;
    private boolean otherIsDown = false;

    private RMIConnection connection;
    private Thread requestThread;

    public RMIConnection getConnection() {
        return connection;
    }

    boolean isOtherDown() {
        return otherIsDown;
    }

    void goSecondary() {
        System.out.println("Becoming SECONDARY server...");
        primary = false;
        if ( !requestThread.isAlive())
            requestThread.stop();
    }

    void goPrimary() {
        System.out.println("Becoming PRIMARY server...");
        primary = true;
        if ( !requestThread.isAlive())
            requestThread.start();
    }

    // For a client to know if we're primary or not
    boolean isPrimary() {
        return primary;
    }

    void notifyConnectionToOtherServerDead() {
        otherIsDown = true;
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
                if ( !primary )
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
        int port, udpReceiverPort, udpTransmitterPort, notificationPort;
        String otherHost;

        if ( args.length >= 1 )
            port = Integer.valueOf(args[0]);
        else
            port = 1234;

        if ( args.length >= 2 )
            udpReceiverPort = Integer.valueOf(args[1]);
        else
            udpReceiverPort = 1235;

        if ( args.length >= 3 )
            udpTransmitterPort = Integer.valueOf(args[2]);
        else
            udpTransmitterPort = 1235;

        if ( args.length >= 4 )
            otherHost = args[3];
        else
            otherHost = "localhost";
        if ( args.length >= 5 )
            notificationPort = Integer.valueOf(args[4]);
        else
            notificationPort = 1237;
        if ( args.length >= 6 )
            primary = false;
        else
            primary = true;
        try {
            acceptSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Listening for incoming connections port "+port);
        System.out.println("Listening for UDP pings on port "+udpReceiverPort);
        System.out.println("Sending UDP pings to "+otherHost+":"+udpTransmitterPort);
        System.out.println("Notification server running on port "+notificationPort);
        System.out.println("Starting as primary: "+primary);

        Socket clientSocket = null;

        connection = new RMIConnection();
        connection.establishConnectionToRegistry();

        // Start the notification server
        new NotificationServer(this, notificationPort).start();


        // Port 1235 is where we will wait for the other server's ping. If we don't get it, means we can't reach it, or
        // it can't reach us...
        new UDPReceiver(udpReceiverPort, this, 2000).start();

        //FIXME: 'localhost' should be the IP of the other server. Also fix port
        new UDPTransmitter("localhost", udpTransmitterPort, 1000).start();

        queue = new RequestQueue(connection.getRMIInterface());
        requestThread = new Thread(queue);

        if ( primary )
            requestThread.start();

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
