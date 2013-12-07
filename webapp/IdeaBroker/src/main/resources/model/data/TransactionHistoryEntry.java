package model.data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA. User: jorl17 Date: 07/12/13 Time: 17:46 To change this template use File | Settings |
 * File Templates.
 */
public class TransactionHistoryEntry implements Serializable {
    private String buyer, seller;
    private int    numShares;
    private float  pricePerShare;
    private float  total;
    private String title;
    private String date;

    public TransactionHistoryEntry(String[] entry) {
        this.buyer = entry[0];
        this.seller = entry[1];
        this.pricePerShare = Float.valueOf(entry[2]);
        this.numShares = Integer.valueOf(entry[3]);
        this.title = entry[4];
        this.date = entry[5];
        this.total = this.pricePerShare * this.numShares;
    }

    public int getNumShares() {
        return numShares;
    }

    public float getPricePerShare() {
        return pricePerShare;
    }

    public float getTotal() {
        return total;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }
}
