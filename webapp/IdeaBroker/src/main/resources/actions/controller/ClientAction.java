package actions.controller;

import com.opensymphony.xwork2.ActionSupport;
import actions.model.Client;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Class which all other Actions are meant to extend. This class encapsulates the session handling logic. It does
 * this by storing the current client (represented by a Client object) in the current session,
 * and either creating it or reading it as necessary. Child classes are expected to call super.execute() to maintain
 * class state as the first step of their own execute() methods. This means that all child classes will eventually
 * invoke getClientSession().
 *
 * A session variable is exposed to children classes, as well as the client object,
 * which is guaranteed by ClientAction to always be valid *IF* they comply and call ClientAction's execute method.
 */
public abstract class ClientAction extends ActionSupport implements SessionAware, ServletResponseAware,
        ServletRequestAware {

    private static final String COOKIE_NAME        = "IdeaBrokerEncodedUid";
    private static final String INVALID_ENCODEDUID = "-1";

    protected Map<String, Object> session;
    protected Client              client;
    protected HttpServletResponse servletResponse;
    protected HttpServletRequest  servletRequest;

    /**
     * Needed by SessionAware
     * @param session Used by struts to set the session
     */
    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    /**
     * Synchronizes this.client and the current session, either creating it if session doesn't have it,
     * or loading it from the session if it exists.
     */
    private void getClientSession() {
        if ( !session.containsKey("client") ) {
            this.client = new Client();
            if ( !readCookie() ) {
                //Logged with encodeduserid
                //client.doLogin("Hakuna", "Matata"); //FIXME: HACKED IN to make our lives easier
            }
            session.put("client", client);
        } else {
            this.client = (Client) session.get("client");
        }
        System.out.println("getClientSession(): " + client.getUid());
        writeCookie();
    }

    boolean readCookie() {
        if ( servletRequest.getCookies() != null )
            for ( Cookie c : servletRequest.getCookies() ) {
                if ( c.getName().equals(COOKIE_NAME) )
                    if ( c.getValue() != INVALID_ENCODEDUID )
                        return client.loginWithEncodedUid(c.getValue());
            }
        return false;
    }

    private void writeCookie() {
        Cookie c = new Cookie(COOKIE_NAME, client.getEncodedUid());
        c.setMaxAge(60*60*24*365); // Make the cookie last a year
        servletResponse.addCookie(c);
    }

    /**
     * Action's execute method, called whenever the action is triggered.
     * @return A String object, informing the success or failure of the operation.
     * @throws Exception    Throws an exception, in case of an error occurrs when accessing to the database.
     */
    public final String execute() throws Exception {
        getClientSession();
        if ( !(this instanceof RegisterAction) && !(this instanceof RegisterWithFacebookAction) ) {
            if ( this.client.getUid() == -1 )
                return ERROR;
        }

        return doWork();
    }

    public abstract String doWork();

    @Override
    public void setServletResponse(javax.servlet.http.HttpServletResponse httpServletResponse) {
        this.servletResponse = httpServletResponse;
    }

    @Override
    public void setServletRequest(HttpServletRequest httpServletRequest) {
        this.servletRequest = httpServletRequest;
    }
}
