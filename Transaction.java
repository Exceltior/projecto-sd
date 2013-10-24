import java.io.Serializable;
import java.sql.Timestamp;

public class Transaction implements Serializable, TimestampClass {
    int uid, iid, numTargetShares, targetPrice, minTargetShares;
    private Timestamp timestamp;
    Transaction(int uid, int iid, int numTargetShares, int targetPrice, int minTargetShares) {
        this.uid = uid; this.iid = iid; this.numTargetShares = numTargetShares;
        this.targetPrice = targetPrice; this.minTargetShares = minTargetShares;
        this.setTimestamp(new Timestamp(new java.util.Date().getTime()));
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
