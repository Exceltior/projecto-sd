import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

////
// Connection -- Manages talking to the destination. It makes sure the messages get to the destination by using
// multiple servers and checking if a message fails during its delivery, restarting the whole process with a new
// server. The reconnect() method achieves this, and is invoked whenever a broken connection is detected. After a
// call to reconnect(), the current command is usually restarted.
//
// NOTE that Connection will never return from its public methods if it has no connection, that is,
// if a command has not been correctly sent. Instead, Connection might loop forever until it manages to send the
// message to any available server. This eases our error checking code when using this class.
//
public class ClientConnection {
    private static final String[] hosts = { "localhost", "server2"};
    private static final int[] ports = { 1234, 1234 };
    private int currentHost = -1;
    private Socket currentSocket = null;
    private DataOutputStream outStream = null;
    private DataInputStream inStream = null;


    ////
    // Connect to any server, starting from the next one
    //
    void connect() {
        do {
            try {

                currentHost = (currentHost+1) % hosts.length;
                System.out.println(" Trying host " + currentHost + " - '" + hosts[currentHost] + "':" +
                        ports[currentHost]);
                currentSocket = new Socket(hosts[currentHost], ports[currentHost]);
                outStream = new DataOutputStream(currentSocket.getOutputStream());
                inStream = new DataInputStream(currentSocket.getInputStream());
            } catch (IOException e) {
                System.err.println("connect ERR"); e.printStackTrace();
            }
        } while ( currentSocket == null);
    }

    ////
    // Reconnect after a connection time out
    //
    private void reconnect() {
        System.out.println(" Connection to " + currentHost + " - '" + hosts[currentHost] + "':" + ports[currentHost]
                + " dropped, initiating reconnecting process...");
        connect();
    }

    ////
    // Try to login at destination with this user and password.
    //
    // Returns: true on successful login; false otherwise.
    //
    boolean login(String user, String pass) {
        Common.Message reply;
        for(;;) {
            if ( !Common.sendMessage(Common.Message.MSG_LOGIN, outStream) ) {
                reconnect(); continue;
            }
            if ( !Common.sendString(user, outStream) ) {
                reconnect(); continue;
            }

            if ( !Common.sendString(pass, outStream) ) {
                reconnect(); continue;
            }

            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                reconnect(); continue;
            }

            return reply == Common.Message.MSG_OK;

        }
    }

    ////
    // Get the list of topics from the server.
    //
    ClientTopic[] getTopics() {
        int numTopics;
        ClientTopic[] topics;
        Common.Message reply;
        for(;;) {
            if ( !Common.sendMessage(Common.Message.MSG_GETTOPICS,outStream) ) {
                reconnect(); continue;
            }

            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN ) {
                //Shouldn't happen, FIXME!
                System.err.println("Bodega");
                return null;
            }

            if ( (numTopics = Common.recvInt(inStream)) == -1) {
                reconnect(); continue;
            }

            topics = new ClientTopic[numTopics];

            boolean needReconnect = false;
            for (int i = 0; i < numTopics; i++) {
                if ( (topics[i] = ClientTopic.fromDataStream(inStream)) == null ) {
                    needReconnect = true;
                    break;
                }
            }
            if ( needReconnect ) {
                reconnect(); continue;
            }

            return topics;
        }
    }
}
