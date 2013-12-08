import common.util.Common;
import model.RMI.RMIConnection;
import model.data.*;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;

////
// This class, which implements an independent thread, is responsible for handling all requests from a given client
// (given to us referenced by its socket)
//
public class ServerClient implements Runnable {
    private Socket           socket    = null;
    private DataOutputStream outStream = null;
    private DataInputStream  inStream  = null;

    private final RMIConnection connection;
    private final Server        server;

    // The client's uid. -1 means not logged in.
    private int uid = -1;

    private static final int limit_characters_topic = 20;//Number of characters for the topic's name

    public ServerClient(Socket currentSocket, RMIConnection connection, Server server) {
        this.socket = currentSocket;
        this.connection = connection;
        this.server = server;
        try {
            this.outStream = new DataOutputStream(currentSocket.getOutputStream());
            this.inStream = new DataInputStream(currentSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error constructing a new ServerClient (did the connection die?");
        }

        if(!initRMIConnection()) {
            System.err.println("Error getting the RMI connection!");
        }
    }

    private boolean initRMIConnection() {

        connection.waitUntilRMIIsUp();

        // Should never return false!
        return connection.getRMIInterface() != null;
    }

    private boolean isLoggedIn() {
        return uid != -1;
    }


    @Override
    public void run() {

        if ( !server.isPrimary() ) {
            // We don't even care if the message goes out!
            Common.sendMessage(Common.Message.ERR_NOT_PRIMARY, outStream);
            try {
                socket.close();
            } catch (IOException ignored)
            {} // We don't care if the uer didn't get the ERR_NOT_PRIMARY message
            server.removeSocket(socket);
            return ;
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream)) {
            System.out.println("Connection to a client dropped while starting!");
            server.removeSocket(socket);
            return;
        }

        for(;;) {
            Common.Message msg;

            // Read the next Message/Request
            if ( ( msg = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD){
                System.out.println("Error No Message Received!!!");
                break ;
            }

            if ( isLoggedIn() ) {
                try {
                    connection.getRMIInterface().updateUserTime(uid);
                } catch (RemoteException e) {
                    connection.onRMIFailed();
                    server.killSockets();
                    break;
                }
            }


            // Handle the request
            if ( msg == Common.Message.REQUEST_LOGIN) {
                if ( !handleLogin() ){
                    //System.err.println("Error in the handle login method!!!");
                    break ;
                }
            } else if (msg == Common.Message.REQUEST_REG){
                if (!handleRegistration()){
                    //System.err.println("Error in the handle registration method!!!");
                    break ;
                }
            } else if (msg == Common.Message.REQUEST_GET_IDEA_BY_IID){
                if ( !handleGetIdeaByIID() ){
                    //System.err.println("Error in the handle get idea by IID method!!!");
                    break ;
                }
            }else if(msg == Common.Message.REQUEST_CREATEIDEA){
                if ( !handleCreateIdea()){
                    //System.err.println("Error in the handle create idea method!!!");
                    break ;
                }
            }else if (msg == Common.Message.REQUEST_GET_TOPICS_OF_IDEA){
                if (!handleGetTopicsOfIdea()){
                    //System.err.println("Error in the handle get topics of idea method!!!!!!");
                    break;
                }

            }else if(msg == Common.Message.REQUEST_GETIDEASHARES){
                if (!handleGetIdeaShares()){
                    //System.err.println("Error in the handle get idea shares method!!!!!!!!");
                    break;
                }
            }else if (msg == Common.Message.REQUEST_BUYSHARES){
                if (!handleBuyShares()){
                    //System.err.println("Error in the handle buy shares method!!!!!");
                    break;
                }
            }
        }

        if ( !isLoggedIn() )
            System.out.println("Connection to UID "+uid+" dropped!");
        else
            System.out.println("Connection to a client dropped!");
        server.removeSocket(socket);
    }

    private boolean handleRegistration(){
        String user, pass, date, email;
        boolean registration; // true = managed to register, false = problems registering

        if ( (user = Common.recvString(inStream)) == null)
            return false;
        if ( (pass = Common.recvString(inStream)) == null)
            return false;
        if ( (email = Common.recvString(inStream)) == null)
            return false;
        if ( (date = Common.recvString(inStream)) == null)
            return false;

        //System.out.println("Vou registar:" +  user + " " + pass + " " + email + " " + date);


        try {
            registration=connection.getRMIInterface().register(user,pass,email);
        } catch (RemoteException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            registration = false;
        }

        if (registration){
            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream) )
                return false;
        } else {
            if ( !Common.sendMessage(Common.Message.MSG_ERR, outStream) )
                return false;
        }

        return true;
    }

