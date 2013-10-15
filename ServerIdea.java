import java.io.DataOutputStream;

public class ServerIdea extends Idea {
    public ServerIdea(int id, int uid, String body, String title) {
        super(id, uid, body, title);
    }

    public ServerIdea() {

    }

    public boolean writeToDataStream(DataOutputStream out) {
        if ( ! Common.sendInt(id, out) )
            return false;
        if ( ! Common.sendInt(uid, out) )
            return false;
        if ( ! Common.sendString(title, out) )
            return false;
        if ( ! Common.sendString(body, out) )
            return false;

        return true;
    }

    ////
    // Create a ServerIdea from a SQL Line (array of strings)
    //
    public ServerIdea(String[] line) {
        /* FIXME: Implement this */
    }
}
