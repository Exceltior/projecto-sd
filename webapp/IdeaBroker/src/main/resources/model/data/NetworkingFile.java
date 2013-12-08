package model.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;

/**
 * This class represents a file to be transmitted through the network.
 */
public class NetworkingFile implements Serializable {
    private byte[] data;
    private static final long serialVersionUID = 1L;
    private String name;

    public NetworkingFile(String path) throws FileNotFoundException {
        name = path.substring(path.lastIndexOf('/')+1); //Get the file name (note that if there's no /,
        // we get the whole string
        RandomAccessFile f = new RandomAccessFile(path, "r");
        try {
            long llength = f.length();
            int length = (int) llength;
            if (length != llength)
                throw new IOException("File size >=2 GB");
            // Read file and return data
            data = new byte[length];
            f.readFully(data);
        } catch (IOException e) {
            //System.err.println("IO Exception while reading file!");
        } finally {
            try {
                f.close();
            } catch (IOException e) {
                //System.err.println("IO Exception while closing file!");
            }
        }
    }


    public NetworkingFile(String path, String fileName) throws FileNotFoundException {
        this(path);
        name = fileName;
    }

    public boolean writeTo(String path) throws FileNotFoundException {
        RandomAccessFile f = new RandomAccessFile(path, "rw");

        if ( this.data == null )
            return false;

        try {
            f.write(data);
        } catch (IOException e) {
            //System.err.println("IO Exception while writing file!");
            return false; //Note that the finally block STILL gets executed in spite of this return.
        } finally {
            try { f.close(); } catch (IOException ignored) {}
        }

        return true;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String n){
        this.name = n;
    }
}
