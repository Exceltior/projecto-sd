package actions.controller;

import org.apache.commons.lang.xwork.StringEscapeUtils;

/**
 * This action is used when the user tries to facebookLogin. When execute() is called, the username and password are expected
 * to be filled with username and password to attempt facebookLogin.
 */
public class LoginAction extends ClientAction{

    /**
     * The struts tags in the .jsp ensure that username and password are filled when execute() is called
     */
    private String username, password;

    /**
     * Try to facebookLogin with given username and password. Note the call to super.execute() which is required by
     * ClientAction.
     * @return              If facebookLogin succeeds, returns SUCCESS. Otherwise it returns ERROR.
     * @throws Exception    Throws an exception, in case of an error occurrs when accessing to the database
     */
    public String doWork() {

        if (username.isEmpty() || password.isEmpty())
            return ERROR;

        System.out.println("Login: "+username+" "+password);
        if ( client.doLogin(StringEscapeUtils.escapeSql(username), StringEscapeUtils.escapeSql(password)) ){
            System.out.println("Vou devolver sucesso no execute do LoginAction");
            return SUCCESS;
        }
        else{
            System.out.println("Vou devolver erro no execute do LoginAction");
            return ERROR;
        }
    }

    /**
     * Gets the password for the given user.
     * @return  A String object, containing the password for the given user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Defines a new password for the given user.
     * @param password  The new password for the user.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the username of the given user.
     * @return  A String object, containing the username of the given user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets a new username for the user.
     * @param username  The new user's username.
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
