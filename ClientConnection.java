import java.io.*;
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
class ClientConnection {
    private static  String[] hosts = { "localhost", "localhost"};
    private static  int[] ports = { 1234, 4000 };
    private static  int[] notificationPorts = { 1237, 4001 };
    private int currentHost = -1;
    private Socket currentSocket = null;
    private DataOutputStream outStream = null;
    private DataInputStream inStream = null;
    private boolean loggedIn = false;
    private String lastUsername, lastPassword;
    private NotificationConnection notificationThread = null;


    ClientConnection(String[] args) {
        if ( args.length > 0 && args.length % 3 == 0) {
            hosts = new String[args.length / 3];
            ports = new int[args.length / 3];
            notificationPorts = new int[args.length / 3];
        }

        int c=0;
        for (int i = 0; i < args.length-2; i+=3) {
            hosts[c] = args[i];
            ports[c] = Integer.valueOf(args[i+1]);
            notificationPorts[c] = Integer.valueOf(args[i+2]);
            c++;
        }
    }

    /**
     * Connect to any server, starting from the next one
     */
    void connect() {
        do {
            try {
                currentHost = (currentHost+1) % hosts.length;
                //System.out.println(" Trying host " + currentHost + " - '" + hosts[currentHost] + "':" +
                //        ports[currentHost]);
                currentSocket = new Socket(hosts[currentHost], ports[currentHost]);
                outStream = new DataOutputStream(currentSocket.getOutputStream());
                inStream = new DataInputStream(currentSocket.getInputStream());

            } catch (IOException e) {
                //System.err.println("connect ERR"); e.printStackTrace();
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
            } catch (IOException ignored) {}

            //Try to connect again...I know this is recursive-based looping...but oh baby it feels so right.
            connect();

            //Once we get here, we're guaranteed to be connected to the primary...
        }
    }
    /**
     * Method to register a client to the database
     * @param username The username of the client's account
     * @param pass The password of the client's account
     * @param email The email of the client
     * @param date The date of the registry of the client
     * @return A boolean value, indicating the success or failure of the operation
     */
    boolean register(String username, String pass, String email, Date date){
        Common.Message reply;
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
        String s_date = format1.format(date);//Now we have the date in the 'yyyy-mm-dd' format

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

            return reply == Common.Message.MSG_OK;

        }
    }

    /**
     * Reconnect after a connection time out
     * @return Returns 0 if everything went well; 1 if there is a need to receive a reply from the server;
     *                 2 if there is a need to send a message to the server; 3 if there was a problem during the login
     */
    private int reconnect() {
       /* System.out.println(" Connection to " + currentHost + " - '" + hosts[currentHost] + "':" + ports[currentHost]
                + " dropped, initiating reconnecting process...");*/

        currentHost--; //We do currentHost-- so that we retry the current host ONCE.
        connect();

        if(this.loggedIn) {
            notificationThread.stop(); //FIXME: proper way to kill!
            //System.out.println("Estou logado");
            this.loggedIn = false;

            return this.login(lastUsername,lastPassword);
        }
        return 0;
    }

    /**
     * Try to login at destination with this user and password.
     * @param user The username of the user's account
     * @param pass The password of the user's account
     * @return Possible return values:
     *         0: ALL OKAY
     *         1: NEED_REPLY from server
     *         2: NEED_DISPATCH from server
     *         3: Problem logging in
     */
    int login(String user, String pass) {
        Common.Message reply;
        //Instead of reconnect() we call connect() here because we're not supposed to try to login... while trying to login!

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

                this.lastUsername = user;
                this.lastPassword = pass;
                this.loggedIn = true;
                notificationThread = new NotificationConnection(hosts[currentHost], user, pass,
                        notificationPorts[currentHost]);
                notificationThread.start();

                if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                    reconnect(); continue;
                }

                if ( reply == Common.Message.MSG_USER_HAS_PENDING_REQUESTS)
                    return 2;
                else if ( reply == Common.Message.MSG_USER_NOT_NOTIFIED_REQUESTS)
                    return 1;
                else
                    return 0;
            } else
                return 3;
        }
    }

    /**
     * Method responsible for creating a topic
     * @param nome The name of the topic
     * @param descricao The description of the topic
     * @return A boolean value, indicating the success or failure of the operation
     */
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
                reconnect(); continue;
            }

            return reply != Common.Message.ERR_NOT_LOGGED_IN && reply == Common.Message.MSG_OK;
        }
    }

    /**
     * Method used to send an ArrayList of objects "String" through the DataOutputStream
     * @param data The ArrayList of objects "String" we want to send
     * @return A boolean value indicating the success or failure of the operation
     */
    boolean sendData(ArrayList<String> data){
        //Send number of items

        if(data != null && data.size()>0){
            if (!Common.sendInt(data.size(),outStream))
                return false;

            //Send itens
            for (String aData : data) {
                if (! Common.sendString(aData, outStream))
                    return false;
            }
        }else{
            if(!Common.sendInt(-2,outStream))
                return false;
        }
        return true;
    }

    /**
     * Method used to send an ArrayList of objects "Integer" through the DataOutputStream
     * @param data The ArrayList of objects "Integer" we want to send
     * @return A boolean value indicating the success or failure of the operation
     */
    boolean sendInteger(ArrayList<Integer> data){
        //Send number of items

        if (data != null && data.size()>0){
            if (!Common.sendInt(data.size(),outStream))
                return false;

            //Send itens
            for (Integer aData : data) {
                if (! Common.sendInt(aData, outStream))
                    return false;
            }
        }else{
            if (!Common.sendInt(0,outStream))
                return false;
        }

        return true;
    }

    /**
     * Method used to set the relationship between two ideas
     * @param iidparent The id of the "parent" idea, that is, the idea which will serve as a "reference"
     * @param iidchild The id of the "child" idea, that is, the idea which will agree, disagree or be neutral with the
     *                 "parent" idea
     * @param type The type of relantionship we want to establish
     * @return A boolean value indicating the success or failure of the operation
     */
    boolean setRelationBetweenIdeas(int iidparent,int iidchild, int type){
        Common.Message reply;

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
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN ) {
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

    /**
     * Creates a new idea in the database of the application
     * @param title The title of the idea
     * @param description The description of the idea
     * @param nshares The number of shares of the idea
     * @param price The price of each share of the idea
     * @param topics A list of the topics' titles where we will include the idea
     * @param minNumShares The minimum number of shares the owner of the idea wants to keep to himself (or herslef)
     * @param ideasFor A list of the ids of the ideas that the idea to be created supports
     * @param ideasAgainst A list of the ids of the ideas that the idea to be created doesnt support
     * @param ideasNeutral A list of the ids of the ideas that the idea is neutral
     * @param ficheiro  The file to associate with the idea to be created
     * @return A boolean value indicating the success or failure of the operation
     */
    boolean createIdea(String title, String description, int nshares, int price, ArrayList<String> topics, int minNumShares, ArrayList<Integer> ideasFor, ArrayList<Integer> ideasAgainst, ArrayList<Integer> ideasNeutral,NetworkingFile ficheiro){
        Common.Message reply;


        for(;;) {
            if ( !Common.sendMessage(Common.Message.REQUEST_CREATEIDEA, outStream) ) {
                reconnect(); continue;
            }

            if ( (reply=Common.recvMessage(inStream)) == Common.Message.MSG_ERR ){
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NO_MSG_RECVD) {
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN ) {
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
            reply = Common.recvMessage(inStream);

            if (reply == Common.Message.ERR_NO_MSG_RECVD){
                System.err.println("Error while creating idea in the database");
                return false;
            }

            for (String topic : topics) {
                reply = Common.recvMessage(inStream);
                if (reply == Common.Message.ERR_NO_MSG_RECVD)
                    return false;

                if (reply == Common.Message.ERR_TOPIC_NAME)
                    System.out.println("Error while associating topic " + topic + ": Invalid topic name");
            }

            for (Integer anIdeasFor : ideasFor) {
                reply = Common.recvMessage(inStream);
                if (reply == Common.Message.ERR_NO_MSG_RECVD)
                    return false;

                if (reply == Common.Message.ERR_NO_SUCH_IID)
                    System.out.println("Error while associating idea " + anIdeasFor + ": Invalid idea name");
                //Else, we got MSG_OK, everything's fine, move along, nothing to see here
            }

            for (Integer anIdeasAgainst : ideasAgainst) {
                reply = Common.recvMessage(inStream);
                if (reply == Common.Message.ERR_NO_MSG_RECVD)
                    return false;

                if (reply == Common.Message.ERR_NO_SUCH_IID)
                    System.out.println("Error while associating idea " + anIdeasAgainst + ": Invalid idea name");
                //Else, we got MSG_OK, everything's fine, move along, nothing to see here
            }

            for (Integer anIdeasNeutral : ideasNeutral) {
                reply = Common.recvMessage(inStream);
                if (reply == Common.Message.ERR_NO_MSG_RECVD)
                    return false;

                if (reply == Common.Message.ERR_NO_SUCH_IID)
                    System.out.println("Error while associating idea " + anIdeasNeutral + ": Invalid idea name");
                //Else, we got MSG_OK, everything's fine, move along, nothing to see here
            }

            //Send file
            if (ficheiro != null){
                if(!Common.sendMessage(Common.Message.MSG_IDEA_HAS_FILE,outStream)){
                    reconnect();continue;
                }

                ObjectOutputStream objectStream;
                try {
                    objectStream = new ObjectOutputStream(outStream);
                    objectStream.writeObject(ficheiro);
                } catch (IOException e) {
                    //e.printStackTrace();
                    return false;
                }

            }else if(!Common.sendMessage(Common.Message.MSG_IDEA_DOESNT_HAVE_FILE,outStream)){
                reconnect();continue;
            }

            //Get Final Confirmation
            reply = Common.recvMessage(inStream);

            if (reply == Common.Message.ERR_NO_MSG_RECVD)
                return false;

            return reply == Common.Message.MSG_OK;

        }
    }

    /**
     * Buys shares of an idea
     * @param iid The idea id
     * @param numberSharesToBuy  Number of shares the user wants to buy
     * @param price  The price that he wants to sell each share
     * @param minNumberShares Minimum nunber of shares he doesnt want to sell (from the shares he bought)
     * @return boolean value, indicating if the transaction happened
     */
    public boolean buyShares(int iid,int numberSharesToBuy,int price,int minNumberShares){
        Common.Message reply;

        if (iid == -1)//Should never happen
            return false;

        if (price == -1)
            price = -2;

        if (minNumberShares == -1)
            minNumberShares = -2;

        for(;;){
            if ( !Common.sendMessage(Common.Message.REQUEST_BUYSHARES, outStream) ) {
                reconnect(); continue;
            }

            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN ) {
                return false;
            }

            //Send data
            if (!Common.sendInt(iid,outStream) ){
                reconnect();continue;
            }

            if (!Common.sendInt(numberSharesToBuy,outStream) ){
                reconnect();continue;
            }

            if (!Common.sendInt(price,outStream) ){
                reconnect();continue;
            }

            if (!Common.sendInt(minNumberShares,outStream) ){
                reconnect();continue;
            }

            //Get Confirmation
            reply = Common.recvMessage(inStream);

            if (reply == Common.Message.MSG_OK ) {
                System.out.println("Bought shares!");
                return true;
            } else {
                System.out.println("Couldn't buy shares (not enough money)! Waiting in queue...");
                return false;
            }
        }
    }

    /**
     * Gets an ArrayList of objects of type "Idea" containing all the ideas the user can buy
     * @return
     */
    ArrayList<Idea> getIdeasBuy(){
        Common.Message reply;
        int len;
        Idea temp;
        boolean needReconnect = false;
        ArrayList<Idea> devolve = new ArrayList<Idea>();

        for(;;){
            if ( !Common.sendMessage(Common.Message.REQUEST_GET_IDEAS_BUY, outStream) ) {
                reconnect(); continue;
            }

            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN )
                return null;

            //Receive Data
            if ( (len = Common.recvInt(inStream)) == -1){
                reconnect();continue;
            }

            for (int i=0;i<len;i++){
                temp = new Idea();
                if (!temp.readFromDataStream(inStream)){
                    needReconnect = true;
                    break;
                }
                devolve.add(temp);
            }

            if (needReconnect){
                needReconnect = false;
                reconnect();
                continue;
            }

            //Receive Final confirmation
            reply = Common.recvMessage(inStream);

            if (reply != Common.Message.MSG_OK)
                return null;

            return devolve;
        }

    }

    /**
     * Gets ideas by its iid and title
     * @param iid  The id of the idea (can be ommitted - Value = -1)
     * @param title The title of the idea (can be ommitted - Value = -1)
     * @return Array of objects "Idea" containing all the ideas with the given id and title
     */
    Idea[] getIdea(int iid, String title){
        Common.Message reply;
        Idea[] ideas;
        int len;
        boolean needReconnect = false;

        for(;;){
            if ( !Common.sendMessage(Common.Message.REQUEST_GET_IDEA, outStream) ) {
                reconnect(); continue;
            }

            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN )
                return null;

            if ( !Common.sendInt(iid,outStream)){
                reconnect();continue;
            }

            if ( !Common.sendString(title,outStream)){
                reconnect();continue;
            }

            reply = Common.recvMessage(inStream);

            if ( reply != Common.Message.MSG_OK)
                return null;

            if ( (len = Common.recvInt(inStream)) == -1){
                reconnect();continue;
            }

            ideas = new Idea[len];

            for (int i=0;i<len;i++){
                ideas[i] = new Idea();
                if (!ideas[i].readFromDataStream(inStream)){
                    needReconnect = true;
                    break;
                }
            }

            if (needReconnect){
                needReconnect = false;
                reconnect();
                continue;
            }

            reply = Common.recvMessage(inStream);

            if (reply != Common.Message.MSG_OK)
                return null;

            return ideas;
        }
    }

    /**
     * Get a topic by its id and name
     * @param tid The id of the topic
     * @param name The name of the topic
     * @return An instance of the "ClientTopic" class, containing the topic we wanted to obtain, or a null reference if
     * no topic was found
     */
    ClientTopic getTopic(int tid, String name){
        Common.Message reply;
        ClientTopic topic;
        int len;

        for(;;){
            if ( !Common.sendMessage(Common.Message.REQUEST_GETTOPIC, outStream) ) {
                reconnect(); continue;
            }

            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN )
                return null;

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

    /**
     * Gets a file associated with an idea whose id is "iid"
     * @param iid The id of the idea whose file we want to get
     * @return An instance of the class "NetworkingFile" which has the file associated with the given idea
     */
    NetworkingFile getIdeaFile(int iid){
        Common.Message reply;
        NetworkingFile ficheiro;
        ObjectInputStream objectStream;

        for(;;){
            if ( !Common.sendMessage(Common.Message.REQUEST_GET_IDEA_FILE, outStream) ) {
                reconnect(); continue;
            }

            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN )
                return null;

            //Send Data
            if (!Common.sendInt(iid,outStream)){
                reconnect();continue;
            }

            //Receive Data Confirmation
            if ( (reply = Common.recvMessage(inStream)) != Common.Message.MSG_OK ){
                return null;
            }

            //Receive Data
            try {
                objectStream = new ObjectInputStream(inStream);
                ficheiro = (NetworkingFile) objectStream.readObject();
            }catch(ClassNotFoundException c){
                return null;
            }catch(IOException i){
                return null;
            }

            //Receive Final Confirmation
            if ( (reply = Common.recvMessage(inStream)) != Common.Message.MSG_OK ){
                System.err.println("Couldnt receive the file!");
                return null;
            }

            return ficheiro;
        }
    }

    /**
     * Get every topic for the given idea
     * @param iid The id of the idea
     * @return An array of "ClientTopic" objects, with all the topics for the given idea
     */
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
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN ) {
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

            if ( (reply = Common.recvMessage(inStream)) != Common.Message.MSG_OK)
                return null;

            return devolve;
        }
    }

    /**
     * Returns all the ideas associated with a given user
     * @return An array of "Idea" objects, with all the ideas associated with a given user
     */
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
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN )
                return null;

            if (reply == Common.Message.ERR_IDEAS_NOT_FOUND)
                return null;

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

    /**
     * Get every idea in a given topic
     * @param topic The id of the topic whose ideas we want to get
     * @return An array of "Idea" objects with all the ideas in the given topic
     */
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
                 reconnect(); continue;
             }

             if ( reply == Common.Message.ERR_NOT_LOGGED_IN )
                 return null;

             if ( !Common.sendInt(topic,outStream)){
                 reconnect();continue;
             }

             if ( (ideaslen = Common.recvInt(inStream)) == -1){
                 reconnect();continue;
             }

             if (ideaslen > 0){
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
             }

             if ( (reply = Common.recvMessage(inStream)) != Common.Message.MSG_OK )
                 return null;

             return devolve;
         }
    }

    /**
     * Get the list of topics from the server.
     * @return An array of "ClientTopic" objects, with the list of topics from the server.
     */
    ClientTopic[] getTopics() {
        int numTopics;
        ClientTopic[] topics;
        Common.Message reply;
        for(;;) {
            if ( !Common.sendMessage(Common.Message.REQUEST_GETTOPICS,outStream) ) {
                reconnect(); continue;
            }

            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN )
                return null;

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

            if ( (reply = Common.recvMessage(inStream)) != Common.Message.MSG_OK )
                return null;

            return topics;
        }
    }
    /**
     * Get transaction history for the given user
     * @return An array of "String" objects, with the transactions made by the user
     */
    String[] showHistory(){
        String[] history;
        String temp;
        Common.Message reply;
        int numTransactions;
        boolean needReconnect = false;

        for(;;){
            if ( !Common.sendMessage(Common.Message.REQUEST_GET_HISTORY,outStream) ) {
                reconnect(); continue;
            }

            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN )
                return null;

            if ( (numTransactions = Common.recvInt(inStream)) == -1) {
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

            reply = Common.recvMessage(inStream);

            if ( reply != Common.Message.MSG_OK )
                return null;

            return history;
        }
    }

    /**
     * Get the ideas in favour, against or neutral to a given idea
     * @param iid The id of the idea whose relationship with other ideas we want to obtain
     * @param relationType The type of relationship we want to check
     * @return An array of "Idea" objects, with the ideas in favour, against or neutral to a given idea
     */
    Idea[] getIdeaRelations(int iid, int relationType){
        Common.Message reply;
        Idea[] ideasList;
        int numIdeas;
        boolean needReconnect = false;

        for(;;){
            if (relationType == 1){
                if ( !Common.sendMessage(Common.Message.REQUEST_GETIDEASFAVOUR,outStream) ) {
                    reconnect(); continue;
                }
            }

            else if(relationType == 0){
                if ( !Common.sendMessage(Common.Message.REQUEST_GETIDEASNEUTRAL,outStream) ) {
                    reconnect(); continue;
                }
            }

            else if(relationType == -1){
                if ( !Common.sendMessage(Common.Message.REQUEST_GETIDEASAGAINST,outStream) ) {
                    reconnect(); continue;
                }
            }

            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN )
                return null;

            //Send data
            if (!Common.sendInt(iid,outStream)){
                reconnect();continue;
            }

            //Get data confirmation
            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN )
                return null;

            if ( (numIdeas = Common.recvInt(inStream)) == -1) {
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

            reply = Common.recvMessage(inStream);

            if ( reply != Common.Message.MSG_OK ){
                System.err.println("Error in the getIdeaRelations method! MSG_OK not received");
                return null;
            }

            return ideasList;
        }
    }

    /**
     * Get the number of shares of a given idea not to sell instantaneously
     * @param iid The id of the idea
     * @return The number of shares of a given idea not to sell instantaneously
     */
    int getSharesNotSell(int iid){
        Common.Message reply;
        int shares;

        for(;;){
            if ( !Common.sendMessage(Common.Message.REQUEST_GETSHARESNOTSELL,outStream) ) {
                reconnect(); continue;
            }

            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN )
                return -1;

            //Send data
            if (!Common.sendInt(iid,outStream)){
                reconnect();continue;
            }

            //Receive Data confirmation
            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN )
                return -1;

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

    /**
     * Method responsible to set the number of shares of a given idea that a user does not want to sell instantaneously
     * @param iid The id of the idea
     * @param numberShares The number of shares of the given idea that the user does not want to sell instantaneously
     * @return A boolean value, indicating the success or failure of the operation
     */
    boolean setSharesNotSell(int iid, int numberShares){
        Common.Message reply;

        for(;;){
            if ( !Common.sendMessage(Common.Message.REQUEST_SETSHARESNOTSELL,outStream) ) {
                reconnect(); continue;
            }

            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN )
                return false;

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

    /**
     * Set the price of the shares of a given idea to a value defined by the user
     * @param iid The id of the idea
     * @param price The desired price the user wants to set for their shares of the given idea
     * @return A boolean value indicating the success or failure of the operation
     */
    boolean setPriceShares(int iid,int price){
        Common.Message reply;

        for(;;){
            if ( !Common.sendMessage(Common.Message.REQUEST_SETPRICESHARES,outStream) ) {
                reconnect(); continue;
            }

            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN )
                return false;

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
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN )
                return false;

            //Receive final confirmation
            reply = Common.recvMessage(inStream);

            return reply == Common.Message.MSG_OK;
        }
    }

    /**
     * Shows the price of the shares of a given idea
     * @param iid The id of the idea whose shares' prices we want to get
     * @return An array of "String" objects containing the prices of the shares for the given idea
     */
    String[] showPricesShares(int iid){
        Common.Message reply;
        String[] ideaShares;
        int len;
        boolean needReconnect = false;

        for(;;){
            if ( !Common.sendMessage(Common.Message.REQUEST_GETIDEASHARES,outStream) ) {
                reconnect(); continue;
            }

            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN )
                return null;

            //Send data
            if (!Common.sendInt(iid,outStream)){
                reconnect();continue;
            }

            //Get data confirmation
            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN )
                return null;

            if ( (len = Common.recvInt(inStream)) == -1){
                reconnect();continue;
            }

            ideaShares = new String[len];

            for (int i=0;i<len;i++){
                if ( (ideaShares[i] = Common.recvString(inStream)) == null)
                    needReconnect = true;
            }

            if (needReconnect){
                needReconnect = false;
                reconnect();
                continue;
            }

            //Get final confirmation
            reply = Common.recvMessage(inStream);
            if ( reply != Common.Message.MSG_OK )
                return null;

            return ideaShares;
        }
    }

    /**
     * Deletes an idea, stored in the database
     * @param iid The id of the idea we want to delete
     * @return A boolean value, indicating the success or failure of the operation
     */
    boolean deleteIdea(int iid) {
        Common.Message reply;
        for(;;){
            if ( !Common.sendMessage(Common.Message.REQUEST_DELETE_IDEA,outStream) ) {
                reconnect(); continue;
            }

            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NOT_LOGGED_IN )
                return false;

            if ( !Common.sendInt(iid, outStream) ) {
                reconnect(); continue;
            }


            if ( (reply = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD) {
                reconnect(); continue;
            }

            if ( reply == Common.Message.ERR_NO_SUCH_IID ) {
                System.out.println("Error while deleting the idea! No idea with that IID!");
                return false;

            } else if ( reply == Common.Message.ERR_IDEA_HAS_CHILDREN ) {
                System.err.println("Error deleting the idea! Idea has children!");
                return false;

            } else if (reply == Common.Message.ERR_NOT_FULL_OWNER){
                System.out.println("Error deleting the idea! Idea not fully owned by the user!");
                return false;
            }else if (reply == Common.Message.MSG_OK){
                System.out.println("Idea deleted with succes!");
                return true;
            }

            return false;
        }
    }
}
