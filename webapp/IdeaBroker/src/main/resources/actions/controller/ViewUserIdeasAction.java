package actions.controller;

import model.data.Idea;

/**
 * Lists all the ideas owned by the user. In struts.xml, we redirect the user to a jsp which expects this.
 */
public class ViewUserIdeasAction extends ClientAction{
    private Idea[] ideasList;

    public Idea[] getIdeasList(){
        return this.ideasList;
    }

    public void setIdeasList(Idea[] ideas_list){
        this.ideasList = ideas_list;
    }

    public String execute() throws Exception{
        super.execute();

        this.ideasList = client.doGetUserIdeas();

        System.out.println("O user tem " + ideasList.length + " ideias");

        return SUCCESS;
    }
}
