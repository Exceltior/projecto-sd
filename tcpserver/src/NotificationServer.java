import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NotificationServer extends Thread {
    private Server server;
    private int port;

    NotificationServer(Server server, int port) {
        this.server = server; this.port = port;
    }

    @Override
    public void run() {

        Socket clientSocket;
        ServerSocket acceptSocket;
        try {
            acceptSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return;
        }

        for(;;) {
            try {
                clientSocket = acceptSocket.accept();
                server.getConnection().testRMINow();
                if ( server.getConnection().RMIIsDown() ) {
                    clientSocket.close();
                    continue;
                }
                server.addNotificationSocket(clientSocket);
                new Thread(new NotificationClient(clientSocket, server.getConnection(), server)).start();
            }
            catch (IOException e) {
                //System.err.println("Accept notification failed!");
            }
        }
    }
}
