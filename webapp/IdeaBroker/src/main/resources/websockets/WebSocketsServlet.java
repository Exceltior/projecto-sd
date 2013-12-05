package websockets;

import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.StreamInbound;

import javax.servlet.http.HttpServletRequest;

public class WebSocketsServlet extends WebSocketServlet {
    protected StreamInbound createWebSocketInbound(String subProtocol,
                                                   HttpServletRequest request) {
        return new ClientWebsocketConnection( request );
    }

}