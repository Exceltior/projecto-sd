package model.data;

import common.util.Common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

public class Idea implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id, uid, shares_to_buy, facebookId;

    public    float  marketValue;
    protected String body, title, file;

    public Topic[] topics;

    /**
     * Specific for the user which makes the request
     */
    private int     numSharesOwned;
    private float   percentOwned;
    private boolean inWatchList;
    private float   sellingPrice; //The price at which this user is selling shares, if he owns any

    /**
     * Class Constructor.
     */
    public Idea() {
        this.id = this.uid = 0;
        this.body = this.title = null;
        this.file = "N";
        this.shares_to_buy = -2;
        this.marketValue = 0;

        /**
         * User specific, might be null at start (changed later with setters)
         */
        this.numSharesOwned = 0;
        this.percentOwned = 0;
        this.inWatchList = false;
        this.sellingPrice = 0;
        this.facebookId = 0;
    }
    /**
     * Method responsible for comunicating with the Server, writing information to the DataOutputStream.
     * @param out   The DataOutputStream where we are going to write the information about the idea.
     * @return      A boolean value, indicating the success or failure of the operation.
     */
    public boolean writeToDataStream(DataOutputStream out) {
        if ( ! Common.sendInt(id, out) )
            return false;
        if ( ! Common.sendInt(uid, out) )
            return false;
        if ( ! Common.sendString(title, out) )
            return false;
        if ( ! Common.sendString(body, out) )
            return false;
        if ( ! Common.sendString(file, out) )
            return false;
        if ( ! Common.sendInt(shares_to_buy, out) )
            return false;
        if ( ! Common.sendInt(numSharesOwned, out) )
            return false;
        if ( ! Common.sendFloat(percentOwned, out) )
            return false;
        if ( ! Common.sendInt(inWatchList ? 1 : 0, out) )
            return false;
        if ( ! Common.sendFloat(sellingPrice, out) )
            return false;
        if ( ! Common.sendFloat(marketValue, out) )
            return false;
        if ( ! Common.sendInt(facebookId, out) )
            return false;

        return true;
    }

    /**
     * Method responsible for receiving messages from the Server, reading information from the DataInputStream.
     * @param in    The DataInputStream where we are going to read the information about the idea.
     * @return      A boolean value, indicating the success or failure of the operation.
     */
    public boolean readFromDataStream(DataInputStream in) {

        if ( (this.id = Common.recvInt(in)) == -1)
            return false;
        if ( (this.uid = Common.recvInt(in)) == -1)
            return false;
        if ( (this.title = Common.recvString(in)) == null )
            return false;
        if ( (this.body = Common.recvString(in)) == null )
            return false;
        if ( (this.file = Common.recvString(in)) == null )
            return false;
        if ( (this.shares_to_buy = Common.recvInt(in)) == -1)
            return false;
        if ( (this.numSharesOwned = Common.recvInt(in)) == -1)
            return false;
        if ( (this.percentOwned = Common.recvFloat(in)) == -1)
            return false;

        int tmp;
        if ( (tmp = Common.recvInt(in)) == -1)
            return false;
        this.inWatchList = tmp == 1;

        if ( (this.sellingPrice = Common.recvFloat(in)) == -1)
            return false;
        if ( (this.marketValue = Common.recvFloat(in)) == -1)
            return false;
        if ( (this.facebookId = Common.recvInt(in)) == -1)
            return false;

        return true;
    }

    /**
     * Create an Idea from a SQL row (array of strings)
     * @param row   SQL row (array of strings)
     */
    public Idea(String[] row) {
        this.id = Integer.valueOf(row[0]);
        this.title = row[1]; //title comes before the body
        this.body = row[2];
        this.uid = Integer.valueOf(row[3]);
        this.file = "N";
        this.shares_to_buy = -2;
        this.marketValue= Float.valueOf(row[7]);

        if ( "null".equals(row[8]) )
            this.facebookId=Integer.valueOf(row[8]);

        /**
         * User specific, might be null at start (changed later with setters)
         */
        this.numSharesOwned = 0;
        this.percentOwned = 0;
        this.inWatchList = false;
        this.sellingPrice = 0;
        this.facebookId = 0;

    }

    /**
     * Gets the id on the facebook associated with the idea, if it exists.
     * @return  The id on the facebook associated with the idea, if it exists.
     */
    public int getFacebookId(){
        return this.facebookId;
    }

    /**
     * Sets the id on the facebook associated with the idea, if it exists.
     */
    public void setFacebookId(int id){
        this.facebookId = id;
    }

    /**
     * Sets the title of the idea.
     * @param title1    The new title to assign to the idea.
     */
    public void setTitle(String title1){
        this.title = title1;
    }

    /**
     * Sets the body of the idea.
     * @param body1    The new body to assign to the idea.
     */
    public void setBody(String body1){
        this.body = body1;
    }

    /**
     * Sets the id of the idea
     * @param id1   The new id to assign the idea.
     */
    public void setId(int id1){
        this.id = id1;
    }

    /**
     * Sets a new value for the attribute "file" of the Idea object, which tells if the idea has a file associated, or not.
     * @param f The new value for the attribute "file" of the Idea object.
     */
    public void setFile(String f){
        this.file = f;
    }

    /**
     * Gets the value assigned to the attribute "file" of the object.
     * @return  The value assigned to the attribute "file" of the object.
     */
    public String getFile(){
        return this.file;
    }

    /**
     * Sets the number of shares for the given idea available for transactions.
     * @param n The new number of shares available for transactions we want to define.
     */
    public void setSharesBuy(int n){
        this.shares_to_buy = n;
    }

    /**
     * Gets the title of the idea.
     * @return  The title of the idea.
     */
    public String getTitle(){
        return this.title;
    }

    /**
     * Gets the id of the idea.
     * @return  The id of the idea.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the number of shares for the given idea available for transactions.
     * @return  The number of shares for the given idea available for transactions.
     */
    public int getSharesBuy(){
        return this.shares_to_buy;
    }

    /**
     * Gets the body of the idea.
     * @return  The body of the idea.
     */
    public String getBody(){
        return this.body;
    }

    /**
     * Add a given number of shares to the number of shares a user wants to buy for the given idea.
     * @param n The number of share to add to the number of shares a user wants to buy for the given idea.
     */
    public void addSharesToBuy(int n){
        this.shares_to_buy = this.shares_to_buy + n;
    }

    /**
     * toString method for the class.
     * @return  A String object, containing all the relevant information about the idea.
     */
    public String toString(){
        if (this.shares_to_buy == -2)
            return "Idea{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", userid = " + uid + '\'' +
                ", file = " + file +
                '}';

        return "Idea{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", userid = " + uid + '\'' +
                ", file = " + file + '\'' +
                ", shares to buy = " + shares_to_buy +
                '}';
    }

    public int getNumSharesOwned() {
        return numSharesOwned;
    }

    public void setNumSharesOwned(int numSharesOwned) {
        this.numSharesOwned = numSharesOwned;
    }

    public float getPercentOwned() {
        return percentOwned;
    }

    public void setPercentOwned(float percentOwned) {
        this.percentOwned = percentOwned;
    }

    public boolean isInWatchList() {
        return inWatchList;
    }

    public void setInWatchList(boolean inWatchList) {
        this.inWatchList = inWatchList;
    }

    public float getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(float sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
    public float getMarketValue() {
        return marketValue;
    }

    /**
     * Gets the list of topics associated to the idea.
     * @return  An array of Topic objects, containing the list of topics associated to the idea.
     */
    public Topic[] getTopics() {
        return topics;
    }

    /**
     * Defines a new list of topics associated to the idea.
     * @param topics    The new list of topics associated to the idea.
     */
    public void setTopics(Topic[] topics) {
        this.topics = new Topic[topics.length];
        System.arraycopy(topics, 0, this.topics, 0, topics.length);

    }
}
