package actions.controller;

public class RemoveIdeaAction extends AJAXAction{
    private int iid;

    public void setIid(int temp){
        this.iid = temp;
    }

    public String execute() throws Exception {
        super.execute();

        setAjaxStatus(client.doRemoveIdea(iid) > 0);

        return SUCCESS;
    }
    public boolean isSuccess() {
        return super.isSuccess();
    }
}
