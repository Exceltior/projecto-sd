package actions.controller;

import model.data.Topic;

/**
 * Lists all the topics stored in the database. In struts.xml, we redirect the user to a jsp which expects this.
 */
public class ListTopicsAction extends ClientAction{

    /**
     * The list of topics we are going to present to the user.
     */
    private Topic[] topics;

    /**
     * Getter for the attribute topics. Gets all the topics stored in the database.
     * @return  An array of Topic objects, containing all the topics stored in the database.
     */
    public Topic[] getTopics(){
        return this.topics;
    }

    /**
     * Setter for the attribute topics.
     * @param topics    List of topics we want to assign to the attribute topics
     */
    public void setTopics(Topic[] topics) {
        this.topics = topics;
    }

    /**
     * Action's execute method, called whenever the action is triggered.
     * @return A String object, informing the success or failure of the operation
     * @throws Exception    Throws an exception, in case of an error occurrs when accessing to the database
     */
    public String execute() throws Exception {
        super.execute();
        this.topics = client.doGetTopics();
        /*topics = new Topic[3];
        for (int i=0; i < topics.length; i++)
            topics[i] =  (new Topic(i+100, "TT"+(i)));*/

        return SUCCESS;
    }
}