package com.zoi7.mysql.example.main;

import com.zoi7.mysql.TableInitializer;
import com.zoi7.mysql.analysis.ScanJar;
import com.zoi7.mysql.config.DataConfig;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

import javax.naming.ConfigurationException;
import java.io.IOException;
import java.util.Set;

/**
 * 测试类
 * Created by yjy on 2017/9/22.
 */
public class SimpleTest {

    @Test
    public void test1() throws IOException, ConfigurationException {

//        // 加载Log4j配置
        String log4jPath = this.getClass().getResource("/config/log4j.properties").getPath();
        PropertyConfigurator.configure(log4jPath);
//
//        // 1.以标准配置文件的方式调用初始化
//        String dbPropertiesPath = this.getClass().getResource("/config/db.properties").getPath();
//        TableInitializer.init(dbPropertiesPath);

//        // 2.组合现有的配置调用初始化
//        String otherPropertiesPath = this.getClass().getResource("/config/other-db.properties").getPath();
//        Properties otherProperties = new Properties();
//        otherProperties.load(new FileInputStream(otherPropertiesPath));
//
//        Properties properties = new Properties();
//        properties.setProperty("db.url", otherProperties.getProperty("url"));
//        properties.setProperty("db.username", otherProperties.getProperty("username"));
//        properties.setProperty("db.password", otherProperties.getProperty("password"));
//        properties.setProperty("db.driver", otherProperties.getProperty("driver"));
//        properties.setProperty("db.packages", otherProperties.getProperty("packages"));
//        properties.setProperty("db.auto", otherProperties.getProperty("auto"));
//        properties.setProperty("db.showSql", otherProperties.getProperty("showSql"));
//        properties.setProperty("db.uppercase", otherProperties.getProperty("uppercase"));
//        TableInitializer.init(properties);


        // 3.自定义配置 (优势: 无需配置文件, 简单明了)
        String[] packages = new String[]{"com.zoi7.mysql.example"};
        String url = "jdbc:mysql://127.0.0.1:3306/table_helper_test?useUnicode=true&serverTimezone=GMT&allowMultiQueries=true";
        String username = "yjy";
        String password = "yyyyyy";
        boolean showSql = true;
        boolean uppercase = true;
        String type = DataConfig.TYPE_UPDATE;
        DataConfig config = new DataConfig(packages, url, username, password, type, showSql);
        config.setUppercase(uppercase);
        TableInitializer.init(config);
    }

    // 测试扫描jar包
    @Test
    public void testScanJar() throws IOException, ClassNotFoundException {
        // 加载Log4j配置
        String log4jPath = this.getClass().getResource("/config/log4j.properties").getPath();
        PropertyConfigurator.configure(log4jPath);
        Set<Class<?>> set = ScanJar.getClassesByPackageName("org.apache.log4j.config");
        for (Class<?> clazz : set) {
            System.out.println(clazz);
        }
    }


}
