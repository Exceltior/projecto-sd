package actions.controller;

import model.data.TransactionHistoryEntry;

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
    private TransactionHistoryEntry[] historyList;


    public String doWork(){

        this.historyList = client.doGetHistory();
        if ( this.historyList != null )
            return SUCCESS;
        else
            return ERROR;
    }

    /**
     * Gets the user's transaction history.
     * @return An array of String objects, each containing the information about a transaction in which the user is involved
     *          (as a buyer or a seller).
     */
    public TransactionHistoryEntry[] getHistoryList() {
        return historyList;
    }
}
