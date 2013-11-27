package actions.controller;

import model.data.Topic;

/**
 * Lists all the topics stored in the database. In struts.xml, we redirect the user to a jsp which expects this.
 */
public class ListTopicsAction extends ClientAction{
    private Topic[] topics;

    public Topic[] getTopics(){
        return this.topics;
    }

    public void setTopics(Topic[] topics) {
        this.topics = topics;
    }


    public String execute() throws Exception {
        super.execute();
        this.topics = client.doTopics();
        /*topics = new Topic[3];
        for (int i=0; i < topics.length; i++)
            topics[i] =  (new Topic(i+100, "TT"+(i)));*/

        return SUCCESS;
    }
}
