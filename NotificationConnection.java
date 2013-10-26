import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

class NotificationConnection extends Thread {
    private Socket socket;
    private DataOutputStream outStream;
    private DataInputStream inStream;
    private ObjectInputStream inObjStream;
    private String user, password;

    NotificationConnection(String host, String user, String pwd, int port) {
        try { socket = new Socket(host, port); } catch (IOException ignored) {}
        this.user = user; this.password = pwd;
    }

    @Override
    public void run() {
        if ( this.socket == null )
            return;
        try {
            this.outStream = new DataOutputStream(socket.getOutputStream());
            this.inStream = new DataInputStream(socket.getInputStream());
            this.inObjStream = new ObjectInputStream(inStream);
        } catch (IOException e) {
            System.err.println("Error constructing a new Notification connection (did the connection die)?");
            return;
        }

        //System.out.println("Aqui me estoy");

        if ( !Common.sendString(user, outStream) )
            return;

        if ( !Common.sendString(password, outStream) )
            return;

        Common.Message reply;

        if ( ( reply = Common.recvMessage(inStream)) == Common.Message.MSG_ERR) {
            System.exit(-1);
            return;
        }

        for(;;) {
            try {
                Notification n = (Notification)inObjStream.readObject();

                System.out.println("\n[Notification]: "+n);
            } catch (IOException ignored) { return; }
              catch (ClassNotFoundException ignored) { return; }
        }
    }
}
