import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    static ServerSocket acceptSocket;
    static public void main(String[] args) throws IOException {
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

        for(;;) {
            try {
                clientSocket = acceptSocket.accept();
                new Thread(new ServerClient(clientSocket)).start();
            }
            catch (IOException e) {
                System.err.println("Accept failed!");
                e.printStackTrace();
            }
        }
    }
}
