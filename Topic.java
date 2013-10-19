import java.io.Serializable;

////
// A Topic, meant to be extended by the Client and the Server.
//
public class Topic implements Serializable{
    protected int   id;
    protected String title;
    protected String body;

    @Override
    public String toString() {
        return "Topic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

    public Topic() {
        this.id = 0;
        this.title = this.body = null;
    }
    public Topic(int id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
    }

    public int getId(){
        return this.id;
    }

    public String getTitle(){
        return this.title;
    }
}
