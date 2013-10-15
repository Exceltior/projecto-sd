import java.io.Serializable;

public class Idea implements Serializable {
    public int getId() {
        return id;
    }

    protected int id, uid;
    protected String body, title;
    enum IdeaRelationship {IDEA_IN_FAVOR, IDEA_AGAINST}

    // FIXME: These may not always be filled. For instance, when we only want to send topic titles
    // to the server (list all ideas in a topic)
    protected int[] parentTopicIDs = null;
    protected int[] parentIdeaIDs = null;
    protected IdeaRelationship[] parentIdeaRelationships = null;

    public Idea(int id, int uid, String body, String title) {
        this.id = id;
        this.uid = uid;
        this.body = body;
        this.title = title;
    }
    public Idea() {
        this.id = this.uid = 0;
        this.body = this.title = null;
    }


}
