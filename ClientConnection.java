import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
    private boolean loggedIn = false;
    private String lastUsername, lastPassword;


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

        Common.Message serverMsg;
        if ( (serverMsg = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {

            //Try to connect again...I know this is recursive-based looping...but oh baby it feels so right.
            connect();

            return;
        }

        if ( serverMsg == Common.Message.ERR_NOT_PRIMARY ) {

            //Close the connection and keep trying until we find the primary server
            try {
                currentSocket.close();
            } catch (IOException e) {}

            //Try to connect again...I know this is recursive-based looping...but oh baby it feels so right.
            connect();

            //Once we get here, we're guaranteed to be connected to the primary...
        }
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

            if ( reply == Common.Message.MSG_OK) {
                this.loggedIn = true;
                this.lastPassword = pass;
                this.lastUsername = username;
            }
            return reply == Common.Message.MSG_OK;

        }
    }

    ////
    // Reconnect after a connection time out
    //
    private int reconnect() {
        System.out.println(" Connection to " + currentHost + " - '" + hosts[currentHost] + "':" + ports[currentHost]
                + " dropped, initiating reconnecting process...");

        currentHost--; //We do currentHost-- so that we retry the current host ONCE.
        // FIXME: We might as well sleep for a while here too...
        connect();

        ////
        //  FIXME O QUE FAZER QUANDO LASTUSERNAME E LASTPASSWORD SAO NULOS??????
        //  MAXI AQUI
        ////
        System.out.println("AQUI " + lastUsername + " " + lastPassword + "\n\n\n");
        login(lastUsername,lastPassword);//FIXME FIXME: O JOCA METEU ESTA LINHA AQUI

        if(this.loggedIn) {

            System.out.println("Estou logado");
            this.loggedIn = false;
            int loginReply;
            if (( loginReply = this.login(lastUsername, lastPassword) ) == 3) {
                System.err.println("Something's gone HORRIBLY WRONG!!!!"); //FIXME: SHIT!
            }

            return loginReply;
        }
        return 0;
    }

    ////
    // Try to login at destination with this user and password.
    //
    // Returns: true on successful login; false otherwise.
    //
    // FIXME: We're using magic numbers, but fuck it!
    // 0: ALL OKAY
    // 1: NEED_REPLY from server
    // 2: NEED_DISPATCH from server
    // 3: Problem logging in
    //
    // FIXME: All these reconnect()s here should probably be connect()s...since reconnect() will call login()!
    //
    int login(String user, String pass) {
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

            if ( reply == Common.Message.MSG_OK ) {

                //FIXME FIXME O JOCA ADICIONOU AS PROXIMAS DUAS LINHAS
                this.lastUsername = user;
                this.lastPassword = pass;

                if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                    reconnect(); continue;
                }

                if ( reply == Common.Message.MSG_USER_HAS_PENDING_REQUESTS) {
                    return 2;
                } else if ( reply == Common.Message.MSG_USER_NOT_NOTIFIED_REQUESTS) {
                    return 1;
                } else {
                    return 0; //MSG_OK; ALL OKAY!
                }

            } else {
                return 3;
            }


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

    boolean sendData(ArrayList<String> data){
        //Send number of items

        if(data != null && data.size()>0){
            if (!Common.sendInt(data.size(),outStream))
                return false;

            //Send itens
            for (int i=0;i<data.size();i++) {
                if (!Common.sendString(data.get(i), outStream))
                    return false;
            }
        }else{
            if(!Common.sendInt(-2,outStream))
                return false;
        }
        return true;
    }

    boolean sendInteger(ArrayList<Integer> data){
        //Send number of items

        if (data != null && data.size()>0){
            if (!Common.sendInt(data.size(),outStream))
                return false;

            //Send itens
            for (int i=0;i<data.size();i++) {
                if (!Common.sendInt(data.get(i), outStream))
                    return false;
            }
        }else{
            if (!Common.sendInt(0,outStream))
                return false;
        }

        return true;
    }

    boolean setRelationBetweenIdeas(int iidparent,int iidchild, int type){
        Common.Message reply;

        //We do this to stop the "They're trying to hack us" message
        //Don't send -1 fields
        if (type == -1)
            type = -2;

        for(;;){
            if ( !Common.sendMessage(Common.Message.REQUEST_SETIDEARELATION, outStream) ) {
                reconnect(); continue;
            }

            if ( (reply=Common.recvMessage(inStream)) == Common.Message.MSG_ERR ){
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NO_MSG_RECVD) {
                System.err.println("AQUI2");
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN ) {
                //Shouldn't happen, FIXME!
                System.err.println("Bodega");
                return false;
            }

            //Send idea1
            if (!Common.sendInt(iidparent,outStream)){
                reconnect();continue;
            }

            //Send idea2
            if (!Common.sendInt(iidchild,outStream)){
                reconnect();continue;
            }

            //Send Relationship Type
            if(!Common.sendInt(type,outStream)){
                reconnect();continue;
            }

            //Get final confirmation
            reply = Common.recvMessage(inStream);

            return reply == Common.Message.MSG_OK;
        }
    }

    boolean createIdea(String title, String description, int nshares, int price, ArrayList<String> topics, int minNumShares, ArrayList<Integer> ideasFor, ArrayList<Integer> ideasAgainst, ArrayList<Integer> ideasNeutral,NetworkingFile ficheiro){
        Common.Message reply;
        int devolve = -1;


        for(;;) {
            if ( !Common.sendMessage(Common.Message.REQUEST_CREATEIDEA, outStream) ) {
                reconnect(); continue;
            }

            if ( (reply=Common.recvMessage(inStream)) == Common.Message.MSG_ERR ){
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NO_MSG_RECVD) {
                System.err.println("AQUI2");
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN ) {
                //Shouldn't happen, FIXME!
                System.err.println("Bodega");
                return false;
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

            if( !Common.sendInt(minNumShares,outStream) ){
                reconnect();continue;
            }

            //Send topics
            if ( !sendData(topics)){
                reconnect();continue;
            }

            //Send ideas for
            if ( !sendInteger(ideasFor)){
                reconnect();continue;
            }

            //Send ideas against
            if ( !sendInteger(ideasAgainst)){
                reconnect();continue;
            }

            //Send ideas neutral
            if ( !sendInteger(ideasNeutral)){
                reconnect();continue;
            }

            //Get Confirmations of data except topics and ideas relations
            // OS DADOS QUE FORAM ENVIADOS, EXCEPTO AS IDEIAS E OS TÓPICOS, 'TÁ TUDO BEM, INCLUSIVE AS SHARES, TÁ TUDO
            // MARADINHO, MANINHO, FIXME
            reply = Common.recvMessage(inStream);

            if (reply == Common.Message.ERR_NO_MSG_RECVD){
                System.err.println("Error while creating idea in the database");
                return false;
            }

            for (int i = 0; i < topics.size(); i++) {
                reply = Common.recvMessage(inStream);
                if (reply == Common.Message.ERR_NO_MSG_RECVD){
                    System.err.println("Error while merdas"); /* FIXME */
                    return false;
                }
                if (reply == Common.Message.ERR_TOPIC_NAME)
                    System.out.println("Error while associating topic "+topics.get(i)+": Invalid topic name");
                else if (reply == Common.Message.MSG_ERR)
                    System.out.println("Error while associating topic "+topics.get(i)+": RMI FODEU-SE");
            }

            for (int i = 0; i < ideasFor.size(); i++) {
                reply = Common.recvMessage(inStream);
                if (reply == Common.Message.ERR_NO_MSG_RECVD){
                    System.err.println("Error while merdas 1"); /* FIXME */
                    return false;
                }
                if (reply == Common.Message.ERR_NO_SUCH_IID)
                    System.out.println("Error while associating idea "+ideasFor.get(i)+": Invalid idea name");
                /* Else, we got MSG_OK, everything's fine, move along, nothing to see here */
            }

            for (int i = 0; i < ideasAgainst.size(); i++) {
                reply = Common.recvMessage(inStream);
                if (reply == Common.Message.ERR_NO_MSG_RECVD){
                    System.err.println("Error while merdas 2"); /* FIXME */
                    return false;
                }
                if (reply == Common.Message.ERR_NO_SUCH_IID)
                    System.out.println("Error while associating idea "+ideasAgainst.get(i)+": Invalid idea name");
                /* Else, we got MSG_OK, everything's fine, move along, nothing to see here */
            }

            for (int i = 0; i < ideasNeutral.size(); i++) {
                reply = Common.recvMessage(inStream);
                if (reply == Common.Message.ERR_NO_MSG_RECVD){
                    System.err.println("Error while merdas 3"); /* FIXME */
                    return false;
                }
                if (reply == Common.Message.ERR_NO_SUCH_IID)
                    System.out.println("Error while associating idea "+ideasNeutral.get(i)+": Invalid idea name");
                /* Else, we got MSG_OK, everything's fine, move along, nothing to see here */
            }

            //Send fie
            if (ficheiro != null){
                if(!Common.sendMessage(Common.Message.MSG_IDEA_HAS_FILE,outStream)){
                    reconnect();continue;
                }

                ObjectOutputStream objectStream = null;
                try {
                    objectStream = new ObjectOutputStream(outStream);
                    objectStream.writeObject(ficheiro);
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    System.err.println("FILHA DA PUTA");
                    return false;
                }



            }else if(!Common.sendMessage(Common.Message.MSG_IDEA_DOESNT_HAVE_FILE,outStream)){
                reconnect();continue;
            }

            //Get Final Confirmation
            reply = Common.recvMessage(inStream);

            if (reply == Common.Message.ERR_NO_MSG_RECVD){
                System.err.println("Error while merdas 4"); /* FIXME */
                return false;
            }

            return reply == Common.Message.MSG_OK;

        }
    }

    ////
    //  Get an idea by its iid and title
    ////
    Idea[] getIdea(int iid, String title){
        Common.Message reply;
        Idea[] ideas;
        int len;

        for(;;){
            if ( !Common.sendMessage(Common.Message.REQUEST_GET_IDEA, outStream) ) {
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

            if ( !Common.sendInt(iid,outStream)){
                reconnect();continue;
            }

            if ( !Common.sendString(title,outStream)){
                reconnect();continue;
            }

            if ( (reply = Common.recvMessage(inStream)) != Common.Message.MSG_OK) {
                System.err.println("Bodega2: "+reply);
                return null;
            }

            if ( (len = Common.recvInt(inStream)) == -1){
                reconnect();continue;
            }

            ideas = new Idea[len];

            for (int i=0;i<len;i++){
                ideas[i] = new Idea();
                ideas[i].readFromDataStream(inStream);
            }

            reply = Common.recvMessage(inStream);

            if (reply != Common.Message.MSG_OK)
                return null;

            return ideas;
        }
    }

    ////
    // Get a topic by its tid and name
    ////
    ClientTopic getTopic(int tid, String name){
        Common.Message reply;
        ClientTopic topic = null;
        int len;

        for(;;){
            if ( !Common.sendMessage(Common.Message.REQUEST_GETTOPIC, outStream) ) {
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

            if ( !Common.sendInt(tid,outStream)){
                reconnect();continue;
            }

            if ( !Common.sendString(name,outStream)){
                reconnect();continue;
            }

            reply = Common.recvMessage(inStream);

            if (reply == Common.Message.TOPIC_OK){

                topic = ClientTopic.fromDataStream(inStream);


                reply = Common.recvMessage(inStream);
                if (reply != Common.Message.MSG_OK)
                    return null;
                return topic;
            }

            return null;
        }
    }

    ////
    //  Get every topic for the given idea
    ////
    ClientTopic[] getIdeaTopics(int iid){
        Common.Message reply;
        ClientTopic[] devolve;
        int len;
        boolean needReconnect = false;

        for(;;){
            if ( !Common.sendMessage(Common.Message.REQUEST_GET_TOPICS_OF_IDEA, outStream) ) {
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

            if ( !Common.sendInt(iid,outStream)){
                reconnect();continue;
            }

            if ( (len = Common.recvInt(inStream)) == -1){
                reconnect();continue;
            }

            devolve = new ClientTopic[len];

            for (int i=0;i<len;i++){
                if ( (devolve[i] = ClientTopic.fromDataStream(inStream)) == null ){
                    needReconnect = true;
                    break;
                }
            }

            if ( needReconnect ) {
                reconnect(); continue;
            }

            if ( (reply = Common.recvMessage(inStream)) != Common.Message.MSG_OK) {
                System.err.println("AQUI2");
                reconnect(); continue;
            }

            return devolve;
        }
    }

    ////
    //  Returns all the ideas associated with a given user
    ////

    Idea[] getIdeasFromUser(){
        Idea[] devolve;
        int numIdeas;
        Common.Message reply;
        boolean needReconnect = false;

        for(;;){
            if ( !Common.sendMessage(Common.Message.REQUEST_GETUSERIDEAS, outStream) ) {
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

            if (reply == Common.Message.ERR_IDEAS_NOT_FOUND){
                System.err.println("Idea not found!");
                return null;
            }

            //No need to send the used id, because it is already stored in the Server

            if ( (numIdeas = Common.recvInt(inStream)) == -1){
                reconnect();continue;
            }

            devolve = new Idea[numIdeas];

            for (int i=0;i<numIdeas;i++){
                devolve[i] = new Idea();
                if ( !devolve[i].readFromDataStream(inStream) ){
                    needReconnect = true;
                    break;
                }
            }

            if ( needReconnect ) {
                reconnect(); continue;
            }

            if ( (reply = Common.recvMessage(inStream)) != Common.Message.MSG_OK ){
                System.err.println("Error while receiving the user's ideas");
                return null;
            }

            return devolve;
        }
    }

    ///
    //  Get every idea in a given topic
    ///
    Idea[] getTopicIdeas(int topic){
        Common.Message reply;
        Idea[] devolve = null;
        int ideaslen;
        boolean needReconnect = false;

         for(;;){
             if ( !Common.sendMessage(Common.Message.REQUEST_GETTOPICSIDEAS, outStream) ) {
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

             if ( !Common.sendInt(topic,outStream)){
                 reconnect();continue;
             }

             if ( (ideaslen = Common.recvInt(inStream)) == -1){
                 reconnect();continue;
             }

             //Receive ideas
             devolve = new Idea[ideaslen];

             for (int i=0;i<ideaslen;i++){
                 devolve[i] = new Idea();
                 if ( !devolve[i].readFromDataStream(inStream) ){
                     needReconnect = true;
                     break;
                 }
             }

             if ( needReconnect ) {
                 reconnect(); continue;
             }

             if ( (reply = Common.recvMessage(inStream)) != Common.Message.MSG_OK ){
                 System.err.println("Error in the getTopicsIdeas method! MSG_OK not received");
                 return null;
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

            System.out.println("O numero de topicos e " + numTopics);

            boolean needReconnect = false;
            for (int i = 0; i < numTopics; i++) {
                if ( (topics[i] = ClientTopic.fromDataStream(inStream)) == null ) {
                    System.out.println("DEU ASNEIRA");
                    needReconnect = true;
                    break;
                }
            }
            if ( needReconnect ) {
                reconnect(); continue;
            }

            if ( (reply = Common.recvMessage(inStream)) != Common.Message.MSG_OK ){
                System.err.println("Error in the getTopics method! MSG_OK not received");
                return null;
            }

            return topics;
        }
    }

    ////
    //  Get transaction history for the given user
    ////
    String[] showHistory(){
        String[] history;
        String temp;
        Common.Message reply;
        int numTransactions = -1;
        boolean needReconnect = false;

        for(;;){
            if ( !Common.sendMessage(Common.Message.REQUEST_GET_HISTORY,outStream) ) {
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

            if ( (numTransactions = Common.recvInt(inStream)) == -1) {
                System.err.println("AQUI3");
                reconnect(); continue;
            }

            history = new String[numTransactions];

            for(int i=0; i<numTransactions;++i){
                if ( (temp = Common.recvString(inStream)) == null ){
                    needReconnect = true;
                    break;
                }
                history[i] = temp;
            }

            if ( needReconnect ) {
                reconnect(); continue;
            }

            if ( (reply = Common.recvMessage(inStream)) != Common.Message.MSG_OK )
                return null;

            return history;
        }
    }

    Idea[] getIdeaRelations(int iid, int relationType){
        Common.Message reply;
        Idea[] ideasList;
        int numIdeas;
        boolean needReconnect = false;

        for(;;){
            if (relationType == 1){
                if ( !Common.sendMessage(Common.Message.REQUEST_GETIDEASFAVOUR,outStream) ) {
                    System.err.println("AQUI");
                    reconnect(); continue;
                }
            }

            else if(relationType == 0){
                if ( !Common.sendMessage(Common.Message.REQUEST_GETIDEASNEUTRAL,outStream) ) {
                    System.err.println("AQUI2");
                    reconnect(); continue;
                }
            }

            else if(relationType == -1){
                if ( !Common.sendMessage(Common.Message.REQUEST_GETIDEASAGAINST,outStream) ) {
                    System.err.println("AQUI3");
                    reconnect(); continue;
                }
            }

            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                System.err.println("AQUI4");
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN ) {
                //Shouldn't happen, FIXME!
                System.err.println("Bodega");
                return null;
            }

            //Send data
            if (!Common.sendInt(iid,outStream)){
                System.err.println("AQUI5");
                reconnect();continue;
            }

            //Get data confirmation
            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                System.err.println("AQUI2");
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN ) {
                //Shouldn't happen, FIXME!
                System.err.println("Bodega");
                return null;
            }

            if ( (numIdeas = Common.recvInt(inStream)) == -1) {
                System.err.println("AQUI6");
                reconnect(); continue;
            }


            ideasList = new Idea[numIdeas];

            for (int i=0;i<numIdeas;i++){
                ideasList[i] = new Idea();
                if ( !ideasList[i].readFromDataStream(inStream) ){
                    needReconnect = true;
                    break;
                }
            }

            if ( needReconnect ) {
                reconnect(); continue;
            }

            if ( (reply = Common.recvMessage(inStream)) != Common.Message.MSG_OK ){
                System.err.println("Error in the getIdeaRelations method! MSG_OK not received");
                return null;
            }

            return ideasList;
        }
    }

    ////
    //  Get the number of shares of a given idea not to sell instantaneously
    ////
    int getSharesNotSell(int iid){
        Common.Message reply;
        int shares = -1;

        for(;;){
            if ( !Common.sendMessage(Common.Message.REQUEST_GETSHARESNOTSELL,outStream) ) {
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
                return -1;
            }

            //Send data
            if (!Common.sendInt(iid,outStream)){
                reconnect();continue;
            }

            //Receive Data confirmation
            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                System.err.println("AQUI2");
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN ) {
                //Shouldn't happen, FIXME!
                System.err.println("Bodega");
                return -1;
            }

            //Receive data
            if( (shares = Common.recvInt(inStream)) == -1){
                reconnect();continue;
            }

            //Receive final confirmation
            reply = Common.recvMessage(inStream);

            if (reply == Common.Message.MSG_OK)
                return shares;

            return -1;
        }
    }

    ////
    //  Method responsible to set the number of shares of a given idea that a user does not want to sell instantaneously
    ////
    boolean setSharesNotSell(int iid, int numberShares){
        Common.Message reply;

        for(;;){
            if ( !Common.sendMessage(Common.Message.REQUEST_SETSHARESNOTSELL,outStream) ) {
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
                return false;
            }

            //Send data
            if (!Common.sendInt(iid,outStream)){
                reconnect();continue;
            }

            //Send data
            if (!Common.sendInt(numberShares,outStream)){
                reconnect();continue;
            }

            //Receive final confirmation
            reply = Common.recvMessage(inStream);

            return reply == Common.Message.MSG_OK;
        }
    }

    ////
    //  Set the price of the shares of a given idea to a value defined by the user
    ////
    boolean setPriceShares(int iid,int price){
        Common.Message reply;

        for(;;){
            if ( !Common.sendMessage(Common.Message.REQUEST_SETPRICESHARES,outStream) ) {
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
                return false;
            }

            //Send data
            if (!Common.sendInt(iid,outStream)){
                reconnect();continue;
            }

            //Send data
            if (!Common.sendInt(price,outStream)){
                reconnect();continue;
            }

            //Receive Data confirmation
            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                System.err.println("AQUI2");
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN ) {
                //Shouldn't happen, FIXME!
                System.err.println("Bodega");
                return false;
            }

            //Receive final confirmation
            reply = Common.recvMessage(inStream);

            return reply == Common.Message.MSG_OK;
        }
    }

    ////
    //  Shows the price of the shares of a given idea
    ////
    String[] showPricesShares(int iid){
        Common.Message reply;
        String[] ideaShares = null;
        int len;
        boolean needReconnect = false;

        for(;;){
            if ( !Common.sendMessage(Common.Message.REQUEST_GETIDEASHARES,outStream) ) {
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

            //Send data
            if (!Common.sendInt(iid,outStream)){
                reconnect();continue;
            }

            //Get data confirmation
            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                System.err.println("AQUI2");
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN ) {
                //Shouldn't happen, FIXME!
                System.err.println("Bodega");
                return null;
            }

            if ( (len = Common.recvInt(inStream)) == -1){
                reconnect();continue;
            }

            ideaShares = new String[len];

            for (int i=0;i<len;i++){
                if ( (ideaShares[i] = Common.recvString(inStream)) == null)
                    needReconnect = true;
            }

            if (needReconnect){
                reconnect();
                continue;
            }

            //Get final confirmation
            if ( (reply = Common.recvMessage(inStream)) != Common.Message.MSG_OK ){
                System.err.println("Error in the getIdeaRelations method! MSG_OK not received");
                return null;
            }

            return ideaShares;
        }
    }

    boolean deleteIdea(int iid) {
        Common.Message reply;
        for(;;){
            if ( !Common.sendMessage(Common.Message.REQUEST_DELETE_IDEA,outStream) ) {
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
                return false;
            }

            if ( !Common.sendInt(iid, outStream) ) {
                reconnect(); continue;
            }


            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                System.err.println("AQUI2");
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NO_SUCH_IID ) {
                //Shouldn't happen, FIXME!
                System.out.println("No idea with that IID"); //FIXME: See what we have to print here
                return false;
            } else if ( reply == Common.Message.ERR_IDEA_HAS_CHILDREN ) {
                //Shouldn't happen, FIXME!
                System.err.println("Idea has children"); //FIXME: See what we have to print here
                return false;
            }

            return true;
        }
    }
}
