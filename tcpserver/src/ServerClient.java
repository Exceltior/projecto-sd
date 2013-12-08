import common.util.Common;
import model.RMI.RMIConnection;
import model.data.*;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;

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
            } else if(msg == Common.Message.REQUEST_CREATEIDEA){
                if ( !handleCreateIdea()){
                    //System.err.println("Error in the handle create idea method!!!");
                    break ;
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


    private String[] receiveTopicsArray(){
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
        int iid, numberSharesToBuy;
        boolean check;
        float maxPricePerShare = 0, targetSellPrice = 0;
        boolean addToQueueOnFailure = true; //FIXMEREFACTOR

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

        if ( (maxPricePerShare = Common.recvFloat(inStream)) == -1)
            return false;

        if ( (targetSellPrice = Common.recvFloat(inStream)) == -1)
            return false;

        if ( targetSellPrice == -2) targetSellPrice = -1;


        BuySharesReturn ret;

        try {
            ret=connection.getRMIInterface().buyShares(uid,iid,maxPricePerShare,numberSharesToBuy,
                                                         addToQueueOnFailure,
                                                   targetSellPrice);
        } catch (RemoteException e) {
            ret=null;
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if (ret!=null && !ret.result.contains("NOBUY")){
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
        String title, description;
        int result;
        float moneyInvested ;
        String[] topicsArray;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
           return false;

        if ( (title = Common.recvString(inStream)) == null)
            return false;

        if ( (description = Common.recvString(inStream)) == null)
            return false;

        if ( (moneyInvested = Common.recvFloat(inStream)) == -1)
            return false;

        //Receive Topics
        if ( (topicsArray = receiveTopicsArray()) == null )
            return false;

        ArrayList<String> topics = new ArrayList<String>(topicsArray.length); //FIXMEREFACTOR
        Collections.addAll(topics, topicsArray);
        try {
            result = connection.getRMIInterface().createIdea(title,description,uid,moneyInvested,topics,null);
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

        try {
            this.uid = connection.getRMIInterface().login(user,pwd);
        } catch (RemoteException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if ( uid == -1 )
            if ( !Common.sendMessage(Common.Message.MSG_ERR,outStream) )
                return false;

        System.out.println("Client goes");
        return Common.sendMessage(Common.Message.MSG_OK, outStream);
    }
}
