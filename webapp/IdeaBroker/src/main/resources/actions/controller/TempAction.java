package actions.controller;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: joaquim
 * Date: 26/11/13
 * Time: 17:35
 * To change this template use File | Settings | File Templates.
 */
public class TempAction extends ClientAction {
    public ArrayList<Temp> banana = new ArrayList<Temp>();

    public String execute() throws Exception {
        super.execute();

        banana.add(new Temp(0,1));
        banana.add(new Temp(2,3));
        banana.add(new Temp(4,5));

        return SUCCESS;
    }

    public ArrayList<Temp> getBanana() {
        return banana;
    }
}
