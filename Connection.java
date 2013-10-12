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
public class Connection {
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
        Common.Messages reply;
        int intReply;
        for(;;) {
            if ( !Common.sendIntThroughStream(Common.Messages.MSG_LOGIN.ordinal(),outStream) ) {
                reconnect(); continue;
            }
            if ( !Common.sendStringThroughStream(user, outStream) ) {
                reconnect(); continue;
            }

            if ( !Common.sendStringThroughStream(pass, outStream) ) {
                reconnect(); continue;
            }

            if ( (intReply = Common.readIntFromStream(inStream)) == -1) {
                reconnect(); continue;
            }
                reply = Common.Messages.values()[intReply];

            return reply == Common.Messages.MSG_OK;

        }
    }

    ////
    // Get the list of topics from the server.
    //
    ClientTopic[] getTopics() {
        int numTopics;
        ClientTopic[] topics;
        for(;;) {
            if ( !Common.sendIntThroughStream(Common.Messages.MSG_GETTOPICS.ordinal(),outStream) ) {
                reconnect(); continue;
            }


            if ( (numTopics = Common.readIntFromStream(inStream)) == -1) {
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
