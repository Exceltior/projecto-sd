import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;

/**
 * This class represents a file to be transmitted through the network.
 * FIXME: Got it from http://stackoverflow.com/questions/6058003/elegant-way-to-read-file-into-byte-array-in-java
 */
public class NetworkingFile implements Serializable {
    private byte[] data;
    private static final long serialVersionUID = 1L;

    public NetworkingFile(String path) throws FileNotFoundException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(path, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            data = new byte[length];
            f.readFully(data);
        } catch (IOException e) {
            System.err.println("IO Exception while reading file!");
        } finally {
            try {
                f.close();
            } catch (IOException e) {
                System.err.println("IO Exception while closing file!");
            }
        }
    }

    public boolean writeTo(String path) throws FileNotFoundException {
        RandomAccessFile f = new RandomAccessFile(path, "w");

        if ( this.data == null )
            return false;

        try {
            f.write(data);
        } catch (IOException e) {
            System.err.println("IO Exception while writing file!");
            return false; //Note that the finally block STILL gets executed in spite of this return. Java uglyness,
                          // so that we can say we used it at least once in our lovely project. FIXME
        } finally {
            try {
                f.close();
            } catch (IOException e) {
                System.err.println("Woohoo, more exceptions!"); //FIXME
            }
        }

        return true;
    }
}
