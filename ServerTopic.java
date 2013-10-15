import java.io.DataOutputStream;

////
// Specialized class which handles writing a Topic through an outputstream, thus sending it.
// FIXME: Since this is the servertopic, it might be useful to store the id of the creator in here
//
public class ServerTopic extends Topic {
    public ServerTopic() {
    }

    public ServerTopic(int id, String title, String body) {
        super(id, title, body);
    }
    public boolean writeToDataStream(DataOutputStream out) {
        if ( ! Common.sendInt(id, out) )
            return false;
        if ( ! Common.sendString(title, out) )
            return false;
        if ( ! Common.sendString(body, out) )
            return false;

        return true;
    }

    ////
    // Create a ServerTopic from a SQL Line (array of strings)
    //
    public ServerTopic(String[] line) {
        this.id = Integer.valueOf(line[0]);
        this.title = line[1];
        this.body = line[2];
    }
}
