package actions.controller;

import model.data.Topic;

public class SearchTopicAction extends ClientAction {
    private String title;
    private Topic[] topics;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Topic[] getTopics() {
        return topics;
    }

    public void setTopics(Topic[] topicsList) {
        this.topics = topicsList;
    }

    public String execute() throws Exception{
        super.execute();

        this.topics = client.doSearchTopic(title);

        if (this.topics != null && this.topics.length>0)
            return SUCCESS;
        else
            return ERROR;
    }

}
