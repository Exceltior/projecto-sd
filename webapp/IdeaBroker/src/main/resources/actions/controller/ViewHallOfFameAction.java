package actions.controller;

import model.data.Idea;

public class ViewHallOfFameAction extends ClientAction {
    private Idea[] ideas;

    public Idea[] getIdeas() {
        return ideas;
    }

    public String doWork() {
        ideas = client.doGetHallOfFameIdeas();

        if ( ideas == null)
            return ERROR;
        else
            return SUCCESS;
    }
}
