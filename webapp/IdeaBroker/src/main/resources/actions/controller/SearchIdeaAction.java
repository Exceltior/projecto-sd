package actions.controller;

import model.data.Idea;

public class SearchIdeaAction extends ClientAction{
    private String title;
    private int id;
    private Idea[] ideasList;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Idea[] getIdeasList() {
        return ideasList;
    }

    public void setIdeasList(Idea[] ideas) {
        this.ideasList = ideas;
    }

    public String execute() throws Exception{
        super.execute();

        this.ideasList = client.doSearchIdea(id, title);

        if (this.ideasList != null && this.ideasList.length>0)
            return SUCCESS;
        else
            return ERROR;
    }
}
