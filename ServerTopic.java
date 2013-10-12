import java.io.DataOutputStream;

////
// Specialized class which handles writing a Topic through an outputstream, thus sending it.
//
public class ServerTopic extends Topic {
    public ServerTopic() {
    }

    public ServerTopic(int id, String title, String body) {
        super(id, title, body);
    }
    public boolean writeToDataStream(DataOutputStream out) {
        if ( ! Common.sendIntThroughStream(id, out) )
            return false;
        if ( ! Common.sendStringThroughStream(title, out) )
            return false;
        if ( ! Common.sendStringThroughStream(body, out) )
            return false;

        return true;
    }
}
