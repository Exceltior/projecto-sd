package model.data.queues;

import model.RMI.RMI_Interface;
import model.data.Transaction;
import rmiserver.RMI_Server;

import java.io.*;
import java.rmi.RemoteException;

public class TransactionQueue extends OrderedTimestampQueue<Transaction> {
    private RMI_Interface RMI;

    public TransactionQueue(RMI_Server RMI) {
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

            int size;
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
                //System.err.println("Error opening transactions file for writing!");
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

            try { out.close(); } catch (IOException ignored) {}

            return true;
    }

    public synchronized void enqueue(Transaction t) {
        synchronized (queue) {
            super.enqueue(t);
        }
        checkQueue();
    }

    public synchronized void dequeue(Transaction t) {
        synchronized (queue) {
            super.dequeue(t);
        }
    }

    public void checkQueue() {
        synchronized (queue) {
            for (int i = 0; i < queue.size(); i++) {
                Transaction t = queue.get(0);

                // Execute transaction t
                // An RMI function to do this might have been better, but right now I just want to push the changes
                // out.
                boolean result;
                try {
                    result = RMI.tryGetSharesIdea(t.uid, t.iid, t.numTargetShares, t.targetPrice);
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
