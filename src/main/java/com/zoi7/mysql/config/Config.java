package com.zoi7.mysql.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.ConfigurationException;
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

    private static final Logger log = LoggerFactory.getLogger(Config.class);

    // 数据库配置信息
    public static DataConfig config;

    /**
     * 加载自定义配置
     * @param config 配置
     */
    public static void loadConfig(DataConfig config) {
        Config.config = config;
        log.info("Config loaded. {}", config);
    }

    /**
     * 从配置文件加载配置
     * @param configPath 配置文件地址
     */
    public static void loadConfig(String configPath) throws ConfigurationException {
        // 表结构更新配置
        Properties tablePros = getProperties(configPath);
        loadConfig(tablePros);
    }

    /**
     * 加载配置
     * @param tablePros 配置信息
     */
    public static void loadConfig(Properties tablePros) throws ConfigurationException {
        String dbDriverName = tablePros.getProperty("db.driver");
        String dbUrl = tablePros.getProperty("db.url");
        String dbUsername = tablePros.getProperty("db.username");
        String dbPassword = tablePros.getProperty("db.password");
        String packages = tablePros.getProperty("db.packages");
        String type = tablePros.getProperty("db.auto");
        String showSql = tablePros.getProperty("db.showSql");
        DataConfig config = new DataConfig(dealPackages(packages), dbUrl.trim(), dbUsername.trim(), dbPassword.trim(),
                type.trim(), Boolean.parseBoolean(showSql.trim()), dbDriverName.trim());
        loadConfig(config);
    }

    /**
     * 加载扫描包配置
     * @param packages 配置的扫描包
     */
    private static String[] dealPackages(String packages) throws ConfigurationException{
        String[] packs;
        if (packages.contains(",")) {
            Set<String> set = new HashSet<String>();
            for (String package1 : packages.split(",")) {
                if ((package1 = package1.trim()).length() > 0)
                    set.add(package1);
            }
            packs = set.toArray(new String[0]);
        } else if (!"".equals(packages.trim())) {
            packs = new String[]{packages.trim()};
        } else {
            throw new ConfigurationException("can not resolve config packages" + packages);
        }
        return packs;
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
