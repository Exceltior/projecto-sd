package actions.controller;

import model.data.Topic;
import org.apache.struts2.ServletActionContext;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: joaquim
 * Date: 26/11/13
 * Time: 17:35
 * To change this template use File | Settings | File Templates.
 */
public class Temp2Action extends ClientAction {
    int umaVariavel2;
    Temp umaVariavel;

    public int getUmaVariavel2() {
        return umaVariavel2;
    }

    public void setUmaVariavel2(int umaVariavel2) {
        this.umaVariavel2 = umaVariavel2;
    }

    public Temp getUmaVariavel() {
        return umaVariavel;
    }

    public void setUmaVariavel(Temp umaVariavel) {
        this.umaVariavel = umaVariavel;
    }

    public String execute() throws Exception {
        super.execute();

        System.out.println(umaVariavel);
        System.out.println(umaVariavel2);
        return SUCCESS;
    }


}
