import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Timestamp;
public class Notification implements Serializable {
    int uidbuyer, uidSeller, amount, pricePerShare;

    // These are shared only because they are useful
    String  usernameBuyer, usernameSeller;

    Timestamp timestamp;

    Notification(int uidBuyer, int uidSeller, int amount, int pricePerShare, String usernameBuyer, String usernameSeller) {
        this.uidbuyer = uidBuyer;
        this.uidSeller =uidSeller;
        this.amount = amount;
        this.pricePerShare = pricePerShare;
        this.usernameBuyer = usernameBuyer;
        this.usernameSeller = usernameSeller;

        // timestamp is NOW
        this.timestamp = new Timestamp(new java.util.Date().getTime());
    }

    void writeToStream(ObjectOutputStream out) {
        try {
            out.writeObject(this);

        } catch (IOException e) {
            System.err.println("Error writing a request to a stream!");
        }
    }

}
