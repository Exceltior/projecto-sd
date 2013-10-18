import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    //  Method to register a client to the database
    ////
    boolean register(String username, String pass, String email, Date date){
        Common.Message reply;
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
        String s_date = format1.format(date);//Now we have the date in the 'yyyy-mm-dd' format

        ////
        //  Ideia para o Maxi: Basicamente é mandar estes campos todos ao servidor de TCP que depois tem de invocar um metodo
        //  remoto que lhe permite fazer o registo do novo utilizador. Depois temos que arranjar uma forma de atribuir os id's
        //  aos utilizadores, nao sei se ha uma forma automatica de fazer isso na base de dados ou nao, mas depois ou hoje a
        //  noite ou amanha de manha vou ver se consigo fazer isso. Well, cya ;)
        ////

        for(;;) {
            if ( !Common.sendMessage(Common.Message.REQUEST_REG, outStream) ) {
                reconnect(); continue;
            }
            if ( !Common.sendString(username, outStream) ) {
                reconnect(); continue;
            }

            if ( !Common.sendString(pass, outStream) ) {
                reconnect(); continue;
            }

            if ( !Common.sendString(email, outStream) ) {
                reconnect(); continue;
            }

            if ( !Common.sendString(s_date, outStream) ) {
                reconnect(); continue;
            }

            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                reconnect(); continue;
            }

            System.out.println("Estou no client connection " + (reply == Common.Message.MSG_OK));
            return reply == Common.Message.MSG_OK;

        }
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
            if ( !Common.sendMessage(Common.Message.REQUEST_LOGIN, outStream) ) {
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

    boolean createTopic(String nome, String descricao){
        Common.Message reply;

        for(;;) {
            if ( !Common.sendMessage(Common.Message.REQUEST_CREATETOPICS, outStream) ) {
                reconnect(); continue;
            }
            if ( !Common.sendString(nome, outStream) ) {
                reconnect(); continue;
            }

            if ( !Common.sendString(descricao, outStream) ) {
                reconnect(); continue;
            }

            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                System.out.println("AQUI3");
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN ) {
                //Shouldn't happen, FIXME!
                System.err.println("AQUI4");
                return false;
            }

            return reply == Common.Message.MSG_OK;

        }
    }

    boolean createIdea(String title, String description, int nshares, int price, String[] topics){
        Common.Message reply;
        int devolve = -1;
        boolean skip = false;

        for(;;) {
            if ( !Common.sendMessage(Common.Message.REQUEST_CREATEIDEA, outStream) ) {
                reconnect(); continue;
            }
            if ( !Common.sendString(title, outStream) ) {
                reconnect(); continue;
            }

            if ( !Common.sendString(description, outStream) ) {
                reconnect(); continue;
            }

            if ( !Common.sendInt(nshares, outStream) ) {
                reconnect(); continue;
            }

            if ( !Common.sendInt(price, outStream) ) {
                reconnect(); continue;
            }

            if (!Common.sendInt(topics.length,outStream)){
                reconnect();continue;
            }

            //Send topics
            for (String topic : topics) {
                if (!Common.sendString(topic, outStream)) {
                    reconnect();skip = true; break;
                }
            }
            if (skip)
                continue;

            //Get Confirmations
            reply = Common.recvMessage(inStream);

            if (reply == Common.Message.ERR_NO_MSG_RECVD){
                System.err.println("Error while creating idea in the database");
                return false;
            }

            //If we get here we added the idea to the database, but not the topics!

            //Get more confirmations
            reply = Common.recvMessage(inStream);
            while (reply != Common.Message.MSG_OK){
                if (reply == Common.Message.MSG_ERR)//Error, going to return false
                    System.out.println("Error while associating topics to the idea. All of the topics may not be associated with it");
                else if (reply == Common.Message.ERR_TOPIC_NAME){
                    //Invalid topic name
                    String wrongTopic = Common.recvString(inStream);
                    System.out.println("Topic name '" + wrongTopic + "' is invalid!");
                }
            }

            return true;
        }
    }

    ///
    //  Get every idea in a given topic
    ///

    Idea[] getTopicIdeas(int topic){
        Common.Message reply;
        Idea[] devolve = null;
        Idea temp = new Idea();
        int ideaslen;

         for(;;){
             if ( !Common.sendMessage(Common.Message.REQUEST_GETTOPICSIDEAS, outStream) ) {
                 reconnect(); continue;
             }

             if ( !Common.sendInt(topic,outStream)){
                 reconnect();continue;
             }

             if ( (ideaslen = Common.recvInt(inStream)) == -1){
                 reconnect();continue;
             }

             //Receive ideas
             devolve = new Idea[ideaslen];
             for (int i=0;i<ideaslen;i++){
                 temp.readFromDataStream(inStream);
                 devolve[i] = temp;
             }

             return devolve;
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
            if ( !Common.sendMessage(Common.Message.REQUEST_GETTOPICS,outStream) ) {
                System.err.println("AQUI");
                reconnect(); continue;
            }

            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                System.err.println("AQUI2");
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN ) {
                //Shouldn't happen, FIXME!
                System.err.println("Bodega");
                return null;
            }

            if ( (numTopics = Common.recvInt(inStream)) == -1) {
                System.err.println("AQUI3");
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
