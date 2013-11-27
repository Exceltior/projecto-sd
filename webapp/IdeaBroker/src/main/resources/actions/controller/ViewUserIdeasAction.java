package actions.controller;

import model.data.Idea;

/**
 * Lists all the ideas owned by the user. In struts.xml, we redirect the user to a jsp which expects this.
 */
public class ViewUserIdeasAction extends ClientAction{
    private Idea[] ideasList;

    /**
     * Gets the ideas owned by the user.
     * @return  An array of Idea objects, containing all the ideas owned by the user.
     */
    public Idea[] getIdeasList(){
        return this.ideasList;
    }

    /**
     * Sets a new list of ideas for the user.
     * @param ideas_list    List of user's ideas
     */
    public void setIdeasList(Idea[] ideas_list){
        this.ideasList = ideas_list;
    }

    /**
     * Action's execute method, called whenever the action is triggered.
     * @return  A String object, informing the success or failure of the operation
     * @throws Exception
     */
    public String execute() throws Exception{
        super.execute();

        this.ideasList = client.doGetUserIdeas();

        //System.out.println("O user tem " + ideasList.length + " ideias");

        return SUCCESS;
    }
}
