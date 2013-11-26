package actions.controller;

/**
 * This action is used when the user tries to register. When execute() is called, the username, password and email are
 * expected to be filled with username, password and email of the user performing the registration.
 */
public class RegisterAction extends ClientAction {

    /**
     * The struts tags in the .jsp ensure that username, password and email are filled when execute() is called
     */
    private String username;
    private String password;
    private String email;

    /**
     * Try to register the user with the username, password and email provided. Note the call to super.execute() which is
     * required by ClientAction.
     * @return  If the registration was successfull, returns SUCCESS. Otherwise returns ERROR.
     * @throws Exception
     */
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