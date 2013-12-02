package actions.controller;

import model.data.Idea;

import java.util.ArrayList;

/**
 * TempAction, just for testing purposes.
 */
public class TempAction extends ClientAction {
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

        variavelLocal.add(77);
        variavelLocal.add(78);
        variavelLocal.add(79);
        banana2 = client.doSearchIdea(1);

        return SUCCESS;
    }
}
