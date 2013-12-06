package actions.controller;

import model.data.Idea;

/**
 * Given a topic ID, fetch its data (ideas, etc), store them in the current Action. In struts.xml,
 * we redirect the user to a jsp which expects this.
 */
public class ListIdeasAction extends ClientAction{
    private int    tid;
    private Idea[] ideas;
    private String title;

    public void setMode(String mode) {
        this.mode = mode;
    }

    private String mode;

    /**
     * Gets the id of the topic we are showing to the user.
     * @return The id of the topic we are showing to the user.
     */
    public int getTid() {
        return tid;
    }

    /**
     * Sets a new id for the current topic, being displayed.
     * @param tid  The new user's username.
     */
    public void setTid(int tid) {
        this.tid = tid;
    }

    /**
     * Gets a list of the ideas present in the topic being displayed.
     * @return  An array of Idea objects, containing all the ideas present in the topic being displayed.
     */
    public Idea[] getIdeas(){
        return this.ideas;
    }

    /**
     * Sets a new list of ideas, present in the given topic.
     * @param ideas1  The new topic's list of ideas.
     */
    public void setIdeas(Idea[] ideas1){
        this.ideas = ideas1;
    }

    /**
     * Action's execute method, called whenever the action is triggered.
     * @return A String object, informing the success or failure of the operation
     * @throws Exception    Throws an exception, in case of an error occurrs when accessing to the database
     */
    public String execute() throws Exception {
        super.execute();

        if ( mode.equals("topic") ) {
            this.ideas     = client.doGetTopicIdeas(tid);
            this.title = "#"+client.doGetTopicTitle(tid);
        } else if ( mode.equals("userideas") ) {
            this.ideas     = client.doGetUserIdeas();
            this.title     = "As minhas ideias";
        }
        //System.out.println("Got " + this.ideas.length + " for topic id: "+tid);

        return SUCCESS;
    }

    public String getTitle() {
        return title;
    }
}
