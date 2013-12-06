package actions.controller;


public class LoginFacebookAction extends ClientAction{
    private String token;

    public String getToken() {
        return this.token;
    }

    public void setToken(String id1){
        this.token = id1;
    }

    public String execute() throws Exception{
        super.execute();
        System.out.println("O token do user no facebook e " + token);
        if (client.doFacebokLogin(token))
            return SUCCESS;
        else
            return ERROR;
    }
}
