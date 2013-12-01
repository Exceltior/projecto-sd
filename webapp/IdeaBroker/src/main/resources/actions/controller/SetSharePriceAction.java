package actions.controller;

public class SetSharePriceAction extends ClientAction{

    private int price;//Price PER SHARE
    private int iid;//Id of the idea

    public int getPrice(){
        return this.price;
    }

    public void setPrice(int price1){
        this.price = price1;
    }

    public int getIid(){
        return this.iid;
    }

    public void setIid(int iid) {
        this.iid = iid;
    }

    public String execute() throws Exception{
        super.execute();

        this.iid = GoSetSharePriceAction.ideaId;
        System.out.println("O execute do SetSharePriceAction esta a correr " + iid);

        if (client.doSetSharePrice(this.iid,this.price)){
            System.out.println("Vou devolver sucesso");
            return SUCCESS;
        }

        System.out.println("Vou devolver erro");
        return ERROR;
    }

}
