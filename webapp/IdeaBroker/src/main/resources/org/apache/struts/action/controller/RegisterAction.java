package org.apache.struts.action.controller;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: joaquim
 * Date: 24/11/13
 * Time: 22:24
 * To change this template use File | Settings | File Templates.
 */
public class RegisterAction extends ActionSupport implements SessionAware {
    private Map<String, Object> session;
    private String username;
    private String password;
    private String email;
    private Date regDate;

    public String execute() throws Exception {
        //TODO pedir dados e guarda-los
        regDate = new Date();//FIXME: A data nao esta bem aqui
        return SUCCESS;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session=session;
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

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }
}
