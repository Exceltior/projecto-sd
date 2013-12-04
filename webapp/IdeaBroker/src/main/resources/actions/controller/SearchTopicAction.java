package actions.controller;

import model.data.Topic;

/**
 * This action isused when the user wants to search a topic in the database. When execute() is called it is expected
 * that title has been filled by the user, in order to perform the search.
 */
public class SearchTopicAction extends ClientAction {
    private String title;
    private Topic[] topics;

    /**
     * Returns the title of the topic we want to search.
     * @return  A String object, containing the title of the topic we want to search.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets a new value to the class' attribute title.
     * @param title The new value we want to assign to the class' attribute title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the list of topics found in the database, based on the search we performed.
     * @return  An array of Topic objects, containing the list of topics found in the database, based on the search
     *          we performed.
     */
    public Topic[] getTopics() {
        return topics;
    }

    /**
     * Defines a new set of Topic objects to the class' attribute topics.
     * @param topicsList    The new set of Topics we want to assign to the class' attribute topics.
     */
    public void setTopics(Topic[] topicsList) {
        this.topics = topicsList;
    }

    /**
     * Performs the search of a topic in the database, based on the title inserted by the user.
     * @return              If the operation is successfull it returns SUCCESS. Otherwise, returns ERROR.
     * @throws Exception    Throws an exception, in case of an error occurrs when accessing to the database
     */
    public String execute() throws Exception{
        super.execute();

        this.topics = client.doSearchTopic(title);

        if (this.topics != null && this.topics.length>0)
            return SUCCESS;
        else
            return ERROR;
    }

}
