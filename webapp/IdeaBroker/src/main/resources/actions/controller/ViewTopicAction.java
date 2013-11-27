package actions.controller;

import model.data.Idea;

/**
 * Given a topic ID, fetch its data (ideas, etc), store them in the current Action. In struts.xml,
 * we redirect the user to a jsp which expects this.
 */
public class ViewTopicAction extends ClientAction{
    private int tid;
    private Idea[] ideas;

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public Idea[] getIdeas(){
        return this.ideas;
    }

    public void setIdeas(Idea[] ideas1){
        this.ideas = ideas1;
    }

    public String execute() throws Exception {
        super.execute();

        this.ideas = client.doGetTopicIdeas(tid);
        //System.out.println("Got " + this.ideas.length + " for topic id: "+tid);

        return SUCCESS;
    }
}
