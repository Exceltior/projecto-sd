import java.io.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class TransactionQueue extends OrderedTimestampQueue<Transaction> {
    private RMI_Interface RMI;

    TransactionQueue(RMI_Interface RMI) {
        this.RMI = RMI;
    }
    public void readFile() {
        String path = "transactions.bin";
            ObjectInputStream in;
            try {
                in = new ObjectInputStream(new FileInputStream(path));
            } catch (IOException e) {
                //System.err.println("Error opening Queue file for reading!");
                return;
            }

            int size = 0;
            try {
                size = in.readInt();
            } catch (IOException e) {
                System.err.println("Error reading size from transactionsFile!");
                return;
            }
            for (int i = 0; i < size; i++)
                try {
                    queue.add((Transaction)in.readObject());
                } catch (IOException e) {
                    System.err.println("Error reading from transactions File!");
                    return;
                } catch (ClassNotFoundException e) {
                    System.err.println("Error reading from transactions File! (Class not found)");
                    return;
                }
    }

    public boolean writeFile() {
            String path = "transactions.bin";
            ObjectOutputStream out;
            try {
                out = new ObjectOutputStream(new FileOutputStream(path));
            } catch (IOException e) {
                System.err.println("Error opening transactions file for writing!");
                return false;
            }

            try {
                out.writeInt(queue.size());
                for (Transaction r : queue)
                    out.writeObject(r);
            } catch (IOException e) {
                System.err.println("Error writing transactions to file!!");
                return false;
            }

            try {
                out.close();
            } catch (IOException e) {
                //FIXME: What damn exception can we get here?
            }

            return true;
    }

    synchronized void enqueue(Transaction t) {
        synchronized (queue) {
            super.enqueue(t);
        }
        checkQueue();
    }

    synchronized void dequeue(Transaction t) {
        synchronized (queue) {
            super.dequeue(t);
        }
    }

    void checkQueue() {
        synchronized (queue) {
            for (int i = 0; i < queue.size(); i++) {
                Transaction t = queue.get(0);

                // Execute transaction t
                // FIXME: Need an RMI function for this
                boolean result;
                try {
                    result = RMI.tryGetSharesIdea(t.uid, t.iid, t.numTargetShares, t.targetPrice, t.minTargetShares);
                } catch (RemoteException e) {
                    System.err.println("Should never happen!");
                    continue;
                }

                if ( result ) {
                    queue.remove(0);
                    i = 0; //Other shares might be changed by this
                    writeFile();
                }
            }
        }
    }
}
