import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class NotificationConnection extends Thread {
    private static final int port = 1237;
    private Socket socket;
    private DataOutputStream outStream;
    private DataInputStream inStream;
    private ObjectInputStream inObjStream;
    private String user, password;

    NotificationConnection(String host, String user, String pwd) {
        //System.out.println("NotificationConnection!"); //FIXME
        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
            System.err.println("Excepção bodega"); //FIXME
            return;
        }
        this.user = user; this.password = pwd;
    }

    @Override
    public void run() {
        //System.out.println("Running!!"); //FIXME
        if ( this.socket == null )
            return;
        try {
            this.outStream = new DataOutputStream(socket.getOutputStream());
            this.inStream = new DataInputStream(socket.getInputStream());
            this.inObjStream = new ObjectInputStream(inStream);
        } catch (IOException e) {
            System.err.println("Error constructing a new Notification connection (did the connection die?");
            return;
        }

        //System.out.println("Aqui me estoy");

        if ( !Common.sendString(user, outStream) )
            return;

        if ( !Common.sendString(password, outStream) )
            return;

        Common.Message reply;

        if ( ( reply = Common.recvMessage(inStream)) == Common.Message.MSG_ERR) {
            System.err.println("This was not expected!!!"); //FIXME
            return;
        }

        for(;;) {
            //System.out.println("Waiting...!"); //FIXME
            try {
                Notification n = (Notification)inObjStream.readObject();

                System.out.println("Notification: "+n);
            } catch (IOException e) {
                return;
            } catch (ClassNotFoundException e) {
                System.err.println("This was not expected!!!!!!!!"); //FIXME
                return;
            }
        }
    }
}
