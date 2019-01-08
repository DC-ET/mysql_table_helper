package com.zoi7.mysql.example.mybatis;

import com.zoi7.mysql.comment.Entity;
import com.zoi7.mysql.comment.Id;
import com.zoi7.mysql.util.FieldUtils;
import com.zoi7.mysql.util.mybatis.AbstractMapperUtils;

import java.lang.reflect.Field;

import static com.zoi7.mysql.util.FieldUtils.getColumn;

/**
 *
 * 继承 MapperUtils 类, 并重写器 getXmlString 方法, 已达到自定义 xml 内容的目的
 * @author yjy
 * 2018-08-14 13:05
 */
public class CustomMapperUtils extends AbstractMapperUtils<CustomMapperConfig> {

    public static CustomMapperUtils INSTANCE = new CustomMapperUtils();

    @Override
    protected String getXmlString(Class<?> clazz, CustomMapperConfig config) {
        Entity entity = clazz.getAnnotation(Entity.class);
        String tableName = entity.tableName();
        String clazzSimpleName = clazz.getSimpleName();
        String clazzPackage = clazz.getPackage().toString();
        Field[] fields = clazz.getDeclaredFields();
        String[] packs = clazzPackage.split("entity");
        String pack = packs.length > 1 ? packs[1] : "";
        String namespace = config.getMapperPackage() + pack + "." + clazzSimpleName + "Mapper";

        Field idField = null;
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?> \n");
        xml.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \n");
        xml.append("\t\"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n\n");

        xml.append("<mapper namespace=\"").append(namespace).append("\">\n\n");

        xml.append("\t<sql id=\"table_name\" > ").append(tableName).append(" </sql> \n\n");

        if (config.isUseCache()) {
            String cacheClass = config.getCacheClass();
            if (cacheClass != null) {
                xml.append("\t<!-- 启用二级缓存, 注意: 涉及到关联查询的sql需要标注 useCache=\"false\" -->\n");
                xml.append("\t<cache type=\"").append(cacheClass).append("\" /> \n\n");
            } else {
                long cacheTime = config.getCacheTime();
                xml.append("\t<!-- 启用二级缓存, 注意: 涉及到关联查询的sql需要标注 useCache=\"false\" -->\n");
                if (cacheTime > 0) {
                    xml.append("\t<cache flushInterval=\"").append(cacheTime).append("\" /> \n\n");
                }
            }
        }

        xml.append("\t<parameterMap id=\"parameterType\" type=\"").append(clazz.getName()).append("\" />\n\n");

        xml.append("\t<resultMap id=\"resultList\" type=\"").append(clazzSimpleName).append("\">\n");
        for (Field field : fields) {
            if (field.isAnnotationPresent(com.zoi7.mysql.comment.Field.class)) {
                if (field.isAnnotationPresent(Id.class)) {
                    idField = field;
                    xml.append("\t\t<id ");
                } else {
                    xml.append("\t\t<result ");
                }
                xml.append("column=\"").append(getColumn(field, config.isUppercase()))
                        .append("\" property=\"").append(field.getName())
                        .append("\" jdbcType=\"").append(getJdbcType(field)).append("\"/>\n");
            }
        }
        xml.append("\t</resultMap>\n\n");

        xml.append("\t<insert id=\"save\" parameterMap=\"parameterType\" ");
        if (idField != null && FieldUtils.isAutoIncrease(idField)) {
            xml.append("useGeneratedKeys=\"true\" keyColumn=\"").append(getColumn(idField, config.isUppercase())).append("\"")
                    .append(" keyProperty=\"").append(idField.getName()).append("\" ");
        }
        xml.append(">\n");
        xml.append("\t\tinsert into <include refid=\"table_name\"/>\n");
        xml.append("\t\t\t(");
        boolean first = true;
        for (Field field : fields) {
            if (field.isAnnotationPresent(com.zoi7.mysql.comment.Field.class)) {
                if (!FieldUtils.isAutoIncrease(field)) {
                    if (!first) {
                        xml.append(", ");
                    } else {
                        first = false;
                    }
                    xml.append(getColumn(field, config.isUppercase()));
                }
            }
        }
        xml.append(") \n");
        xml.append("\t\tvalues (\n");
        xml.append("\t\t\t");
        first = true;
        for (Field field : fields) {
            if (field.isAnnotationPresent(com.zoi7.mysql.comment.Field.class)) {
                if (!FieldUtils.isAutoIncrease(field)) {
                    if (!first) {
                        xml.append(", ");
                    } else {
                        first = false;
                    }
                    xml.append("#{").append(field.getName()).append("}");
                }
            }
        }
        xml.append("\n\t\t)\n");
        xml.append("\t</insert>\n\n");

        if (idField != null) {
            xml.append("\t<update id=\"update\" parameterMap=\"parameterType\" >\n");
            xml.append("\t\tupdate\n");
            xml.append("\t\t<include refid=\"table_name\"/>\n");
            xml.append("\t\t<set>");
            first = true;
            for (Field field : fields) {
                if (field.isAnnotationPresent(com.zoi7.mysql.comment.Field.class)) {
                    if (!FieldUtils.isAutoIncrease(field)) {
                        if (!first) {
                            xml.append(", ");
                        } else {
                            first = false;
                        }
                        xml.append("\n\t\t\t").append(getColumn(field, config.isUppercase()));
                        xml.append(" = #{").append(field.getName()).append("}");
                    }
                }
            }
            xml.append("\n\t\t</set>\n");
            xml.append("\t\t<where>\n");
            xml.append("\t\t\t").append(getColumn(idField, config.isUppercase())).append(" = #{").append(idField.getName()).append("} \n");
            xml.append("\t\t</where>\n");
            xml.append("\t</update>\n\n");

            if (config.isCreateDelete()) {
                xml.append("\t<delete id=\"delete\" parameterType=\"long\" >\n");
                xml.append("\t\tdelete from <include refid=\"table_name\"/> \n ");
                xml.append("\t\t<where>\n");
                xml.append("\t\t\t").append(getColumn(idField, config.isUppercase())).append(" = #{param1}\n");
                xml.append("\t\t</where>\n");
                xml.append("\t</delete>\n\n");
            }

            xml.append("\t<select id=\"findById\" parameterType=\"long\" resultMap=\"resultList\" >\n");
            xml.append("\t\tselect * from <include refid=\"table_name\"/> \n");
            xml.append("\t\t<where>\n");
            xml.append("\t\t\t").append(getColumn(idField, config.isUppercase())).append(" = #{param1}\n");
            xml.append("\t\t</where>\n");
            xml.append("\t</select>\n\n");

        } else {
            System.err.println("isField is not appoint!");
        }

        xml.append("\t<select id=\"findList\" parameterMap=\"parameterType\" resultMap=\"resultList\">\n");
        xml.append("\t\tselect * from <include refid=\"table_name\"/>\n");
        xml.append("\t\t<where>\n");
        xml.append("\t\t\t1=1\n");
        for (Field field : fields) {
            if (field.isAnnotationPresent(com.zoi7.mysql.comment.Field.class)) {
                xml.append("\t\t\t<if test=\"").append(field.getName()).append(" != null\">");
                xml.append(" and ").append(getColumn(field, config.isUppercase())).append(" = #{").append(field.getName()).append("} ");
                xml.append(" </if>\n");
            }
        }
        xml.append("\t\t</where>\n");
        if (config.getMaxLimit() > 0) {
            xml.append("\t\tlimit 0,").append(config.getMaxLimit()).append(" \n");
        }
        xml.append("\t</select>\n\n");

        xml.append("\t<select id=\"findAll\" resultMap=\"resultList\">\n");
        xml.append("\t\tselect * from <include refid=\"table_name\"/>\n");
        if (config.getMaxLimit() > 0) {
            xml.append("\t\tlimit 0,").append(config.getMaxLimit()).append(" \n");
        }
        xml.append("\t</select>\n\n");

        xml.append("\t<select id=\"findPage\" resultMap=\"resultList\">\n");
        xml.append("\t\tselect * from <include refid=\"table_name\"/>\n");
        xml.append("\t\t<where>\n");
        xml.append("\t\t\t1=1\n");
        for (Field field : fields) {
            if (field.isAnnotationPresent(com.zoi7.mysql.comment.Field.class)) {
                xml.append("\t\t\t<if test=\"param3.").append(field.getName()).append(" != null\">");
                xml.append(" and ").append(getColumn(field, config.isUppercase())).append(" = #{param3.").append(field.getName()).append("} ");
                xml.append(" </if>\n");
            }
        }
        xml.append("\t\t</where>\n");
        xml.append("\t\tlimit #{param1},#{param2}\n");
        xml.append("\t</select>\n\n");

        if (idField != null) {
            xml.append("\t<select id=\"findCount\" parameterMap=\"parameterType\" resultType=\"int\">\n");
            xml.append("\t\tselect count(").append(getColumn(idField, config.isUppercase())).append(") from <include refid=\"table_name\"/>\n");
            xml.append("\t\t<where>\n");
            xml.append("\t\t\t1=1\n");
            for (Field field : fields) {
                if (field.isAnnotationPresent(com.zoi7.mysql.comment.Field.class)) {
                    xml.append("\t\t\t<if test=\"").append(field.getName()).append(" != null\">");
                    xml.append(" and ").append(getColumn(field, config.isUppercase())).append(" = #{").append(field.getName()).append("} ");
                    xml.append(" </if>\n");
                }
            }
            xml.append("\t\t</where>\n");
            xml.append("\t</select>\n\n");
        } else {
            System.err.println("isField is not appoint!");
        }

        xml.append("\t<select id=\"findAllCount\" resultType=\"int\">\n");
        xml.append("\t\tselect count(*) from <include refid=\"table_name\"/>\n");
        xml.append("\t</select>\n\n");

        xml.append("</mapper>");
        return xml.toString();
    }

}
