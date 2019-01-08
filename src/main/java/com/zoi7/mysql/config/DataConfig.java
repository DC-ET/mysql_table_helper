package com.zoi7.mysql.config;

import java.io.Serializable;
import java.util.Arrays;

/**
 *
 * @author yjy
 * 2018-06-07 14:19
 */
public class DataConfig implements Serializable {

    public static final String TYPE_NONE = "none"; // 新建表, 如果存在旧表, 则删除
    public static final String TYPE_CREATE = "create"; // 新建表, 如果存在旧表, 则删除
    public static final String TYPE_UPDATE = "update"; // 更新表结构, 如果不存在表, 则创建

//    public static final String DEFAULT_DRIVER = "com.mysql.jdbc.Driver";
    public static final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String DEFAULT_TYPE = TYPE_UPDATE;
    public static final boolean DEFAULT_SHOW_SQL = true;

    private String driver; // 驱动
    private String url; // 数据库连接地址
    private String username; // 用户名
    private String password; // 密码
    private String[] packages; // 实体类所在包, 将包含子包
    private String type; // 更新类型
    private boolean showSql; // 是否打印执行的SQL
    private boolean scanJar = true; // 是否扫描jar中的实体
    private boolean uppercase = false; // 表字段是否大写, 默认小写

    public DataConfig(String[] packages, String url, String username, String password) {
        this(packages, url, username, password, DEFAULT_TYPE, DEFAULT_SHOW_SQL, DEFAULT_DRIVER);
    }

    public DataConfig(String[] packages, String url, String username, String password, String type) {
        this(packages, url, username, password, type, DEFAULT_SHOW_SQL, DEFAULT_DRIVER);
    }

    public DataConfig(String[] packages, String url, String username, String password, boolean showSql) {
        this(packages, url, username, password, DEFAULT_TYPE, showSql, DEFAULT_DRIVER);
    }

    public DataConfig(String[] packages, String url, String username, String password, String type, boolean showSql) {
        this(packages, url, username, password, type, showSql, DEFAULT_DRIVER);
    }

    public DataConfig(String[] packages, String url, String username, String password, String type, boolean showSql,
                      String driver) {
        this(packages, url, username, password, type, showSql, driver, true);
    }

    public DataConfig(String[] packages, String url, String username, String password, String type, boolean showSql,
                      String driver, boolean scanJar) {
        this(packages, url, username, password, type, showSql, driver, scanJar, false);
    }

    public DataConfig(String[] packages, String url, String username, String password, String type, boolean showSql,
                      String driver, boolean scanJar, boolean uppercase) {
        this.packages = packages;
        this.url = url;
        this.username = username;
        this.password = password;
        this.type = type;
        this.showSql = showSql;
        this.driver = driver;
        this.scanJar = scanJar;
        this.uppercase = uppercase;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
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

    public String[] getPackages() {
        return packages;
    }

    public void setPackages(String[] packages) {
        this.packages = packages;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isShowSql() {
        return showSql;
    }

    public void setShowSql(boolean showSql) {
        this.showSql = showSql;
    }

    public boolean isUppercase() {
        return uppercase;
    }

    public void setUppercase(boolean uppercase) {
        this.uppercase = uppercase;
    }

    public boolean isScanJar() {
        return scanJar;
    }

    public void setScanJar(boolean scanJar) {
        this.scanJar = scanJar;
    }

    @Override
    public String toString() {
        return "DataConfig{" +
                "driver='" + driver + '\'' +
                ", url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", packages=" + Arrays.toString(packages) +
                ", type='" + type + '\'' +
                ", showSql=" + showSql +
                ", scanJar=" + scanJar +
                ", uppercase=" + uppercase +
                '}';
    }
}
