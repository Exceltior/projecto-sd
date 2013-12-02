package actions.controller;

public class BuySharesAction extends ClientAction {
    private int ideaId;
    private int targetNumberShares;
    private float targetPrice;

    public String execute() throws Exception {
        super.execute();

        return SUCCESS;
    }

    public int getIdeaId() {
        return ideaId;
    }

    public void setIdeaId(int ideaId) {
        this.ideaId = ideaId;
    }

    public int getTargetNumberShares() {
        return targetNumberShares;
    }

    public void setTargetNumberShares(int targetNumberShares) {
        this.targetNumberShares = targetNumberShares;
    }

    public float getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(float targetPrice) {
        this.targetPrice = targetPrice;
    }
}
