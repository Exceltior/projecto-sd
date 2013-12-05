package actions.controller;


public class LoginFacebookAction extends ClientAction{
    private String id;

    public String getId(){
        return this.id;
    }

    public void setId(String id1){
        this.id = id1;
    }

    public String execute() throws Exception{
        System.out.println("O id do user no facebook e " + id);
        //super.execute();
        return SUCCESS;
    }
}
