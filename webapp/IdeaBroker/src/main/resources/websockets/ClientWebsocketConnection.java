package websockets;

import actions.model.Client;
import model.RMI.RMINotificationCallback;
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;
import org.json.JSONException;
import org.json.JSONObject;

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
            //getWsOutbound().writeTextMessage(CharBuffer.wrap("Hello!"));
            RMINotificationCallback callback = new RMINotificationCallback(this);
            client.getRMI().getRMIInterface().addCallbackToUid(client.getUid(), callback);
        } catch (IOException e) {
            System.out.println("onOpen exception!");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    protected void onClose(int status) {
    }

    public void notify(String username, String type, float currentMoney, float pricePerShare, int numShares, int iid,
                       int currentSharesIid, float currPricePerShare) {
        JSONObject obj = new JSONObject();


        try {
            obj.put("money", currentMoney);
            obj.put("iid", iid);
            obj.put("currentShares", currentSharesIid);
            obj.put("pricePerShare",pricePerShare);
            obj.put("numShares",numShares);
            obj.put("username",username);
            obj.put("type",type);
            obj.put("currPricePerShare", currPricePerShare);

        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            getWsOutbound().writeTextMessage(CharBuffer.wrap(obj.toString()));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    protected void onTextMessage(CharBuffer message) throws IOException {
    }

    protected void onBinaryMessage(ByteBuffer message) throws IOException {
        throw new UnsupportedOperationException("Binary messages not supported.");
    }
}