package com.zoi7.mysql.util.generate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yjy
 * @date 2019/10/12 10:36
 **/
public class SqlHelper {
    private String url;
    private String username;
    private String password;

    private Connection connection;

    private String driverName;

    public SqlHelper(String url, String driverName, String username, String password) {
        this.url = url;
        this.driverName = driverName;
        this.username = username;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> get(String sql, String columnName) {
        List<T> result = new ArrayList<T>();
        Statement statement = null;
        try {
            statement = getStatement();
            ResultSet set = statement.executeQuery(sql);
            while (set.next()) {
                result.add((T) set.getObject(columnName));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeStatement(statement);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public Statement getStatement() throws ClassNotFoundException, SQLException{
        Class.forName(driverName);
        Connection con = DriverManager.getConnection(url, username, password);
        Statement statement = con.createStatement();
        return statement;
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        if (connection == null) {
            Class.forName(driverName);
            connection = DriverManager.getConnection(url, username, password);
        }

        return connection;
    }

    public void closeConnection(Connection conn) throws ClassNotFoundException, SQLException {
        if (connection != null) {
            connection.close();
        }

        if (conn != null) {
            conn.close();
        }

        System.out.println("-----------Connection closed now-----------");
    }

    public void closeStatement(Statement statement) throws SQLException {
        if (statement != null) {
            Connection con = statement.getConnection();
            statement.close();
            if (con != null) {
                con.close();
            }
        }
    }
}