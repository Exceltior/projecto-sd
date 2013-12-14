package actions.controller.ajax;

import model.data.BuySharesReturn;

public class BuySharesAction extends AJAXAction {

    /**
     * IN
     */
    private int     iid;
    private float   maxPricePerShare;
    private int     buyNumShares;
    private boolean addToQueueOnFailure;
    private float   targetSellPrice;


    /**
     * OUT
     */
    private String result;
    private int    numSharesBought;
    private int    numSharesFinal;
    private float    totalSpent;

    public void doAjaxWork(){
        System.out.println("BuySharesAction called!");
        BuySharesReturn ret = client.doBuyShares(iid,maxPricePerShare,buyNumShares,addToQueueOnFailure,
                                                 targetSellPrice);
        System.out.println("BuySharesAction got ret!");
        System.out.println(ret);
        ajaxSuccess();
        result = ret.result;
        numSharesBought=ret.numSharesBought;
        numSharesFinal = ret.numSharesFinal;
        totalSpent = ret.totalSpent;
       System.out.println("BuySharesAction leaving!");
    }
    public boolean isSuccess() {
        return super.isSuccess();
    }

    public void setIid(int iid) {
        this.iid = iid;
    }

    public void setMaxPricePerShare(float maxPricePerShare) {
        this.maxPricePerShare = maxPricePerShare;
    }

    public void setBuyNumShares(int buyNumShares) {
        this.buyNumShares = buyNumShares;
    }

    public void setAddToQueueOnFailure(boolean addToQueueOnFailure) {
        this.addToQueueOnFailure = addToQueueOnFailure;
    }

    public String getResult() {
        return result;
    }

    public int getNumSharesBought() {
        return numSharesBought;
    }

    public float getTotalSpent() {
        return totalSpent;
    }

    public int getNumSharesFinal() {
        return numSharesFinal;
    }

    public void setTargetSellPrice(float targetSellPrice) {
        this.targetSellPrice = targetSellPrice;
    }
}
