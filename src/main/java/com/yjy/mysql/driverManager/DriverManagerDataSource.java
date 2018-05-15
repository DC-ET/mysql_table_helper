package com.yjy.mysql.driverManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * edit on org.springframework.jdbc.datasource.DriverManagerDataSource
 * see {org.springframework.jdbc.datasource.DriverManagerDataSource}
 * Created by yjy on 2017/9/20.
 */
public class DriverManagerDataSource extends AbstractDataSource {

    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private Properties connectionProperties;

    public DriverManagerDataSource() {
    }

    public DriverManagerDataSource(String driverClassName, String url, String username, String password) {
        this.setDriverClassName(driverClassName);
        this.setUrl(url);
        this.setUsername(username);
        this.setPassword(password);
    }

    public DriverManagerDataSource(String url, String username, String password) {
        this.setUrl(url);
        this.setUsername(username);
        this.setPassword(password);
    }

    public DriverManagerDataSource(String url) {
        this.setUrl(url);
    }

    public void setDriverClassName(String driverClassName) {
        if("".equals(driverClassName.trim())) {
            throw new IllegalArgumentException("driverClassName must not be empty");
        } else {
            this.driverClassName = driverClassName.trim();
            try {
                Class.forName(this.driverClassName);
            } catch (ClassNotFoundException var3) {
                logger.error("Could not load JDBC driver class [" + this.driverClassName + "]", var3);
            }

            if(this.logger.isInfoEnabled()) {
                this.logger.info("Loaded JDBC driver: " + this.driverClassName);
            }

        }
    }

    public String getDriverClassName() {
        return this.driverClassName;
    }

    public void setUrl(String url) {
        if("".equals(url.trim())) {
            throw new IllegalArgumentException("url must not be empty");
        } else {
            this.url = url.trim();
        }
    }

    public String getUrl() {
        return this.url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public void setConnectionProperties(Properties connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    public Properties getConnectionProperties() {
        return this.connectionProperties;
    }

    public Connection getConnection() throws SQLException {
        return this.getConnectionFromDriverManager();
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return this.getConnectionFromDriverManager(username, password);
    }

    protected Connection getConnectionFromDriverManager() throws SQLException {
        return this.getConnectionFromDriverManager(this.getUsername(), this.getPassword());
    }

    protected Connection getConnectionFromDriverManager(String username, String password) throws SQLException {
        Properties props = new Properties(this.getConnectionProperties());
        if(username != null) {
            props.setProperty("user", username);
        }

        if(password != null) {
            props.setProperty("password", password);
        }

        return this.getConnectionFromDriverManager(this.getUrl(), props);
    }

    protected Connection getConnectionFromDriverManager(String url, Properties props) throws SQLException {
        if(this.logger.isDebugEnabled()) {
            this.logger.debug("Creating new JDBC Connection to [" + url + "]");
        }
        return DriverManager.getConnection(url, props);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public Logger getParentLogger() {
        return null;
    }
}
