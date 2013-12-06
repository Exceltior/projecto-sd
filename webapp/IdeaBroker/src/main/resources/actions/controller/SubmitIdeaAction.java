package actions.controller;

import model.data.Idea;
import model.data.NetworkingFile;
import java.io.File;
import java.util.ArrayList;

/**
 * This action is used when the user wants to create a new idea. When this action is "generated" the following attributes
 * are expected to have been defined by the user: title; body; topicsList; moneyInvested; contentType; filename.
 */
public class SubmitIdeaAction extends ClientAction{

    /**
     * idea contains an Idea object, with the idea created by the user.
     * topics contains an ArrayList of String objects, each containin a topic where the idea is going to be included.
     * title contains the title of the idea.
     * body contains the body of the idea.
     * topicsList contains a list of the topics where we are going to include the idea, all concatenated in a String object.
     * moneyInvested contains an Integer value, with the money the user wants to invest in the idea.
     * file contains a file the user may decide to upload alongside with the submition of the idea.
     * contentType contains the type of the content of the file uploaded with the idea.
     * filename contains the name of the file uploaded with the idea.
     */
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

    /**
     * Setter for the attribute setFileContentType, containing the type of content of the file uploaded as an attachment
     * of the uploaded idea.
     * @param temp  The new value for the type of content of the file uploaded with the idea.
     */
    public void setFileContentType(String temp){
        this.contentType =  temp;
    }

    /**
     * Gets the type of content of the file uploaded with the idea.
     * @return  A String object, containing the type of content of the file uploaded with the idea.
     */
    public String getFileContentType(){
        return this.contentType;
    }

    /**
     * Sets a new file name for the file uploaded with the idea
     * @param temp  The name of the file uploaded with the idea
     */
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
            if (!aList.trim().contains(" ") && !aList.trim().equals(""))//If the topic doesnt have a space
                devolve.add(aList.trim());
        }

        return devolve;
    }

    /**
     * Action's execute method, called whenever the action is triggered.
     * In this method we try to create a new idea with the information inserted by the user. We are also accepting files
     * as an optional attachment to the idea submition.
     * @return              If the action performes well, as expected, then this method returns SUCCESS.
     *                      Otherwise, it returns ERROR.
     * @throws Exception    Throws an exception, in case of an error occurrs when accessing to the database.
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
