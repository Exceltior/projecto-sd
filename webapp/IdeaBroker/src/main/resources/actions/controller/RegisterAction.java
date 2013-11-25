package actions.controller;

public class RegisterAction extends ClientAction {
    private String username;
    private String password;
    private String email;

    public String execute() throws Exception {
        super.execute();

        if ( client.doRegister(username, password,email) )
            return SUCCESS;
        else
            return ERROR;

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}