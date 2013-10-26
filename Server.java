import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RMISecurityManager;
import java.util.ArrayList;

public class Server {
    static ServerSocket acceptSocket;
    RequestQueue queue;
    private boolean primary = true;
    private boolean otherIsDown = false;

    private RMIConnection connection;
    private Thread requestThread;
    private final ArrayList<Socket> sockets  = new ArrayList<Socket>();
    private final ArrayList<Socket> notificationSockets  = new ArrayList<Socket>();

    public void addNotificationSocket(Socket s) {
        synchronized (notificationSockets) {
            notificationSockets.add(s);
        }
    }
    public void removeNotificationSocket(Socket s) {
        synchronized (notificationSockets) {
            if (notificationSockets.contains(s))
                notificationSockets.remove(s);
        }
    }

    public void removeSocket(Socket s) {
        synchronized (sockets) {
            if (sockets.contains(s))
               sockets.remove(s);
        }
    }

    public void killSockets() {
        synchronized (sockets) {
            for (Socket s : sockets)
                try {
                    s.close();
                } catch (IOException e) {
                    System.err.println("Couldn't force close a socket!");
                }
            sockets.clear();
        }

        synchronized (notificationSockets) {
            for (Socket s : notificationSockets)
                try {
                    s.close();
                } catch (IOException e) {
                    System.err.println("Couldn't force close a notification socket!");
                }
            notificationSockets.clear();
        }
    }

    public RMIConnection getConnection() {
        return connection;
    }

    boolean isOtherDown() {
        return otherIsDown;
    }

    void goSecondary() {
        System.out.println("Becoming SECONDARY server...");
        primary = false;

        if ( queue != null )
            queue.killThread();
    }

    void goPrimary() {
        System.out.println("Becoming PRIMARY server...");
        primary = true;
        if ( requestThread == null ) {
            startQueueThread();
        } else if ( !requestThread.isAlive()) {
            queue.notifyStartingAgain();
            requestThread.start();
        }
    }

    // For a client to know if we're primary or not
    boolean isPrimary() {
        return primary;
    }

    synchronized void notifyConnectionToOtherServerDead() {
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

    synchronized void notifyConnectionToOtherServerBack() {
        System.out.println("Server is back!");
    }

    void execute(String[] args) throws IOException {
        int port, udpReceiverPort, udpTransmitterPort, notificationPort;
        String otherHost, RMIHost;

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
            RMIHost = args[5];
        else
            RMIHost="localhost";
        if ( args.length >= 7 )
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

        connection = new RMIConnection(RMIHost);
        connection.connect(); //FIXME: Handle this failing
        connection.start();

        // Start the notification server
        new NotificationServer(this, notificationPort).start();


        // Port 1235 is where we will wait for the other server's ping. If we don't get it, means we can't reach it, or
        // it can't reach us...
        new UDPReceiver(udpReceiverPort, this, 2000).start();

        //FIXME: 'localhost' should be the IP of the other server. Also fix port
        new UDPTransmitter("localhost", udpTransmitterPort, 1000).start();

        startQueueThread();

        if ( primary )
            requestThread.start();

        for(;;) {
            try {
                clientSocket = acceptSocket.accept();
                connection.testRMINow();
                if ( connection.RMIIsDown() ) {
                    clientSocket.close();
                    continue;
                }
                synchronized (sockets) {
                    sockets.add(clientSocket);
                }
                new Thread(new ServerClient(clientSocket, connection, this)).start();
            }
            catch (IOException e) {
                System.err.println("Accept failed!");
                e.printStackTrace();
            }
        }
    }

    synchronized private void startQueueThread() {
        queue = new RequestQueue(connection, this);
        requestThread = new Thread(queue);
    }

    static public void main(String[] args) throws IOException {
        System.getProperties().put("java.security.policy", "security.policy");
        System.setSecurityManager(new RMISecurityManager());
        Server server = new Server();
        server.execute(args);
    }
}
