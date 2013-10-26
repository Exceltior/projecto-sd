import java.io.IOException;
import java.net.*;

public class UDPTransmitter extends Thread {
    private String targetServer;
    private int targetPort;
    private int period;

    public UDPTransmitter(String targetServer, int targetPort, int period) {
        this.targetServer = targetServer;
        this.targetPort = targetPort;
        this.period = period;
    }


    @Override
    public void run() {
        DatagramSocket clientSocket = null;
        InetAddress IPAddress = null;

        do {
            try {
                clientSocket = new DatagramSocket();
            } catch (SocketException e) {
                System.err.println("Error creating DatagramSocket!");
            }
            try {
                IPAddress = InetAddress.getByName(targetServer);
            } catch (UnknownHostException e) {
                System.err.println("Can't reach host to ping!!");
            }
        } while (clientSocket == null || IPAddress == null);


        for(;;) {

            String sentence = "ALIVE";
            byte[] sendData = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, targetPort);

            try {
                clientSocket.send(sendPacket);
            } catch (IOException e) {
                System.err.println("Error sending packet!");
            }

            try {
                Thread.sleep(period);
            } catch (InterruptedException ignored) {}
        }
    }
}
