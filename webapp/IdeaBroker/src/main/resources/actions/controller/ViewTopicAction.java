package actions.controller;

import model.data.Topic;

import java.util.ArrayList;

/**
 * Given a topic ID, fetch its data (ideas, etc), store them in the current Action. In struts.xml,
 * we redirect the user to a jsp which expects this.
 */
public class ViewTopicAction extends ClientAction{
    private int tid;

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }


    public String execute() throws Exception {
        super.execute();
        System.out.println("Got topic id: "+tid);
        //System.out.println("Got topic: "+topic);

        return SUCCESS;
    }
}
