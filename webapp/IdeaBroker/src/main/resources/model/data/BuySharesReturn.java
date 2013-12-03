package model.data;

import java.io.Serializable;

/**
 * Data type used to return all the info from buyShares()
 */
public class BuySharesReturn implements Serializable {
    public String   result = "";
    public int      numSharesBought = 0;
    public int      numSharesFinal = 0;
    public float    totalSpent = 0.0f;

    @Override
    public String toString() {
        return "BuySharesReturn{" +
                "result='" + result + '\'' +
                ", numSharesBought=" + numSharesBought +
                ", numSharesFinal=" + numSharesFinal +
                ", totalSpent=" + totalSpent +
                '}';
    }
}
