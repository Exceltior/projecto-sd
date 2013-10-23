import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
    }
}
