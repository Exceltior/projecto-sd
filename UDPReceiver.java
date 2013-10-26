import java.io.IOException;
import java.net.*;

public class UDPReceiver extends Thread{

    private int targetPort;
    private Server server;
    private int checkPeriod;

    public UDPReceiver(int targetPort, Server server, int checkPeriod) {
        this.targetPort = targetPort;
        this.server = server;
        this.checkPeriod = checkPeriod;
    }

    @Override
    public void run() {
        DatagramSocket serverSocket = null;
        boolean currentlyDead = false;

        do {
            try {
                serverSocket = new DatagramSocket(targetPort);
                serverSocket.setSoTimeout(checkPeriod);
            } catch (SocketException e) {
                System.err.println("Error creating DatagramSocket!");
            }
        } while (serverSocket == null);

        for(;;) {

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                serverSocket.receive(receivePacket);
            } catch (SocketTimeoutException e) {
                //System.out.println("Timeout! Trying again to make sure it's not transient...");

                // Try to get the package one more time (retry)
                try {
                    serverSocket.receive(receivePacket);
                } catch (SocketTimeoutException e2) {
                    //System.out.println("Timeout!");
                    //Lost connection to client!
                    if ( !currentlyDead ) {
                        server.notifyConnectionToOtherServerDead();
                        currentlyDead = true;
                    }
                    continue;
                } catch (IOException e2) {
                    System.err.println("Error receiving packet!");
                    continue;
                }

                System.out.println("This time we got connection. Assuming it was a transient fault...");

            } catch (IOException e) {
                System.err.println("Error receiving packet!");
                continue;
            }

            if ( currentlyDead ) {
                server.notifyConnectionToOtherServerBack();
                currentlyDead = false;
            }
        }
    }

}
