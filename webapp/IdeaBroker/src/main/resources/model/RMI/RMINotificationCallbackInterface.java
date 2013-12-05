package model.RMI;

import java.rmi.Remote;

/**
 * Created with IntelliJ IDEA. User: jorl17 Date: 05/12/13 Time: 00:45 To change this template use File | Settings |
 * File Templates.
 */
public interface RMINotificationCallbackInterface extends Remote {
    public void notify(String username, String type, float currentMoney, float pricePerShare, int numShares, int iid,
                       int currentSharesIid, float currPricePerShare) throws java.rmi.RemoteException;

    public void notifyNewMarketValue(int iid, float value) throws java.rmi.RemoteException;
}
