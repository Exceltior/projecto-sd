package actions.controller;

public class SetSharePriceAction extends AJAXAction{

    private float price; //Price PER SHARE
    private int   iid;   //Id of the idea

    public void setIid(int iid) {
        this.iid = iid;
    }

    public String execute() throws Exception{
        super.execute();

        System.out.println("O execute do SetSharePriceAction esta a correr " + iid + " price: "+price);

        if (client.doSetSharePrice(this.iid,this.price)){
            System.out.println("Vou devolver sucesso");
            ajaxSuccess();
        }
        else {
            System.out.println("Vou devolver erro");
            ajaxFailure();
        }
        return SUCCESS;
    }
    public boolean isSuccess() {
        return super.isSuccess();
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
