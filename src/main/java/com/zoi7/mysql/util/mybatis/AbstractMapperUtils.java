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
            String xml = getXmlString(clazz, config);
            String fileName = clazz.getSimpleName() + "Mapper.xml";
            createFile(xml, fileName, config.getXmlOutPut());
        }
    }

    protected abstract String getXmlString(Class<?> clazz, T config);

    private static void createFile(String xml, String fileName, String xmlOutPut) throws IOException {
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
        String jdbcType ;
        FieldType fieldType = FieldUtils.getType(field);
        switch (fieldType) {
            case DATETIME:
                jdbcType = "TIMESTAMP"; break;
            case DATE:
                jdbcType = "DATA"; break;
            case BIGINT:
                jdbcType = "BIGINT"; break;
            case INTEGER:
                jdbcType = "INTEGER"; break;
            case VARCHAR:
                jdbcType = "VARCHAR"; break;
            case TINYINT:
                jdbcType = "TINYINT"; break;
            case DOUBLE:
                jdbcType = "DOUBLE"; break;
            case FLOAT:
                jdbcType = "FLOAT"; break;
            case DECIMAL:
                jdbcType = "DECIMAL"; break;
            case TIME:
                jdbcType = "TIME"; break;
            case TIMESTAMP:
                jdbcType = "TIMESTAMP"; break;
            default:
                jdbcType = "VARCHAR"; break;
        }
        return jdbcType;
    }

}
