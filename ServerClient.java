import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.sql.SQLException;

////
// This class, which implements an independent thread, is responsible for handling all requests from a given client
// (given to us referenced by its socket)
//
public class ServerClient implements Runnable {
    private Socket socket = null;
    private DataOutputStream outStream = null;
    private DataInputStream inStream = null;

    public ServerClient(Socket currentSocket) {
        this.socket = currentSocket;
        try {
            this.outStream = new DataOutputStream(currentSocket.getOutputStream());
            this.inStream = new DataInputStream(currentSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    @Override
    public void run() {

        ServerTopic topic = new ServerTopic(5, "Hello, Ladies", "Hakuna Matata");

        for(;;) {
            Common.Messages msg;
            int intMsg;

            // Read the next Message/Request
            if ( (intMsg = Common.readIntFromStream(inStream)) == -1) {
                break; //Connection dead!
            }
            msg = Common.Messages.values()[intMsg];


            // Handle the request
            if ( msg == Common.Messages.MSG_LOGIN)
                if ( !handleLogin() )
                    break ;

        }
    }

    ////
    //  Only returns false if we lost connection
    ////
    private boolean handleLogin() {
        String user, pwd;
        boolean log;

        if ( (user = Common.readStringFromStream(inStream)) == null)
            return false;
        if ( (pwd = Common.readStringFromStream(inStream)) == null)
            return false;

        // Do actual login handling code here

        try {
            //Start RMIRegistry programmatically
            RMI_Interface rmi_i = (RMI_Interface) LocateRegistry.getRegistry(7000).lookup("academica");

            //Login
            log = rmi_i.Login(user, pwd);

            if (!log){
                if ( !Common.sendIntThroughStream(Common.Messages.MSG_ERR.ordinal(),outStream) )
                    return false;
            }
            else{
                if ( !Common.sendIntThroughStream(Common.Messages.MSG_OK.ordinal(),outStream) )
                    return false;
            }

        } catch (RemoteException e) {
           System.out.println("Remote Exception no ServerClient!");
        } catch (NotBoundException n) {
            System.out.println("NotBoundException no ServerClient!");
        } catch (SQLException s){
           System.out.println("SQLException no ServerClient!");
        }

        return false;
    }
}
