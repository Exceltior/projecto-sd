package actions.controller;

import model.data.Idea;
import model.data.NetworkingFile;
import org.apache.commons.lang.xwork.StringEscapeUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * This action is used when the user wants to create a new idea. When this action is "generated" the following attributes
 * are expected to have been defined by the user: title; body; topicsList; moneyInvested; contentType; filename.
 */
public class SubmitIdeaAction extends AJAXAction{

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
    float moneyInvested;
    File file;
    String contentType;
    String filename;

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
     * Sets a new file name for the file uploaded with the idea
     * @param temp  The name of the file uploaded with the idea
     */
    public void setfileFileName(String temp){
        this.filename = temp;
    }

    /**
     * Defines a new value to the title of the idea.
     * @param title1   The new title of the idea.
     */
    public void setTitle(String title1){
        this.title = title1;
    }

    /**
     * Defines the money the user is going to invest in this new idea.
     * @param money The money the user is going to invest in this new idea.
     */
    public void setMoneyInvested(float money){
        this.moneyInvested = money;
    }

    public void setTopicsList(String topicsq){
        this.topicsList = topicsq;
    }

    /**
     * Setter for the attribute body.
     * @param body1 The body of the idea.
     */
    public void setBody(String body1){
        this.body = body1;
    }

    /**
     * Setter for the attribute idea.
     * @param idea2 The idea we want to assign to the attribute idea.
     */
    public void setIdea(Idea idea2){
        this.idea = idea2;
    }

    private String verifyTopicString(String temp){
        if (!temp.trim().contains(" "))
            return temp.trim();
        //Here we know the string has at least one space, lets check if it is at the end of the string
        String substring = temp.substring(0,temp.length()-1);
        if (!substring.trim().contains(" "))//The space is in the last position of the string
            return temp.trim();
        //Note: I left the if unsimplified for better understanding of the code
        return null;
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
        String temp;

        for (String aList : list) {
            if (!aList.trim().equals("")){
                temp = verifyTopicString(aList);
                if (temp != null && !devolve.contains(temp.trim()))
                    devolve.add(StringEscapeUtils.escapeSql(aList.trim()));
            }
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

        System.out.println("moneyInvested: "+moneyInvested);
        System.out.println("title: "+title);
        System.out.println("topicsList: "+topicsList);

        NetworkingFile userFile = null;

        if (file != null){
            //System.out.println("Ideia submetida e tem " + file.getPath() + " como caminho para o ficheiro");
            userFile = new NetworkingFile(file.getPath());
        }

        idea = new Idea();
        idea.setBody(StringEscapeUtils.escapeSql(body));
        idea.setTitle(StringEscapeUtils.escapeSql(title));

        topics = getTopicsFromList(topicsList.split("#"));

        if (client.doSubmitIdea(idea,topics,moneyInvested,userFile)){
            System.out.println("Correu bem a submissao da ideia!!!!!!!!");
            ajaxSuccess();
        }
        else{
            System.out.println("Correu mal a submissao da ideia!!!!!!!");
            ajaxFailure();
        }

        return SUCCESS;
    }
    public boolean isSuccess() {
        return super.isSuccess();
    }
}
