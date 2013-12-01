package model.data;

import java.io.Serializable;

////
// A Topic, meant to be extended by the Client and the Server.
//
public class Topic implements Serializable{
    protected int   id, numIdeas, userid;
    protected String title;

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return "Topic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", created by user: " + userid + '\'' +
                ", number of ideas = " + numIdeas + '\'' +
                '}';
    }

    public Topic() {
        this.id = 0;
        this.numIdeas = 0;
        this.userid = 0;
        this.title = null;
    }
    public Topic(int id, String title, int userid, int numIdeas) {
        this.id = id;
        this.title = title;
        this.userid = userid;
        this.numIdeas = numIdeas;
    }

    public int getId(){
        return this.id;
    }

    public int getNumIdeas(){
        return this.numIdeas;
    }

    public String getTitle(){
        return this.title;
    }

    public int getUserid(){
        return this.userid;
    }
}
