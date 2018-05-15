package com.yjy.mysql;

import com.yjy.mysql.config.Config;
import com.yjy.mysql.dialect.MYSQL5Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * 初始化入口
 *
 * @author yjy
 * 2018-05-14 13:10
 */
public class TableInitializer {

    private static final Logger log = LoggerFactory.getLogger(TableInitializer.class);

    public static void init(String configPath) {
        // 加载配置参数
        Config.loadConfig(configPath);
        new MYSQL5Dialect().init();
    }

    public static void init(Properties properties) {
        // 加载配置参数
        Config.loadConfig(properties);
        new MYSQL5Dialect().init();
    }

}
