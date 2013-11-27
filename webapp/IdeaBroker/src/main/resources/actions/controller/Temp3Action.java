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
public class Temp3Action extends ClientAction{
    private Topic[] topic;
    private Topic currentTopic;


    public Topic[] getTopic(){
        return this.topic;
    }

    public void setTopic(Topic[] topico){
        this.topic = topico;
    }

    public void setCurrentTopic(Topic topic1){
        this.currentTopic = topic1;
    }

    public Topic getCurrentTopic(){
        return this.currentTopic;
    }


    public String execute() throws Exception {
        super.execute();
        topic = client.doTopics();

        return SUCCESS;
    }
}
