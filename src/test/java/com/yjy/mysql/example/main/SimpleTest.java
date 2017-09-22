package com.yjy.mysql.example.main;

import com.yjy.mysql.dialect.MYSQL5Dialect;
import org.apache.log4j.PropertyConfigurator;

/**
 * 测试类
 * Created by yjy on 2017/9/22.
 */
public class SimpleTest {

    public static void main(String[] args) {

        // 加载Log4j配置
        PropertyConfigurator.configure("test/config/log4j.properties");

        // 初始化数据库
        new MYSQL5Dialect("test/config/db.properties").init();
    }

}
