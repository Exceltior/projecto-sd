import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.RemoteException;

public class NotificationClient implements Runnable {
    private Socket socket = null;
    private DataOutputStream outStream = null;
    private DataInputStream inStream = null;

    private RMIConnection connection;
    private RMI_Interface RMIInterface = null;
    private Server server;

    // The client's uid. -1 means not logged in.
    private int uid = -1;

    public NotificationClient(Socket currentSocket, RMIConnection connection, Server server) {
        this.socket = currentSocket;
        this.connection = connection;
        this.server = server;
        try {
            this.outStream = new DataOutputStream(currentSocket.getOutputStream());
            this.inStream = new DataInputStream(currentSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error constructing a new NotificationClient (did the connection die?");
        }

        initRMIConnection();

    }

    private boolean initRMIConnection() {
        RMIInterface = connection.getRMIInterface();
        return RMIInterface != null;
    }

    @Override
    public void run() {
        // Get the user to give us username and password
        // If it works, cool, enter message loop otherwise, drop the connection
        String user, pwd;

        // Read the next Message/Request
        if ( ( user = Common.recvString(inStream)) == null){
            System.out.println("Error No Message Received!!!");
            return ;
        }

        if ( ( pwd = Common.recvString(inStream)) == null){
            System.out.println("Error No Message Received!!!");
            return ;
        }

        try {
            int uid = RMIInterface.canLogin(user, pwd);
        } catch (RemoteException e) {
            //FIXME: Try catch the merda
        }

        if ( uid == -1 ) {
            Common.sendMessage(Common.Message.MSG_ERR, outStream);
            return ;
        }

        System.out.println("Notification: User "+uid+" logged in.");

        /*
        try {
            socket.setSoTimeout(100);
        } catch (SocketException e) {
            System.err.println("Error accessing socket!");
            return ;
        }
        */
        ObjectOutputStream objStream = null;
        try {
            objStream = new ObjectOutputStream(outStream);
        } catch (IOException e) {
            System.err.println("Error creating object output stream");
            return;
        }
        for(;;) {
            /**
             * Note that when we do new NotificationQueue(RMIInterface, uid) we are talking to the RMI server and
             * getting the notification queue from it!
             */
            NotificationQueue queue = new NotificationQueue(RMIInterface, uid);

            Notification n;

            while ( (n = queue.getNextNotification() ) != null) {
                if(!n.writeToStream(objStream)) {
                    System.out.println("Notification: User "+uid+" dropped!");
                    return;
                }

            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {}
        }

    }
}
