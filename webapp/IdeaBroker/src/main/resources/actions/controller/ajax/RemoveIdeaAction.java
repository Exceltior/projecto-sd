package actions.controller.ajax;


public class RemoveIdeaAction extends AJAXAction {

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

    public void doAjaxWork(){
        int retval;
        System.out.println("Remove idea "+iid);
        setAjaxStatus(client.doRemoveIdea(iid) !=-1);
        /*if ( retval == -2 )
            result = "NOT_OWNER";
        else*/
            result = "OK";

        System.out.println("result "+result);
    }
    public boolean isSuccess() {
        return super.isSuccess();
    }

    public String getResult() {
        return result;
    }
}