    ////
    //  Get the list of topics where a given idea is
    ////
    private boolean handleGetTopicsOfIdea(){
        int iid;
        ServerTopic[] list_topics;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        if ( (iid = Common.recvInt(inStream)) == -1)
            return false;

        try{
            list_topics = connection.getRMIInterface().getIdeaTopics(iid);
        }catch(RemoteException r){
            System.err.println("Error while getting ideas from topic");
            connection.onRMIFailed();
            server.killSockets();
            return false;
        }

        if (!Common.sendInt(list_topics.length,outStream))
            return false;

        for (ServerTopic list_topic : list_topics) {
            if (! list_topic.writeToDataStream(outStream))
                return false;
        }

        return Common.sendMessage(Common.Message.MSG_OK, outStream);
    }

    private String[] receiveData(){ //YA JOCA, QUE NOME DE MERDA PARA ESTA FUNÇÃO. FIXME.
        String[] data;
        int numIdeas;
        String temp;

        if ( (numIdeas = Common.recvInt(inStream)) == -2)
            return new String[0];

        data = new String[numIdeas];
        for (int i=0;i<numIdeas;i++){
            if( (temp = Common.recvString(inStream)) == null)
                return null;
            data[i] = temp;
        }

        return data;
    }

    private boolean receiveInt(ArrayList<Integer> ideias){
        int numIdeas, temp;

        if ( (numIdeas = Common.recvInt(inStream)) == -1) {
            return false;
        }

        for (int i=0;i<numIdeas;i++){
            if( (temp = Common.recvInt(inStream)) == -1)
                return false;
            ideias.add(temp);
        }

        return true;
    }

