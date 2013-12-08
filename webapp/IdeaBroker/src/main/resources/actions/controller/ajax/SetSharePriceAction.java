package actions.controller.ajax;


/**
 * This action is used when the user tries to define a new selling price for his shares of a given idea.
 * When execute() is called, the iid and price are expected to have been filled, so that we know which idea is involved in
 * the operation, and at which price the user wants to sell his shares of that idea.
 */
public class SetSharePriceAction extends AJAXAction {

    /**
     * price represents the price ar which the user wants to sell his shares of an idea.
     * iid represents the id of the idea choosen by the user.
     */
    private float price; //Price PER SHARE
    private int   iid;   //Id of the idea

    /**
     * Sets a new id for the idea, whose shares' selling prices we are going to define
     * @param iid   The id of the idea.
     */
    public void setIid(int iid) {
        this.iid = iid;
    }

    /**
     * Try to set the selling price of the selected idea's shares to a value defined by the user.
     * @return              If the operations is successfull, it returns SUCCESS. Otherwise, returns ERROR.
     * @throws Exception    Throws an exception, in case of an error occurrs when accessing to the database.
     */
    public void doAjaxWork(){

        System.out.println("O execute do SetSharePriceAction esta a correr " + iid + " price: "+price);

        if (client.doSetSharePrice(this.iid,this.price)){
            System.out.println("Vou devolver sucesso");
            ajaxSuccess();
        }
        else {
            System.out.println("Vou devolver erro");
            ajaxFailure();
        }
    }

    /**
     * Gets the result of the operation, stored in the super class AJAXAction.
     * @return  A boolean value, indcating the success or failure of the operation, stored in the super class AJAXAction.
     */
    public boolean isSuccess() {
        return super.isSuccess();
    }

    /**
     * Sets a new shares' selling price, defined by the user.
     * @param price The new shares' selling price.
     */
    public void setPrice(float price) {
        this.price = price;
    }
}
