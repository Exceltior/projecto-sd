package actions.controller;

import model.data.Topic;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: joaquim
 * Date: 27/11/13
 * Time: 09:40
 * To change this template use File | Settings | File Templates.
 */
public class ListTopics extends ClientAction{
    private Topic[] topics;

    public Topic[] getTopics(){
        return this.topics;
    }

    public void setTopics(Topic[] topics) {
        this.topics = topics;
    }


    public String execute() throws Exception {
        super.execute();
        //topic = client.doTopics();
        topics = new Topic[3];
        for (int i=0; i < topics.length; i++)
            topics[i] =  (new Topic(i+100, "TT"+(i)));

        return SUCCESS;
    }
}
