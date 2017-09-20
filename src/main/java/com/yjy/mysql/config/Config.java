package com.yjy.mysql.config;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * 获取配置信息
 * Created by yjy on 2017/9/20.
 */
public class Config {

    private static final Logger log = Logger.getLogger(Config.class);

    // 数据库配置信息
    public static String DB_DRIVER_NAME = "com.mysql.jdbc.Driver"; // 驱动
    public static String DB_URL; // 数据库地址
    public static String DB_USERNAME; // 用户
    public static String DB_PASSWORD; // 密码

    public static String[] DB_PACKAGES; // 扫描包
    public static String DB_AUTO = "update"; // 更新方式
    public static boolean DB_SHOW_SQL = true; // 是否打印sql语句

    public static void loadConfig(String configPath) {
        // 表结构更新配置
        Properties tablePros = getProperties(configPath);
        String dbDriverName = tablePros.getProperty("db.driver");
        String dbUrl = tablePros.getProperty("db.url");
        String dbUsername = tablePros.getProperty("db.username");
        String dbPassword = tablePros.getProperty("db.password");
        String packages = tablePros.getProperty("db.packages");
        String auto = tablePros.getProperty("db.auto");
        String showSql = tablePros.getProperty("db.showSql");
        if (dbDriverName != null)
            DB_DRIVER_NAME = dbDriverName.trim();
        if (dbUrl != null)
            DB_URL = dbUrl.trim();
        if (dbUsername != null)
            DB_USERNAME = dbUsername.trim();
        if (dbPassword != null)
            DB_PASSWORD = dbPassword.trim();
        if (packages != null)
            dealPackages(packages);
        if (auto != null)
            DB_AUTO = auto.trim();
        if (showSql != null)
            DB_SHOW_SQL = Boolean.parseBoolean(showSql.trim());
    }

    /**
     * 加载扫描包配置
     * @param packages 配置的扫描包
     */
    private static void dealPackages(String packages) {
        if (packages.contains(",")) {
            Set<String> set = new HashSet<String>();
            for (String package1 : packages.split(",")) {
                if ((package1 = package1.trim()).length() > 0)
                    set.add(package1);
            }
            DB_PACKAGES = set.toArray(new String[]{});
        } else
            DB_PACKAGES = new String[]{packages.trim()};
    }

    /**
     * 获取取配置文件
     *
     * @return properties
     */
    private static Properties getProperties(String filePath) {
        Properties properties = new Properties();
        try {
            FileInputStream inputStream = getFileInputStream(filePath);
            properties.load(inputStream);
            inputStream.close();
        } catch (FileNotFoundException e1) {
            log.error("gatProperties failed, cause file not found", e1);
        } catch (Exception e2) {
            log.error("getProperties throw an error", e2);
        }
        return properties;
    }

    /**
     * 获取配置文件输入流
     * @param filePath 配置文件地址
     * @return 输入流
     * @throws FileNotFoundException 找不到文件
     */
    private static FileInputStream getFileInputStream(String filePath) throws FileNotFoundException {
        return new FileInputStream(filePath);
    }


}
