package actions.controller;

import model.data.Idea;

public class ViewHallOfFameAction extends ClientAction{
    private Idea[] hallOfFameIdeas;

    public Idea[] getHallOfFameIdeas() {
        return hallOfFameIdeas;
    }

    public void setHallOfFameIdeas(Idea[] hallOfFameIdeas) {
        this.hallOfFameIdeas = hallOfFameIdeas;
    }

    public String execute() throws Exception{
        super.execute();

        hallOfFameIdeas = client.doGetHallOfFameIdeas();

        if (hallOfFameIdeas == null)
            return ERROR;
        else
            return SUCCESS;
    }
}
