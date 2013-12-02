package actions.controller;

public class RemoveIdeaAction extends ClientAction{
    private int iid;

    public void setIid(int temp){
        this.iid = temp;
    }
    public int getIid(){
        return this.iid;
    }

    public String execute() throws Exception{
        super.execute();

        if (client.doRemoveIdea(iid) > 0)
            return SUCCESS;
        else
            return ERROR;
    }
}
