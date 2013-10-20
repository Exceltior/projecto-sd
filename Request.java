import java.io.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class Request implements Serializable {
    public enum RequestType { SQL_INSERT, SQL_SELECT, SET_TOPICS_IDEA, LOGIN}
    int uid;
    RequestType requestType;
    ArrayList<Object> requestArguments = new ArrayList<Object>();
    Timestamp timestamp;

    // We need to store the queryResult here for various reasons. Most notably, because after the dispatcher thread is
    // done working with a Request, the original thread must have access to the output...
    //
    // This might be a queryResult, such as an ArrayList<String[]>, or simply an ArrayList with a boolean value for
    // a call to setTopicsIdea, for instance!
    ArrayList<Object> requestResult = new ArrayList<Object>();
    boolean dispatched;

    Request(RequestType requestType, ArrayList<Object> requestArguments) {
        this.requestType = requestType;
        this.requestArguments = requestArguments;

        // timestamp is NOW
        this.timestamp = new Timestamp(new java.util.Date().getTime());
    }

    /**
     * Writes a Request object to a DataOutputStream so that it can later be read by a @Request constructor.
     * @param out
     */
    void writeToStream(ObjectOutputStream out) {
        try {
            out.writeInt(uid);
            out.writeObject(requestArguments);
            out.writeUTF(timestamp.toString());
            out.writeObject(requestResult);
            out.writeObject(requestType);
            out.writeObject(dispatched);

        } catch (IOException e) {
            System.err.println("Error writing a request to a stream!");
        }
    }

    /**
     * Constructs a Request object from a DataInputStream (reads it)
     * @param in
     */
    Request(ObjectInputStream in) {

        try {
            this.uid = in.readInt();
            this.requestArguments = (ArrayList<Object>) in.readObject();
            String timeStampStr = in.readUTF();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            java.util.Date date = sdf.parse(timeStampStr);
            this.timestamp = new Timestamp(date.getTime());

            requestResult = (ArrayList<Object>) in.readObject();
            requestType = (RequestType) in.readObject();
            dispatched = (Boolean)in.readObject();

        } catch (IOException e) {
            System.err.println("Error reading a request from a stream!");
        } catch (ParseException e) {
            System.err.println("Error reading a request from a stream (parse problem)!");
        } catch (ClassNotFoundException e) {
            System.err.println("Erro reading requestArguments object or requestResult!");
        }

    }
}
