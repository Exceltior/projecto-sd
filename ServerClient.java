import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

////
// This class, which implements an independent thread, is responsible for handling all requests from a given client
// (given to us referenced by its socket)
//
public class ServerClient implements Runnable {
    private Socket socket = null;
    private DataOutputStream outStream = null;
    private DataInputStream inStream = null;

    public ServerClient(Socket currentSocket) {
        this.socket = currentSocket;
        try {
            this.outStream = new DataOutputStream(currentSocket.getOutputStream());
            this.inStream = new DataInputStream(currentSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    @Override
    public void run() {
        ServerTopic topic = new ServerTopic(5, "Hello, Ladies", "Hakuna Matata");

        for(;;) {
            Common.Messages msg;
            int intMsg;

            // Read the next Message/Request
            if ( (intMsg = Common.readIntFromStream(inStream)) == -1) {
                break; //Connection dead!
            }
            msg = Common.Messages.values()[intMsg];

            // Handle the request
            if ( msg == Common.Messages.MSG_LOGIN)
                if ( !handleLogin() )
                    break ;

        }
    }

    private boolean handleLogin() {
        String user, pwd;

        if ( (user = Common.readStringFromStream(inStream)) == null)
            return false;
        if ( (pwd = Common.readStringFromStream(inStream)) == null)
            return false;

        // Do actual login handling code here

        if ( !Common.sendIntThroughStream(Common.Messages.MSG_OK.ordinal(),outStream) )
            return false;

        return true;
    }
}
