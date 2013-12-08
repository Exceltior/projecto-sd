package actions.controller;

/**
 * Class responsible for handling the creation of a new account in our system, associated with a facebook Account
 */
public class RegisterWithFacebookAction extends ClientAction{

    /**
     * token conatins the user's Facebook Access Token.
     * username contains the user's Facebook name.
     * password contains the user's account's password (not the facebook account)
     * email contains the user's email
     */
    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    public String doWork(){
        System.out.println("O token do utilizador no asdasdasd facebook e " + token);

        if (client.doRegisterNewAccountWithFacebook(token))
            return SUCCESS;
        else
            return ERROR;
    }
}
