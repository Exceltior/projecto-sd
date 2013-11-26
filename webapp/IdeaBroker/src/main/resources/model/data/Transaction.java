package model.data;

import model.data.queues.TimestampClass;

import java.io.Serializable;
import java.sql.Timestamp;

public class Transaction implements Serializable, TimestampClass {
    public  int       uid;
    public  int       iid;
    public  int       numTargetShares;
    public  int       targetPrice;
    private Timestamp timestamp;
    private static final long serialVersionUID = 1L;

    public Transaction(int uid, int iid, int numTargetShares, int targetPrice) {
        this.uid = uid;
        this.iid = iid;
        this.numTargetShares = numTargetShares;
        this.targetPrice = targetPrice;
        this.setTimestamp(new Timestamp(new java.util.Date().getTime()));
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}