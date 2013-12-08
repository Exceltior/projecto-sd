package actions.controller.ajax;

public class TakeoverAction extends AJAXAction {

    /**
     * The id of the idea we want to add to the user's watchlist.
     */
    private int     iid;

    /**
     * Try to add the selected idea to the user's watchlist.
     * @return              If the removal was successfull returns SUCCESS. Otherwise returns ERROR.
     * @throws Exception
     */
    public void doAjaxWork() {
        //System.out.println(" TakeoverAction: " + iid);
        setAjaxStatus(client.doTakeover(iid));
        //System.out.println(" done...");
    }

    public boolean isSuccess() {
        return super.isSuccess();
    }

    /**
     * Setter for the class' attribute id, containing the id of the idea we want to add to the user's watchlist.
     * @param iid   The value we want to assign to the class' attribute iid.
     */
    public void setIid(int iid) {
        this.iid = iid;
    }
}
