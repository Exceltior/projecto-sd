package actions.controller;

import model.data.Idea;
import java.util.ArrayList;


public class SubmitIdeaAction extends ClientAction{
    Idea idea;
    ArrayList<String> topics;
    String title;
    String body;
    String filePath;
    String topicsList;
    int moneyInvested;

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title1){
        this.title = title1;
    }

    public int getMoneyInvested(){
        return this.moneyInvested;
    }

    public void setMoneyInvested(int money){
        this.moneyInvested = money;
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

    public String getFilePath(){
        return this.filePath;
    }

    public void setFilePath(String filePath1){
        this.filePath = filePath1;
    }

    /**
     * Method to filter the topics inserted by the user. With this method we can discard invalid topic names, like:
     * #this is #topic . In this example we only consider the topics "this" and "topic", since it's impossible to use
     * hashtags with spaces.
     * @param list  An array of String objects, containing a list of the topics inserted by the user
     * @return  An ArrayList of String objects, containing the final valid topics.
     */
    private ArrayList<String> getTopicsFromList(String[] list){
        ArrayList<String> devolve = new ArrayList<String>();

        for (String aList : list) {
            if (!aList.contains(" ") && !aList.equals(""))//If the topic doesnt have a space
                devolve.add(aList);
        }

        return devolve;
    }

    public String execute() throws Exception {
        super.execute();

        System.out.println("O cliente e " + client.getUid());

        //Criar ideia; FIXME CODIGO FEIO COMO TUDO!!!!
        idea = new Idea();
        idea.setBody(body);
        idea.setTitle(title);

        topics = getTopicsFromList(topicsList.split("#"));

        if (client.doSubmitIdea(idea,topics,moneyInvested)){
            System.out.println("Correu bem a submissao da ideia!!!!!!!!");
            return SUCCESS;
        }
        else{
            System.out.println("Correu mal a submissao da ideia!!!!!!!");
            return ERROR;
        }
    }
}
