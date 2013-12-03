package actions.controller;

public class RemoveIdeaAction extends AJAXAction{

    /**
     * IN
     */
    private int iid;


    /**
     * OUT
     */
    private String result; // "OK" or "NOT_OWNER"

    public void setIid(int temp) {
        this.iid = temp;
    }

    public String execute() throws Exception {
        super.execute();
        int retval;

        setAjaxStatus((retval = client.doRemoveIdea(iid)) > 0);
        if ( retval == -2 )
            result = "NOT_OWNER";
        else
            result = "OK";

        return SUCCESS;
    }
    public boolean isSuccess() {
        return super.isSuccess();
    }

    public String getResult() {
        return result;
    }
}
