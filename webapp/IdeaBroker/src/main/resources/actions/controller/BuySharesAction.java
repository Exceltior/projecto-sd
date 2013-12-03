package actions.controller;

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
    private int    totalSpent;

    public String execute() throws Exception {
        super.execute();

        ajaxSuccess();
        result = "OK";
        numSharesFinal = 10000;

        return SUCCESS;
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

    public int getTotalSpent() {
        return totalSpent;
    }

    public int getNumSharesFinal() {
        return numSharesFinal;
    }

    public void setTargetSellPrice(float targetSellPrice) {
        this.targetSellPrice = targetSellPrice;
    }
}
