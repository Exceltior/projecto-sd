package actions.controller;

/**
 * This class is used when the user wants to see his/hers history of transactions, stored in the database. When execute()
 * is called, there are no requirements in terms of "user-defined data". We just access the database and extract the user's
 * history of transactions.
 */
public class ShowTransactionHistoryAction extends ClientAction {

    /**
     * historyList is an Array of String objects, each containing the information about a transaction in which the user is
     * involved (as a buyer or a seller).
     */
    private String[] historyList;


    public String execute() throws Exception {
        super.execute();

        this.historyList = client.doGetHistory();
        if (this.historyList != null)
            return SUCCESS;
        else
            return ERROR;
    }

    /**
     * Gets the user's transaction history.
     * @return  An array of String objects, each containing the information about a transaction in which the user is involved
     *          (as a buyer or a seller).
     */
    public String[] getHistoryList() {
        return historyList;
    }

    /**
     * Sets the user's transaction history
     * @param historyList   An array of String objects, each containing the information about a transaction in which the
     *                      user is involved (as a buyer or a seller).
     */
    public void setHistoryList(String[] historyList) {
        this.historyList = historyList;
    }
}
