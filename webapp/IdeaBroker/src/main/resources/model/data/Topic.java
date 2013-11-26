package model.data;

import java.io.Serializable;

////
// A Topic, meant to be extended by the Client and the Server.
//
public class Topic implements Serializable{
    protected int   id;
    protected String title;

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return "Topic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }

    public Topic() {
        this.id = 0;
        this.title = null;
    }
    public Topic(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId(){
        return this.id;
    }

    public String getTitle(){
        return this.title;
    }
}
