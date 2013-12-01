package actions.controller;

public class GoSetSharePriceAction extends ClientAction{

    private int iid;
    public static int ideaId;

    public void setIid(int iid1){
        this.iid = iid1;
    }

    public int getIid(){
        return this.iid;
    }

    public String execute() throws Exception{
        super.execute();

        ideaId = iid;//FIXME: I only do this to be able to pass this information from one class to the other

        return SUCCESS;
    }
}
