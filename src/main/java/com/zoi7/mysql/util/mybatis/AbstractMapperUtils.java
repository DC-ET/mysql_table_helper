package com.zoi7.mysql.util.mybatis;

import com.zoi7.mysql.analysis.ScanPackage;
import com.zoi7.mysql.comment.Entity;
import com.zoi7.mysql.comment.FieldType;
import com.zoi7.mysql.util.FieldUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * @author yjy
 * 2018-08-14 13:17
 */
public abstract class AbstractMapperUtils<T extends MapperConfig> {

    public void makeAll(T config) throws IOException {
        Set<Class<?>> classSet = ScanPackage.getClassesByPackageName(config.getEntityPackage());
        System.out.println(classSet.size());
        for (Class<?> clazz : classSet) {
            doMakeXml(clazz, config);
        }
    }

    private void doMakeXml(Class<?> clazz, T config) throws IOException {
        if (clazz.isAnnotationPresent(Entity.class)) {
            Entity entity = clazz.getAnnotation(Entity.class);
            if (entity != null && entity.check()) {
                String xml = getXmlString(clazz, config);
                String fileName = clazz.getSimpleName() + "Mapper.xml";
                createFile(xml, fileName, config.getXmlOutPut());
            }
        }
    }

    /**
     * 组装对应的 xml 信息
     * 可通过继承此类并重写此方法来自定义 xml 生成的内容
     * @param clazz 实体类
     * @param config 配置
     * @return ..
     */
    protected abstract String getXmlString(Class<?> clazz, T config);

    private static void createFile(String xml, String fileName, String xmlOutPut) throws IOException {
        if (!xmlOutPut.endsWith("\\") && !xmlOutPut.endsWith("/")) {
            xmlOutPut += "\\";
        }
        File xmlFile = new File(xmlOutPut + fileName);
        FileWriter writer = new FileWriter(xmlFile);
        writer.write(xml);
        writer.flush();
        writer.close();
    }

    /**
     * 根据Mysql类型获取jdbc类型
     * @param field 属性
     * @return jdbc类型
     */
    public static String getJdbcType(Field field) {
        FieldType fieldType = FieldUtils.getType(field);
        if (fieldType == FieldType.DATETIME) {
            return "TIMESTAMP";
        }
        if (fieldType == FieldType.TINYTEXT || fieldType == FieldType.MEDIUMTEXT || fieldType == FieldType.TEXT || fieldType == FieldType.LONGTEXT) {
            return "VARCHAR";
        }
        return fieldType.toString();
    }

}
