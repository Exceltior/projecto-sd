package actions.controller;

/**
 * Class responsible for handling the association of a Facebook Account to an existing account in our system.
 */
public class AssociateWithFacebookAction extends AJAXAction {

    /**
     * token stores the user's Facebook Access Token.
     */
    private String token;

    /**
     * Defines a new Facebook Access Token for the user.
     * @param id1   The new Facebook Access Token we want to define to the user.
     */
    public void setToken(String id1){
        this.token = id1;
    }

    /**
     * execute method, called whenever a user wants to associate a facebook account to his current account in our system.
     * @return              A String object, containing the result or failure of the operation.
     * @throws Exception
     */
    public String execute() throws Exception{
        super.execute();
        System.out.println("O token do utilizador no facebook 222 e " + token);
        setAjaxStatus(client.doAssociateWithFacebook(token));
        return SUCCESS;
    }
    public boolean isSuccess() {
        return super.isSuccess();
    }
}
