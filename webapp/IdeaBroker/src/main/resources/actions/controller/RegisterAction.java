package actions.controller;

import org.apache.commons.lang.xwork.StringEscapeUtils;

/**
 * This action is used when the user tries to registerWithFacebook. When execute() is called, the username, password and email are
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
     * Try to registerWithFacebook the user with the username, password and email provided. Note the call to super.execute() which is
     * required by ClientAction.
     * @return              If the registration was successfull, returns SUCCESS. Otherwise returns ERROR.
     * @throws Exception    Throws an exception, in case of an error occurrs when accessing to the database
     */
    public String doWork() {

        if ( client.doRegister(StringEscapeUtils.escapeSql(username), StringEscapeUtils.escapeSql(password),
                               StringEscapeUtils.escapeSql(email)) )
            return SUCCESS;
        else
            return ERROR;

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

    /**
     * Gets the email of the given user.
     * @return  A String object, containing the email of the given user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets a new email for the user.
     * @param email  The new user's email.
     */
    public void setEmail(String email) {
        this.email = email;
    }
}