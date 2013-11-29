package actions.controller;

import model.data.Idea;
import org.apache.struts2.ServletActionContext;

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
    ArrayList<Integer> variavelLocal = new ArrayList<Integer>();
    public int aha = 3;
    Idea banana2;

    public ArrayList<Integer> getVariavelLocal() {
        return variavelLocal;
    }

    public void setVariavelLocal(ArrayList<Integer> variavelLocal) {
        this.variavelLocal = variavelLocal;
    }

    public int getAha() {
        return aha;
    }

    public String getBanana2(){
        return this.banana2.toString();
    }

    public void setBanana2(Idea temp){
        this.banana2 = temp;
    }

    public String execute() throws Exception {
        super.execute();

        banana.add(new Temp(9, 10));
        banana.add(new Temp(11, 12));
        banana.add(new Temp(13, 14));

        variavelLocal.add(77);
        variavelLocal.add(78);
        variavelLocal.add(79);
        banana2 = client.doSearchIdea(1);

        return SUCCESS;
    }

    public ArrayList<Temp> getBanana() {
        return banana;
    }
}
