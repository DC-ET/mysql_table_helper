package com.zoi7.mysql;

import com.zoi7.mysql.config.Config;
import com.zoi7.mysql.config.DataConfig;
import com.zoi7.mysql.dialect.MYSQL5Dialect;

import javax.naming.ConfigurationException;
import java.util.Properties;

/**
 * 初始化入口
 *
 * @author yjy
 * 2018-05-14 13:10
 */
public class TableInitializer {

    // way1
    public static void init(String configPath) throws ConfigurationException {
        // 加载配置参数
        Config.loadConfig(configPath);
        new MYSQL5Dialect().init();
    }

    // way2
    public static void init(Properties properties) throws ConfigurationException {
        // 加载配置参数
        Config.loadConfig(properties);
        new MYSQL5Dialect().init();
    }

    // way3
    public static void init(DataConfig config) {
        // 加载配置参数
        Config.loadConfig(config);
        new MYSQL5Dialect().init();
    }

}
