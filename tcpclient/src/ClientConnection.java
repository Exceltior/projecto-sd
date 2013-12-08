import common.util.Common;
import model.data.Idea;
import model.data.NetworkingFile;

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
                                        // its own
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
    boolean sendTopicsArray(ArrayList<String> data){
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
     * Creates a new idea in the database of the application
     * @param title The title of the idea
     * @param description The description of the idea
     * @param topics A list of the topics' titles where we will include the idea
     * @return A boolean value indicating the success or failure of the operation
     */
    boolean createIdea(String title, String description, ArrayList<String> topics,float initialInvestment){
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

            if ( !Common.sendFloat(initialInvestment, outStream) ) {
                reconnect(); continue;
            }

            //Send topics
            if ( !sendTopicsArray(topics)){
                reconnect();continue;
            }

            //Get Final Confirmation
            reply = Common.recvMessage(inStream);

            return reply != Common.Message.ERR_NO_MSG_RECVD && reply == Common.Message.MSG_OK;

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
}
