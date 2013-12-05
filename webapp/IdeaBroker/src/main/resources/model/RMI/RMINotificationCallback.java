package model.RMI;

import websockets.ClientWebsocketConnection;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created with IntelliJ IDEA. User: jorl17 Date: 05/12/13 Time: 00:48 To change this template use File | Settings |
 * File Templates.
 */
public class RMINotificationCallback extends UnicastRemoteObject implements RMINotificationCallbackInterface {
    private ClientWebsocketConnection websocket;

    protected RMINotificationCallback() throws RemoteException {
    }

    public RMINotificationCallback(ClientWebsocketConnection s) throws RemoteException {
        this.websocket = s;
    }

    @Override
    public void notify(String msg) throws RemoteException {
        websocket.notify(msg);
    }
}
