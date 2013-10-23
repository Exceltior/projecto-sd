import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NotificationServer extends Thread {
    private Server server;

    NotificationServer(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        int port = 1237;

        Socket clientSocket = null;
        ServerSocket acceptSocket = null;
        try {
            acceptSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return;
        }

        for(;;) {
            try {
                clientSocket = acceptSocket.accept();
                new Thread(new NotificationClient(clientSocket, server.getConnection(), server)).start();
            }
            catch (IOException e) {
                System.err.println("Accept notification failed!");
                e.printStackTrace();
            }
        }
    }
}
