import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

////
// "Static" class with auxiliary functions which perform some error checking and wrap exception handling.
//
// It also has the message ids that the client and server must pass to one another.
//
public class Common {
    static enum Messages { MSG_GETTOPICS, MSG_OK, MSG_LOGIN, MSG_ERR }

    static public boolean sendStringThroughStream(String s, DataOutputStream outStream) {
        if ( outStream == null ) {
            System.err.println("sendStringThroughStream ERR");
            return false;
        }

        try {
            outStream.writeUTF(s);
        } catch (IOException e) {
            System.err.println("sendStringThroughStream ERR"); e.printStackTrace();
            return false;
        }
        return true;
    }

    static public boolean sendIntThroughStream(int i, DataOutputStream outStream) {
        if ( outStream == null ) {
            System.err.println("sendIntThroughStream ERR");
            return false;
        }
        try {
            outStream.writeInt(i);
        } catch (IOException e) {
            System.err.println("sendIntThroughStream ERR"); e.printStackTrace();
            return false;
        }

        return true;
    }

    static public String readStringFromStream(DataInputStream inStream) {
        if ( inStream == null ) {
            System.err.println("readStringFromStream ERR");
            return null;
        }
        String ret = null;
        try {
            ret = inStream.readUTF();
        } catch (IOException e) {
            System.err.println("readStringFromStream ERR"); e.printStackTrace();
        }

        return ret;
    }
    static public int readIntFromStream(DataInputStream inStream) {
        if ( inStream == null ) {
            System.err.println("readIntFromStream ERR");
            return -1;
        }
        int ret = -1;
        try {
            ret = inStream.readInt();
        } catch (IOException e) {
            System.err.println("readIntFromStream ERR"); e.printStackTrace();
        }

        return ret;
    }
}
