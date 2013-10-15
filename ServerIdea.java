import java.io.DataOutputStream;
import java.util.ArrayList;

public class ServerIdea extends Idea {
    public ServerIdea(int id, int uid, String body, String title) {
        super(id, uid, body, title);
    }

    public ServerIdea() {

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
    // Create a ServerIdea from a SQL Line (array of strings)
    //
    public ServerIdea(String[] lines) {
        /* FIXME: Implement this */
        this.id = Integer.valueOf(lines[0]);
        this.title = lines[1]; //title come sbefore the body
        this.body = lines[2];
        this.uid = Integer.valueOf(lines[3]);
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
            parentIdeaRelationships[i] = relationshipFromInt(Integer.valueOf(row[1]));
        }

        return true;
    }
}
