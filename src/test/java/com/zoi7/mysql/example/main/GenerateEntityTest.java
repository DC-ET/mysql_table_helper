package com.zoi7.mysql.example.main;

import com.zoi7.mysql.util.generate.EntitiesGenerator;
import com.zoi7.mysql.util.mybatis.DefaultMapperUtils;
import com.zoi7.mysql.util.mybatis.MapperConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 根据数据库表结构自动生成表实体
 * @author yjy
 * 2018-08-14 12:55
 */
public class GenerateEntityTest {

    public static void main(String[] args) throws IOException {
        String outputPath = "D:/JavaWorks/maven/mysql_table_helper/target/entity/"; // 文件输出地址
        String entityPackage = "com.zoi7.mysql.example.entity"; // 生成的实体类所属包
        String url = "jdbc:mysql://localhost:3306"; // 数据库地址
        String driverName = "com.mysql.jdbc.Driver"; // driver
        String database = "test"; // 数据库
        String username = "root"; // 用户
        String password = "1234"; // 密码
        String superEntityClass = "com.zoi7.mysql.example.base.BaseEntity"; // 实体基类
        String author = "yjy"; // 生成的类注释上的作者名字
        EntitiesGenerator gen = new EntitiesGenerator(outputPath, entityPackage, url, driverName, database, username, password, superEntityClass, author);
        // 注释注解
        gen.setDisableAnnotation(true);
        gen.generate();
    }

}
