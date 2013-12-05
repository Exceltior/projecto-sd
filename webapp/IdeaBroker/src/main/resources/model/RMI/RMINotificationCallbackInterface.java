package model.RMI;

import java.rmi.Remote;

/**
 * Created with IntelliJ IDEA. User: jorl17 Date: 05/12/13 Time: 00:45 To change this template use File | Settings |
 * File Templates.
 */
public interface RMINotificationCallbackInterface extends Remote {
    public void notify(String msg) throws java.rmi.RemoteException;
}
