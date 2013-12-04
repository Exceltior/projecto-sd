package actions.controller;

import model.data.Idea;

/**
 * This action isused when the user wants to search an idea in the database. When execute() is called title and id dont
 * have to be filled by the user, since we offer a search by idea's id; search by idea's title; or search by idea's id
 * and title. The type of search to be made depends on what was filled by the user, before pressing the submit button.
 */
public class SearchIdeaAction extends ClientAction{
    private String title;
    private int id;
    private Idea[] ideasList;

    /**
     * Gets the title of the idea the user wants to search.
     * @return  The title of the idea the user wants to search.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Defines a new value for the title of the idea to search.
     * @param title The new value for the title of the idea to search.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the id of the idea the user wants to search.
     * @return  The id of the idea the user wants to search.
     */
    public int getId() {
        return id;
    }

    /**
     * Defines a new value for the id of the idea to search.
     * @param id    The new value for the id of the idea to search.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets a list of the ideas found, based on the type of search made and the user's input data.
     * @return  An array of Idea objects, containing the list of the ideas found, based on the type of search made and
     *          the user's input data.
     */
    public Idea[] getIdeasList() {
        return ideasList;
    }

    /**
     * Defines a new set of ideas to assign to the attribute ideasList.
     * @param ideas The new set of ideas to assign to the attribute ideasList.
     */
    public void setIdeasList(Idea[] ideas) {
        this.ideasList = ideas;
    }

    /**
     * Try to find in the database all the ideas with user's defined id and title.
     * @return              A String object, containing the result of the operation (success/failure).
     * @throws Exception    Throws an exception, in case of an error occurrs when accessing to the database
     */
    public String execute() throws Exception{
        super.execute();

        this.ideasList = client.doSearchIdea(id, title);

        if (this.ideasList != null && this.ideasList.length>0)
            return SUCCESS;
        else
            return ERROR;
    }
}
