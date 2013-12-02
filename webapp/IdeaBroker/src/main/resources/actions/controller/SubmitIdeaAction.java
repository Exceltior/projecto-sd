package actions.controller;

import model.data.Idea;
import model.data.NetworkingFile;

import java.io.File;
import java.util.ArrayList;


public class SubmitIdeaAction extends ClientAction{
    Idea idea;
    ArrayList<String> topics;
    String title;
    String body;
    String topicsList;
    int moneyInvested;
    File file;
    String contentType;
    String filename;

    /**
     * Gets the file attached to the idea.
     * @return  A File object, containing the file attached to the idea.
     */
    public File getFile(){
        return this.file;
    }

    /**
     * Defines a new file to attach to the idea.
     * @param file1 A File object, which is the new file to attach to the idea.
     */
    public void setFile(File file1){
        this.file = file1;
    }

    public void setFileContentType(String temp){
        this.contentType =  temp;
    }

    public String getFileContentType(){
        return this.contentType;
    }

    public void setfileFileName(String temp){
        this.filename = temp;
    }

    /**
     * Gets the title of the idea being created.
     * @return  A String object, containig the title of the idea being created.
     */
    public String getTitle(){
        return this.title;
    }

    /**
     * Defines a new value to the title of the idea.
     * @param title1   The new title of the idea.
     */
    public void setTitle(String title1){
        this.title = title1;
    }

    /**
     * Gets the money invested by the user in the idea.
     * @return  The money invested by the user in the idea.
     */
    public int getMoneyInvested(){
        return this.moneyInvested;
    }

    /**
     * Defines the money the user is going to invest in this new idea.
     * @param money The money the user is going to invest in this new idea.
     */
    public void setMoneyInvested(int money){
        this.moneyInvested = money;
    }

    /**
     * Gets the list of topics in which we are going to include the new idea.
     * @return  An array of String objects, containing the list of topics in which we are going to include the new idea.
     */
    public String getTopicsList(){
        return this.topicsList;
    }

    public void setTopicsList(String topicsq){
        this.topicsList = topicsq;
    }

    /**
     * Gets the body of the idea the user tried to submit.
     * @return  A String object, with the body of the idea the user tried to submit.
     */
    public String getBody(){
        return this.body;
    }

    /**
     * Setter for the attribute body.
     * @param body1 The body of the idea.
     */
    public void setBody(String body1){
        this.body = body1;
    }

    /**
     * Returns the idea the user tried to submit.
     * @return  An Idea object, containing the idea the user tried to submit.
     */
    public Idea getIdea(){
        return this.idea;
    }

    /**
     * Setter for the attribute idea.
     * @param idea2 The idea we want to assign to the attribute idea.
     */
    public void setIdea(Idea idea2){
        this.idea = idea2;
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

    /**
     * Action's execute method, called whenever the action is triggered.
     * In this method we try to create a new idea with the information inserted by the user. We are also accepting files
     * as an optional attachment to the idea submition.
     * @return  If the action performes well, as expected, then this method returns SUCCESS. Otherwise, it returns ERROR.
     * @throws Exception
     */
    public String execute() throws Exception {
        super.execute();

        NetworkingFile userFile = null;

        if (file != null){
            //System.out.println("Ideia submetida e tem " + file.getPath() + " como caminho para o ficheiro");
            userFile = new NetworkingFile(file.getPath());
        }

        //Criar ideia; FIXME CODIGO FEIO COMO TUDO!!!!
        idea = new Idea();
        idea.setBody(body);
        idea.setTitle(title);

        topics = getTopicsFromList(topicsList.split("#"));

        if (client.doSubmitIdea(idea,topics,moneyInvested,userFile)){
            System.out.println("Correu bem a submissao da ideia!!!!!!!!");
            return SUCCESS;
        }
        else{
            System.out.println("Correu mal a submissao da ideia!!!!!!!");
            return ERROR;
        }
    }
}
