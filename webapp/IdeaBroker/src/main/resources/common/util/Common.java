package common.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * FIXME: This is old and dated!
 */
////
// "Static" class with auxiliary functions which perform some error checking and wrap exception handling.
//
// It also has the message ids that the client and server must pass to one another.
//
public class Common {
    static enum Message {
        MSG_OK,                     // Generic OK message
        MSG_ERR,                    // Generic ERROR message
        REQUEST_GETTOPICS,          // Get topics
        REQUEST_LOGIN,              // Initiate Login process
        REQUEST_REG,                // Initiate Register process
        REQUEST_CREATETOPICS,       // Create a topic
        REQUEST_GET_IDEA_BY_IID,    // Get an Idea only by IID
        REQUEST_CREATEIDEA,         // Create an Idea
        REQUEST_GETTOPICSIDEAS,     // Get ideas associated with topic
        REQUEST_GET_HISTORY,        // Get rmiserver.Transaction History
        REQUEST_GETTOPIC,           // Get a topic
        REQUEST_GET_IDEA,           // Get one or more ideas from an IID or title
        REQUEST_DELETE_IDEA,        // Delete an Idea
        REQUEST_GET_TOPICS_OF_IDEA, // Get Idea topics
        REQUEST_GETUSERIDEAS,       // Get the ideas of a given user
        REQUEST_GETIDEASFAVOUR,     //Get the ideas in favour of a given idea
        REQUEST_GETIDEASAGAINST,    //Get the ideas against a given idea
        REQUEST_GETIDEASNEUTRAL,    //Get the ideas neutral to a given idea
        REQUEST_SETIDEARELATION,    //Set the relationship between two ideas
        REQUEST_GETIDEASHARES,      //Get the number of shares for a given idea and its price
        REQUEST_SETPRICESHARES,     //Set the price of a user's given idea's shares to a value defined by the user
        REQUEST_GETSHARESNOTSELL,   //Get the number of shares of a given idea not to sell instantaneously
        REQUEST_SETSHARESNOTSELL,   //Set the number of shares of a given idea not to sell instantaneously
        REQUEST_GETFILE,            //Get a file associated with a given idea
        REQUEST_GET_IDEAS_FILES,    //Get a list with the id's of the files which have a file attached
        REQUEST_GET_IDEA_FILE,      //Get a file from an idea
        REQUEST_GET_IDEAS_BUY,      //Gets all the ideas a user can buy
        REQUEST_BUYSHARES,          //The user wants to buy shares



        ERR_NOT_LOGGED_IN,          // User Not logged in
        ERR_TOPIC_NOT_FOUND,        // Topic not found
        ERR_IDEA_HAS_CHILDREN,      // Idea has children (and shouldn't, in this cenario)
        ERR_NOT_PRIMARY,            // Server isn't primary server, so tell client to disconnect
        ERR_TOPIC_NAME,             // Invalid topic name
        ERR_NO_SUCH_IID,            // No idea by this IID
        ERR_NO_MSG_RECVD,           // Used by our Common class to indicate a connection problem
        ERR_IDEAS_NOT_FOUND,        // No Ideas were found
                                    // and, hence, we couldn't just use MSG_ERR?
        ERR_NO_FILE,                // Error while getting the file
        ERR_NOT_FULL_OWNER,         // Error not the full owner of the idea
        
        TOPIC_OK,                    // Topic is Okay.

        MSG_USER_HAS_PENDING_REQUESTS,
        MSG_USER_NOT_NOTIFIED_REQUESTS,
        MSG_IDEA_HAS_FILE,
        MSG_IDEA_DOESNT_HAVE_FILE
    }

    static public boolean sendMessage(Message msg, DataOutputStream outStream) {
        //System.out.println("SENDING: "+msg.name());
        return Common.sendInt(msg.ordinal(), outStream);
    }
    static public Message recvMessage(DataInputStream inStream) {
        int intMsg;
        if ( (intMsg = Common.recvInt(inStream)) == -1) {
            return Message.ERR_NO_MSG_RECVD; //Connection dead!
        }

        //System.out.println("Got Message: "+Message.values()[intMsg].name());
        return Message.values()[intMsg];
    }

    static public boolean sendString(String s, DataOutputStream outStream) {
        if ( outStream == null ) {
            //System.err.println("sendString ERR");
            return false;
        }

        try {
            outStream.writeUTF(s);
        } catch (IOException e) {
            //System.err.println("sendString ERR"); e.printStackTrace();
            return false;
        }
        return true;
    }

    static public boolean sendInt(int i, DataOutputStream outStream) {
        if ( outStream == null ) {
            //System.err.println("sendInt ERR");
            return false;
        }
        try {
            if (i==-1)
                System.out.println("WE ARE SENDING THE INTEGER -1 -> THIS WILL NOT END WELL");
            outStream.writeInt(i);
        } catch (IOException e) {
            //System.err.println("sendInt ERR"); e.printStackTrace();
            return false;
        }

        return true;
    }

    static public boolean sendFloat(float i, DataOutputStream outStream) {
        if ( outStream == null ) {
            //System.err.println("sendInt ERR");
            return false;
        }
        try {
            if (i==-1)
                System.out.println("WE ARE SENDING THE FLOAT -1 -> THIS WILL NOT END WELL");
            outStream.writeFloat(i);
        } catch (IOException e) {
            //System.err.println("sendInt ERR"); e.printStackTrace();
            return false;
        }

        return true;
    }




    static public String recvString(DataInputStream inStream) {
        if ( inStream == null ) {
            //System.err.println("recvString ERR");
            return null;
        }
        String ret = null;
        try {
            ret = inStream.readUTF();
        } catch (IOException e) {
            //System.err.println("recvString ERR"); e.printStackTrace();
        }

        return ret;
    }
    static public int recvInt(DataInputStream inStream) {
        if ( inStream == null ) {
            //System.err.println("recvInt ERR");
            return -1;
        }
        int ret = -1;
        try {
            ret = inStream.readInt();
            if ( ret == -1 ) {
                System.err.println("They're trying to hack us!");
                inStream.close(); // Our humble attempt at solving this
            }
        } catch (IOException e) {
            //System.err.println("recvInt ERR"); e.printStackTrace();
        }

        return ret;
    }

    static public float recvFloat(DataInputStream inStream) {
        if ( inStream == null ) {
            //System.err.println("recvInt ERR");
            return -1;
        }
        float ret = -1;
        try {
            ret = inStream.readFloat();
            if ( ret == -1 ) {
                System.err.println("They're trying to hack us!");
                inStream.close(); // Our humble attempt at solving this
            }
        } catch (IOException e) {
            //System.err.println("recvInt ERR"); e.printStackTrace();
        }

        return ret;
    }
}
