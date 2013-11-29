package actions.controller;

import model.data.Idea;
import model.data.Topic;


public class submitIdeaAction extends ClientAction{
    Idea idea;
    String[] topics;
    String title;
    String body;
    String topicsList;

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title1){
        this.title = title1;
    }

    public String getTopicsList(){
        return this.topicsList;
    }

    public void setTopicsList(String topicsq){
        this.topicsList = topicsq;
    }

    public String getBody(){
        return this.body;
    }

    public void setBody(String body1){
        this.body = body1;
    }

    public Idea getIdea(){
        return this.idea;
    }

    public void setIdea(Idea idea2){
        this.idea = idea2;
    }

    public void setTopics(String[] topic1){
        this.topics = topic1;
    }

    public String[] getTopics(){
        return this.topics;
    }

    public String execute() throws Exception {
        super.execute();

        //Criar ideia; FIXME CODIGO FEIO COMO TUDO!!!!
        idea = new Idea();
        idea.setBody(body);
        idea.setTitle(title);

        topics = topicsList.split(";");

        if (client.doSubmitIdea(idea,topics)){
            System.out.println("Correu bem a submissao da ideia!!!!!!!!");
            return SUCCESS;
        }
        else{
            System.out.println("Correu mal a submissao da ideia!!!!!!!");
            return ERROR;
        }
    }
}
