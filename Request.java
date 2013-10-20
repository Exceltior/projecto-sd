import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class Request implements Serializable {
    int uid;
    String query;
    // FIXME: We might possibly have to add the queryType in another variable (to know which type of function to use...
    Timestamp timestamp;

    // We need to store the queryResult here for various reasons. Most notably, because after the dispatcher thread is
    // done working with a Request, the original thread must have access to the output...
    ArrayList<String[]> queryResult = new ArrayList<String[]>();
    boolean dispatched;

    /**
     * Writes a Request object to a DataOutputStream so that it can later be read by a @Request constructor.
     * @param out
     */
    void writeToStream(DataOutputStream out) {
        try {
            out.writeInt(uid);
            out.writeUTF(query);
            out.writeUTF(timestamp.toString());
            out.writeInt(queryResult.size());
            for ( String[] row : queryResult) {
                out.writeInt(row.length);
                for (String cell : row)
                    out.writeUTF(cell);
            }

        } catch (IOException e) {
            System.err.println("Error writing a request to a stream!");
        }
    }

    /**
     * Constructs a Request object from a DataInputStream (reads it)
     * @param in
     */
    Request(DataInputStream in) {
        this.dispatched = false;

        try {
            this.uid = in.readInt();
            this.query = in.readUTF();
            String timeStampStr = in.readUTF();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            java.util.Date date = sdf.parse(timeStampStr);
            this.timestamp = new Timestamp(date.getTime());

            int queryResultSize = in.readInt();
            for ( int i = 0; i < queryResultSize; i++) {
                int rowSize = in.readInt();
                String[] row = new String[rowSize];
                for ( int j = 0 ; j < rowSize; j++)
                    row[i] = in.readUTF();

                queryResult.add(row);
            }

        } catch (IOException e) {
            System.err.println("Error reading a request from a stream!");
        } catch (ParseException e) {
            System.err.println("Error reading a request from a stream (parse problem)!");
        }

    }
}
