import java.io.DataInputStream;
////
// Specialized class which handles reading a Topic from an input stream, thus receiving it.
//
public class ClientTopic extends Topic {
    private ClientTopic(int id, String title, String body) {
        super(id, title, body);
    }
    private ClientTopic() {
        super();
    }

    static public ClientTopic fromDataStream(DataInputStream in) {
        ClientTopic ret = new ClientTopic();
        if ( (ret.id = Common.recvInt(in)) == -1)
            return null;
        if ( (ret.title = Common.recvString(in)) == null)
            return null;
        if ( (ret.body = Common.recvString(in)) == null)
            return null;
        return ret;
    }
}
