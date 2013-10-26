import java.io.Serializable;

public class Share  implements Comparable<Share>, Serializable {
    private int uid;
    private int iid;
    private int price;
    private int numMin;
    private int num;

     private static final long serialVersionUID = 1L;

    /**
     * Create shares from a SQL row
     * @param row
     */
    public Share(String[] row) {
        /* FIXME: Implement this */
        this.iid = Integer.valueOf(row[0]);
        this.uid = Integer.valueOf(row[1]);
        this.num = Integer.valueOf(row[2]);
        this.price = Integer.valueOf(row[3]);
        this.numMin = Integer.valueOf(row[4]);
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
        return num-numMin;
    }

    public int getPriceForNum(int n) {
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

    public int getNumMin() {
        return numMin;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Share{" +
                "uid=" + uid +
                ", iid=" + iid +
                ", price=" + price +
                ", numMin=" + numMin +
                ", num=" + num +
                '}';
    }
}
