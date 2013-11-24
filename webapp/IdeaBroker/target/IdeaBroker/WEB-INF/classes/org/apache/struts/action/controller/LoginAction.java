package org.apache.struts.action.controller;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: jorl17 Date: 24/11/13 Time: 02:04 To change this template use File | Settings |
 * File Templates.
 */
public class LoginAction extends ActionSupport implements SessionAware {
    private Map<String, Object> session;

    private String username, password;
    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public String execute() throws Exception {
        // TODO: Login e guardar na sessão
        return SUCCESS;
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
