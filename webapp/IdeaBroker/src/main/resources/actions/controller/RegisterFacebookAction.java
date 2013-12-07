package actions.controller;

public class RegisterFacebookAction extends ClientAction {
    private String token;

    public String getToken() {
        return this.token;
    }

    public void setToken(String id1){
        this.token = id1;
    }

    public String execute() throws Exception{
        super.execute();
        System.out.println("O token do utilizador no facebook e " + token);
        if (client.doFacebookRegistration(token))
            return SUCCESS;
        else
            return ERROR;
    }
}
