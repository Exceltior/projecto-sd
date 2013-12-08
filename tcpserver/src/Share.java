package model.data;

import java.io.Serializable;

public class Share  implements Comparable<Share>, Serializable {
    private int uid;
    private int iid;
    private float price;
    private int num;

     private static final long serialVersionUID = 1L;

    /**
     * Create shares from a SQL row
     * @param row
     */
    public Share(String[] row) {
        this.iid = Integer.valueOf(row[0]);
        this.uid = Integer.valueOf(row[1]);
        this.num = Integer.valueOf(row[2]);
        this.price = Float.valueOf(row[3]);
    }

    public double getPriceShareRatio() {
        if ( num == 0 )
            return 0;

        return price/(double)(num);
    }

    public int getNum() {
        return num;
    }

    public int getAvailableShares() {
        return getNum(); //FIXME: Change this
    }

    public float getPriceForNum(int n) {
        if ( n > getAvailableShares() )
            return 0; //Error: can't buy these many
        return price*n;
    }

    public int getUid() {
        return uid;
    }

    public int getIid() {
        return iid;
    }

    @Override
    public int compareTo(Share share) {
        /*if ( this.getPriceShareRatio() < share.getPriceShareRatio() )
            return -1;
        else if  ( this.getPriceShareRatio() == share.getPriceShareRatio() )
            return 0;
        else
            return 1;*/
        if ( this.price < share.price )
            return -1;
        else if ( this.price > share.price )
            return 1;
        else return 0;
    }

    public float getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Share{" +
                "uid=" + uid +
                ", iid=" + iid +
                ", price=" + price +
                ", num=" + num +
                '}';
    }
}
