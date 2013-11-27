package model.data;

import common.util.Common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

public class Idea implements Serializable {

    private static final long serialVersionUID = 1L;

    public int id, uid, shares_to_buy;
    protected String body, title, file;

    ////
    // Class Constructor
    ////
    public Idea() {
        this.id = this.uid = 0;
        this.body = this.title = null;
        this.file = "N";
        this.shares_to_buy = -2;
    }

    ////
    //  Method responsible for comunicating with the Server, writing information to the data Output Stream
    ////
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

        return true;
    }

    ////
    //  Method responsible for receiving messages from the Server, reading information from the data Output Stream
    ////
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

        return true;
    }

    ////
    // Create an Idea from a SQL row (array of strings)
    //
    public Idea(String[] row) {
        this.id = Integer.valueOf(row[0]);
        this.title = row[1]; //title comes before the body
        this.body = row[2];
        this.uid = Integer.valueOf(row[3]);
        this.file = "N";
        this.shares_to_buy = -2;
    }

    public void setFile(String f){
        this.file = f;
    }

    public String getFile(){
        return this.file;
    }

    public void setSharesBuy(int n){
        this.shares_to_buy = n;
    }

    public String getTitle(){
        return this.title;
    }

    public int getId() {
        return id;
    }

    public int getSharesBuy(){
        return this.shares_to_buy;
    }

    public void addSharesToBuy(int n){
        this.shares_to_buy = this.shares_to_buy + n;
    }

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
}
