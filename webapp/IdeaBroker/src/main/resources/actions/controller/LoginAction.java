package actions.controller;

/**
 * This action is used when the user tries to login. When execute() is called, the username and password are expected
 * to be filled with username and password to attempt login.
 */
public class LoginAction extends ClientAction{

    /**
     * The struts tags in the .jsp ensure that username and password are filled when execute() is called
     */
    private String username, password;

    /**
     * Try to login with given username and password. Note the call to super.execute() which is required by
     * ClientAction.
     * @return If login succeeds, returns SUCCESS. Otherwise it returns ERROR.
     * @throws Exception
     */
    public String execute() throws Exception {
        super.execute();

        if ( client.doLogin(username, password) )
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
}
