package websockets;

import actions.model.Client;
import model.RMI.RMINotificationCallback;
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public class ClientWebsocketConnection extends MessageInbound implements Serializable {
    private static final String COOKIE_NAME = "IdeaBrokerEncodedUid";
    private Client client = new Client();

    public ClientWebsocketConnection(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for ( Cookie c : cookies ) {
            if ( c.getName().equals(COOKIE_NAME) )
                if ( !client.loginWithEncodedUid(c.getValue()) ) {
                    /* FIXME: Should not happen...ever...*/
                }
        }
    }

    protected void onOpen(WsOutbound outbound) {
        try {
            getWsOutbound().writeTextMessage(CharBuffer.wrap("Hello!"));
        } catch (IOException e) {
            System.out.println("onOpen exception!");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    protected void onClose(int status) {
    }
    }

    protected void onBinaryMessage(ByteBuffer message) throws IOException {
        throw new UnsupportedOperationException("Binary messages not supported.");
    }
}