package actions.controller;

/**
 * Class responsible for handling the creation of a new account in our system, associated with a facebook Account
 */
public class RegisterNewAccountWithFacebook extends ClientAction{

    /**
     * token conatins the user's Facebook Access Token.
     * username contains the user's Facebook name.
     * password contains the user's account's password (not the facebook account)
     * email contains the user's email
     */
    private String token;
    private String username;
    private String email;//FIXME Este atributo e preciso???? - Podemos ir buscar o email do facebook, se for preciso
    private String password;//FIXME Este atributo e preciso????

    public String getEmail(){
        return this.email;
    }

    public void setEmail(String email1){
        this.email = email1;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String execute() throws Exception{
        super.execute();
        System.out.println("O token do utilizador no facebook e " + token);

        if (client.doRegisterNewAccountWithFacebook(username, password, email, token))
            return SUCCESS;
        else
            return ERROR;
    }
}
