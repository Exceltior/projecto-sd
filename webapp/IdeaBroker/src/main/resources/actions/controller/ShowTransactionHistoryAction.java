package actions.controller;

public class ShowTransactionHistoryAction extends ClientAction {
    private String[] historyList;


    public String execute() throws Exception {
        super.execute();

        this.historyList = client.doGetHistory();
        if (this.historyList != null)
            return SUCCESS;
        else
            return ERROR;
    }

    public String[] getHistoryList() {
        return historyList;
    }

    public void setHistoryList(String[] historyList) {
        this.historyList = historyList;
    }
}