    private boolean handleBuyShares(){
        int iid, price, numberSharesToBuy, minNumberShares, numSharesAlreadyHas;
        boolean check;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        //Receive Data
        if ( (iid = Common.recvInt(inStream)) == -1)
            return false;

        if ( (numberSharesToBuy = Common.recvInt(inStream)) == -1)
            return false;

        if ( (price = Common.recvInt(inStream)) == -1)
            return false;

 //       if (price == -2)//To void the "They're trying to hack us" message
 //           price = -1;

        if ( (minNumberShares = Common.recvInt(inStream)) == -1)
            return false;

        float maxPricePerShare = 100, targetSellPrice = 100;
        boolean addToQueueOnFailure = true; //FIXMEREFACTOR

        BuySharesReturn ret;

        try {
            ret=connection.getRMIInterface().buyShares(uid,iid,maxPricePerShare,numberSharesToBuy,
                                                         addToQueueOnFailure,
                                                   targetSellPrice);
        } catch (RemoteException e) {
            ret=null;
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if (ret!=null && ret.result.contains("OK")){
            if (!Common.sendMessage(Common.Message.MSG_OK,outStream))
                return false;
        } else {
            if(!Common.sendMessage(Common.Message.MSG_ERR,outStream))
                return false;
        }

        return true;
    }
    ////
    //  Creates a new Idea
    ////
    private boolean handleCreateIdea(){
        Common.Message reply;
        String title, description, topic;
        String[] topicsArray;
        //int[] ideasForArray, ideasAgainstArray, ideasNeutralArray;
        ArrayList<Integer> ideasForArray = new ArrayList<Integer>(),
                           ideasAgainstArray = new ArrayList<Integer>(),
                           ideasNeutralArray = new ArrayList<Integer>();
        int nshares, price, result, numMinShares;
        boolean result_topics, result_shares;
        NetworkingFile ficheiro = null;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
           return false;

        if ( (title = Common.recvString(inStream)) == null)
            return false;

        if ( (description = Common.recvString(inStream)) == null)
            return false;

        if ( (nshares = Common.recvInt(inStream)) == -1)
            return false;
        if ( (price = Common.recvInt(inStream)) == -1)
            return false;

        if ( (numMinShares = Common.recvInt(inStream)) == -1)
            return false;

        //Receive Topics
        if ( (topicsArray = receiveData()) == null )
            return false;

        //Receive Ideas For
        if ( !receiveInt(ideasForArray) )
            return false;

        //Receive Ideas Against
        if ( !receiveInt(ideasAgainstArray))
            return false;

        //Receive Ideas Neutral
        if ( !receiveInt(ideasNeutralArray))
            return false;

        float moneyInvested = 100; //FIXMEREFACTOR
        ArrayList<String> topics = new ArrayList<String>(); //FIXMEREFACTOR
        try {
            result = connection.getRMIInterface().createIdea(title,description,uid,moneyInvested,topics,ficheiro);
        } catch (RemoteException e) {
            result = -1;
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        if (result < 0){
            if ( !Common.sendMessage(Common.Message.MSG_ERR, outStream) )
                return false;

            return true;
        }

        //Everything ok
        if(!Common.sendMessage(Common.Message.MSG_OK,outStream))
            return false;

        return true;
    }

    ////
    //  Gets an idea from its id
    ////
    private boolean handleGetIdeaByIID() {
        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        int iid;

        if ( (iid = Common.recvInt(inStream)) == -1)
            return false;

        Idea idea;

        try {
            idea = connection.getRMIInterface().getIdeaByIID(iid,uid);
        } catch (RemoteException e) {
            System.err.println("RMI exception while fetching an idea by its IID");
            connection.onRMIFailed();
            server.killSockets();
            return false;
        }

        if ( idea == null ) {
            // There is no idea with this ID
            if ( !Common.sendMessage(Common.Message.ERR_NO_SUCH_IID, outStream))
                return false;
        } else {
            // Got the idea, let's send it
            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
                return false;

            if ( !idea.writeToDataStream(outStream) )
                return false;

            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
                return false;
        }

        return true;
    }

    private boolean handleGetIdeaShares(){
        int iid;
        Share ideaShares;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        //Receive data
        if ( (iid = Common.recvInt(inStream)) == -1){
            return false;
        }

        try{
            ideaShares = connection.getRMIInterface().getIdeaShares(iid,uid);
        }catch(RemoteException r){
            System.err.println("RemoteException in the handle get idea shares method!!");
            connection.onRMIFailed();
            server.killSockets();
            return false;
        }

        if (ideaShares == null){
            return Common.sendMessage(Common.Message.MSG_ERR, outStream);
        }else{
            //Send data confirmation
            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
                return false;
        }
/** FIXMEREFACTOR: Como é que é esta merda?!
        //Send Data
        if (!Common.sendInt(ideaShares.size(),outStream))
            return false;

        for (String ideaShare : ideaShares) {
            if (! Common.sendString(ideaShare, outStream))
                return false;
        }

        //Send final confirmation*/
        return Common.sendMessage(Common.Message.MSG_OK, outStream);

    }

    ////
    //  Only returns false if we lost connection
    ////
    private boolean handleLogin() {
        String user, pwd;

        if ( isLoggedIn() ) {
            // Can't login without logging out!
            Common.sendMessage(Common.Message.MSG_ERR,outStream);
            return true; // Message was handled (note that we return true because not being logged in is not a
                         // connection error
        }

        if ( (user = Common.recvString(inStream)) == null)
            return false;
        if ( (pwd = Common.recvString(inStream)) == null)
            return false;

        ArrayList<Object> objects = new ArrayList<Object>();
        objects.add(user);
        objects.add(pwd);

        try {
            this.uid = connection.getRMIInterface().login(user,pwd);
        } catch (RemoteException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if ( uid == -1 )
            return true;

        // Message was handled successfully
        return true;
    }
}
