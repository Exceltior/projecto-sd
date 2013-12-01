package actions.controller;


/**
 * AJAX action. This action is mapped in struts.xml to produce JSON. The JSON parser will iterate through the action
 * and append all getters to the JSON output. In particular, the .JSP is expecting a success boolean value which
 * indicates the success or failure of the operation. Note that if we removed the getter (isSuccess()),
 * our .jsp would have problems.
 * FIXME: This documentation has to be improved
 */
public class AJAXAction extends ClientAction{
    private boolean success;

    public String execute() throws Exception {
        super.execute();
        return SUCCESS;
    }

    public boolean isSuccess() {
        return success;
    }

    protected void ajaxSuccess() { this.success = true; }
    protected void ajaxFailure() { this.success = false; }
    protected void setAjaxStatus(boolean s) { this.success = s; }
}
