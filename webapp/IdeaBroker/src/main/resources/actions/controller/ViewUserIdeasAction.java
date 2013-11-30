package actions.controller;

import model.data.Idea;

/**
 * Lists all the ideas owned by the user. In struts.xml, we redirect the user to a jsp which expects this.
 */
public class ViewUserIdeasAction extends ClientAction{
    private Idea[] ideasList;
    private boolean userIdeas = true;

    /**
     * Gets the ideas owned by the user.
     * @return  An array of Idea objects, containing all the ideas owned by the user.
     */
    public Idea[] getIdeasList(){
        return this.ideasList;
    }

    /**
     * Gets a boolean value, telling us if the ideas we are going to see are owned (totally or partially) by the user
     * @return  A boolean value, telling us if the ideas we are going to see are owned (totally or partially) by the user
     */
    public boolean getUserIdeas(){
        return this.userIdeas;
    }

    /**
     * Setter for the attribute userIdeas.
     * @param type  The value we want to assign to userIdeas
     */
    public void setUserIdeas(boolean type){
        this.userIdeas = type;
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
