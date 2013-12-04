package actions.controller;

import model.data.Idea;

/**
 * Class whose responsability is to handle a user's watchlist when he/she requires the application to show the ideas stored
 * in his/hers watchlist.
 */
public class ViewUserWatchListAction extends ClientAction {
    private Idea[] ideasList;
    private boolean userIdeas = false;

    /**
     * Setter for attribute ideasList. This method allow us to define a new set of ideas to define as the user's watchlist.
     * @param ideasList1    A list of Idea objects, containing the set of ideas to define as the user's watchlist.
     */
    public void setIdeasList(Idea[] ideasList1){
        this.ideasList = ideasList1;
    }

    /**
     * Getter for attribute ideasList. This method allow us to get all the ideas the user is following.
     * @return  An array of Idea objects, containing all the ideas the user is following.
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
     * Action's execute method, called whenever the action is triggered.
     * @return              A String object, informing the success or failure of the operation
     * @throws Exception    Throws an exception, in case of an error occurrs when accessing to the database
     */
    public String execute() throws Exception{
        super.execute();

        this.ideasList = client.doGetUserWatchList();

        return SUCCESS;
    }
}
