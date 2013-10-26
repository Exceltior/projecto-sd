import java.io.Serializable;

////
// A Topic, meant to be extended by the Client and the Server.
//
public class Topic implements Serializable{
    int   id;
    String title;
    String body;

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return "Topic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

    Topic() {
        this.id = 0;
        this.title = this.body = null;
    }

    public int getId(){
        return this.id;
    }

    public String getTitle(){
        return this.title;
    }
}
