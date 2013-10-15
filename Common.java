import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

////
// "Static" class with auxiliary functions which perform some error checking and wrap exception handling.
//
// It also has the message ids that the client and server must pass to one another.
//
public class Common {
    static enum Message {
        REQUEST_GETTOPICS, MSG_OK, REQUEST_LOGIN, MSG_ERR, ERR_NO_MSG_RECVD, ERR_NOT_LOGGED_IN, REQUEST_REG,
        REQUEST_CREATETOPICS, REQUEST_GET_IDEA_BY_IID, ERR_NO_SUCH_IID, REQUEST_CREATEIDEA
    }

    static public boolean sendMessage(Message msg, DataOutputStream outStream) {
        return Common.sendInt(msg.ordinal(), outStream);
    }
    static public Message recvMessage(DataInputStream inStream) {
        int intMsg;
        if ( (intMsg = Common.recvInt(inStream)) == -1) {
            return Message.ERR_NO_MSG_RECVD; //Connection dead!
        }

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
            outStream.writeInt(i);
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
                // FIXME: Deal with this!
            }
        } catch (IOException e) {
            //System.err.println("recvInt ERR"); e.printStackTrace();
        }

        return ret;
    }
}
