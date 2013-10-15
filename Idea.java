import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

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

    //Parent ideas and their relationships
    protected int[] parentIdeaIDs = null;
    protected IdeaRelationship[] parentIdeaRelationships = null;

    //Children ideas and their relationships
    protected int[] childrenIdeaIDs = null;
    protected IdeaRelationship[] childrenIdeaRelationships = null;

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

    public boolean writeToDataStream(DataOutputStream out) {
        if ( ! Common.sendInt(id, out) )
            return false;
        if ( ! Common.sendInt(uid, out) )
            return false;
        if ( ! Common.sendString(title, out) )
            return false;
        if ( ! Common.sendString(body, out) )
            return false;

        return true;
    }

    ////
    // Create an Idea from a SQL row (array of strings)
    //
    public Idea(String[] row) {
        /* FIXME: Implement this */
        this.id = Integer.valueOf(row[0]);
        this.title = row[1]; //title come sbefore the body
        this.body = row[2];
        this.uid = Integer.valueOf(row[3]);
    }

    ////
    // Given a SQL row with all the parent topics (from table TopicosIdeias), add them to this idea as its parent
    //
    public boolean addParentTopicsFromSQL(ArrayList<String[]> lines) {
        parentTopicIDs = new int[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            String[] row = lines.get(i);
            parentTopicIDs[i] = Integer.valueOf(row[0]);
        }

        return true;
    }

    private IdeaRelationship relationshipFromInt(int relationship) {
        if ( relationship == 1 )
            return IdeaRelationship.IDEA_IN_FAVOR;
        else if ( relationship == -1 )
            return IdeaRelationship.IDEA_AGAINST;
        else // NEVER GONNA HAPPEN
            return IdeaRelationship.IDEA_IN_FAVOR;
    }


    public boolean addParentIdeasFromSQL(ArrayList<String[]> lines) {
        parentIdeaIDs = new int[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            String[] row = lines.get(i);
            parentIdeaIDs[i] = Integer.valueOf(row[0]);
            parentIdeaRelationships[i] = relationshipFromInt(Integer.valueOf(row[2]));
        }

        return true;
    }

    public boolean addChildrenIdeasFromSQL(ArrayList<String[]> lines) {
        childrenIdeaIDs = new int[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            String[] row = lines.get(i);
            childrenIdeaIDs[i] = Integer.valueOf(row[1]);
            childrenIdeaRelationships[i] = relationshipFromInt(Integer.valueOf(row[2]));
        }

        return true;
    }

}
