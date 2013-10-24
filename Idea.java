import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

public class Idea implements Serializable {

    private static final long serialVersionUID = 1L;

    public int getId() {
        return id;
    }

    protected int id, uid;
    protected String body, title, file;

    ////
    // Class Constructor
    ////
    public Idea() {
        this.id = this.uid = 0;
        this.body = this.title = null;
        this.file = "N";
    }

    ////
    //  Method responsible for comunicating with the Server, writing information to the data Output Stream
    ////
    public boolean writeToDataStream(DataOutputStream out) {
        if ( ! Common.sendInt(id, out) )
            return false;
        if ( ! Common.sendInt(uid, out) )
            return false;
        if ( ! Common.sendString(title, out) )
            return false;
        if ( ! Common.sendString(body, out) )
            return false;
        if ( ! Common.sendString(file, out) )
            return false;

        return true;
    }

    ////
    //  Method responsible for receiving messages from the Server, reading information from the data Output Stream
    ////
    public boolean readFromDataStream(DataInputStream in) {

        if ( (this.id = Common.recvInt(in)) == -1)
            return false;
        if ( (this.uid = Common.recvInt(in)) == -1)
            return false;
        if ( (this.title = Common.recvString(in)) == null )
            return false;
        if ( (this.body = Common.recvString(in)) == null )
            return false;
        if ( (this.file = Common.recvString(in)) == null )
            return false;

        return true;
    }

    ////
    // Create an Idea from a SQL row (array of strings)
    //
    public Idea(String[] row) {
        /* FIXME: Implement this */
        this.id = Integer.valueOf(row[0]);
        this.title = row[1]; //title comes before the body
        this.body = row[2];
        this.uid = Integer.valueOf(row[3]);
        this.file = "N";
    }

    public void setFile(String f){
        this.file = f;
    }

    public String getFile(){
        return this.file;
    }

    public String toString(){
        return "Idea{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", userid = " + uid + '\'' +
                ", file = " + file +
                '}';
    }

}
