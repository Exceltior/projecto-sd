package actions.controller;

/**
 * AJAX action. This action is mapped in struts.xml to produce JSON. The JSON parser will iterate through the action
 * and append all getters to the JSON output. In particular, the .JSP is expecting a success boolean value which
 * indicates the success or failure of the operation. Note that if we removed the getter (isSuccess()),
 * our .jsp would have problems.
 *
 * AJAX IN: iid: The idea iid where to remove the watchlist of the current user (user is determined by session)
 * AJAX OUT: JSON:
 *              success: Boolean indicating success or failure of the operation.
 */
public class RemoveFromWatchlistAction extends AJAXAction{
    private int     iid;

    public String execute() throws Exception {
        super.execute();
        System.out.println(" RemoveFromWatchlistAction: " + iid);
        setAjaxStatus(client.doRemoveFromWatchList(iid));
        return SUCCESS;
    }
    public boolean isSuccess() {
        return super.isSuccess();
    }

    public void setIid(int iid) {
        this.iid = iid;
    }
}
