import java.io.Serializable;
import java.sql.Timestamp;

public class Transaction implements Serializable, TimestampClass {
    final int uid;
    final int iid;
    final int numTargetShares;
    final int targetPrice;
    final int minTargetShares;
    private Timestamp timestamp;
    private static final long serialVersionUID = 1L;

    Transaction(int uid, int iid, int numTargetShares, int targetPrice, int minTargetShares) {
        this.uid = uid; this.iid = iid; this.numTargetShares = numTargetShares;
        this.targetPrice = targetPrice; this.minTargetShares = minTargetShares;
        this.setTimestamp(new Timestamp(new java.util.Date().getTime()));
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    private void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
