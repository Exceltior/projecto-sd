import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Timestamp;
public class Notification implements Serializable, TimestampClass {
    private final int uidbuyer;
    private final int uidSeller;
    private final int amount;
    private final int pricePerShare;
    private final int iid;

    // These are shared only because they are useful
    private final String  usernameBuyer;
    private final String usernameSeller;

    private Timestamp timestamp;

    Notification(int uidBuyer, int uidSeller, int amount, int pricePerShare, String usernameBuyer,
                 String usernameSeller, int iid) {
        this.uidbuyer = uidBuyer;
        this.uidSeller = uidSeller;
        this.amount = amount;
        this.pricePerShare = pricePerShare;
        this.usernameBuyer = usernameBuyer;
        this.usernameSeller = usernameSeller;
        this.iid = iid;

        // timestamp is NOW
        this.setTimestamp(new Timestamp(new java.util.Date().getTime()));
    }

    boolean writeToStream(ObjectOutputStream out) {
        try {
            out.writeObject(this);

        } catch (IOException e) {
            System.err.println("Error writing a request to a stream!");
            return false;
        }
        return true;
    }

    public String toString() {
        return usernameBuyer + "(UID "+uidbuyer+") bought "+amount+" shares, at "+pricePerShare+" DEICoins per share " +
                "from "+usernameSeller + "(UID "+uidSeller+") for idea "+iid;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    private void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
