package com.zoi7.mysql.example.main;

import com.zoi7.mysql.util.mybatis.DefaultMapperUtils;
import com.zoi7.mysql.util.mybatis.MapperConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yjy
 * 2018-08-14 12:55
 */
public class GenerateXmlTest {

    public static void main(String[] args) throws IOException {
        // 表实体所在包
        String entityPackage = "com.zoi7.mysql.example.entity";
        // Mapper接口所在包, Mapper接口需要自己写
        String mapperPackage = "com.zoi7.mysql.example.mapper";
        // 指定 mapper.xml 文件保存位置, 需要以 \ 或 / 结尾
        String xmlOutPut= "D:\\JavaWorks\\maven\\mysql_table_helper\\target\\";

        // 使用默认的工具进行生成
        Map<String, String> customCachePros = new HashMap<String, String>();
        customCachePros.put("interval", "15000");
        MapperConfig config = new MapperConfig(entityPackage, mapperPackage, xmlOutPut, "com.zoi7.mysql.CustomCacheClass", customCachePros);
        config.setUppercase(true);
        DefaultMapperUtils.INSTANCE.makeAll(config);

        // 使用自定义的方式生成
//        CustomMapperConfig mapperConfig = new CustomMapperConfig(entityPackage, mapperPackage, xmlOutPut, false);
//        mapperConfig.setCreateDelete(false);
//        CustomMapperUtils.INSTANCE.makeAll(mapperConfig);
    }

}
