package rmiserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class ConnectionPool {

    // Free connections
    private ArrayList<Connection> connections           = new ArrayList<Connection>();

    // We keep track of the checkedOut connections in case we want to eventually check on them to free them
    private ArrayList<Connection> checkedOutConnections = new ArrayList<Connection>();

    private String url;
    private String uname;
    private String pwd;


    public ConnectionPool(String url,String uname, String pwd) throws SQLException {
        try { Class.forName("oracle.jdbc.driver.OracleDriver"); } catch (ClassNotFoundException ignored) {}
        this.url = url;
        this.uname = uname;
        this.pwd = pwd;

        int startNumConnections = 5;
        for (int count = 0; count < startNumConnections; count++)
            connections.add(getConnection());
    }


    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, uname, pwd);
    }


    public synchronized Connection checkOutConnection() throws SQLException {
        Connection newConnection;

        if (connections.size() == 0) {
            newConnection = getConnection();
            connections.add(newConnection);
        } else {
            newConnection = connections.get(0);
            connections.remove(0);
        }

        checkedOutConnections.add(newConnection);
        return newConnection;
    }

    public synchronized void returnConnection(Connection c) {
        connections.add(c);
        checkedOutConnections.remove(c);
    }
}
