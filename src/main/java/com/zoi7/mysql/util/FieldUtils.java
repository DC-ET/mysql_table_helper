package com.zoi7.mysql.util;

import com.zoi7.mysql.comment.Field;
import com.zoi7.mysql.comment.FieldType;
import com.zoi7.mysql.comment.Id;

import java.sql.Date;

/**
 * @author yjy
 * 2018-06-21 13:28
 */
public class FieldUtils {

    /**
     * 获取字段名
     * @param field 属性名
     * @return 字段名
     */
    public static String getColumn(java.lang.reflect.Field field) {
        Field fieldAnnotation = field.getAnnotation(Field.class);
        // 字段名
        String column = fieldAnnotation.field();
        if ("".equals(column))
            column = getColumnByField(field.getName());
        return column;
    }

    /**
     * 根据属性名获取字段名
     * @param fieldName 属性名
     * @return 字段名
     */
    public static String getColumnByField(String fieldName) {
        StringBuilder column = new StringBuilder();
        for (int i = 0; i < fieldName.length(); i++) {
            char c = fieldName.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                c += 32;
                column.append('_');
            }
            column.append(c);
        }
        return column.toString();
    }

    /**
     * 获取字段指定的类型
     * @param field 字段
     * @return 类型
     */
    public static FieldType getType(java.lang.reflect.Field field) {
        Field fieldAnnotation = field.getAnnotation(Field.class);
        FieldType type = fieldAnnotation.type();
        if (FieldType.AUTO.equals(type)) {
            Class clazz = field.getType();
            // int
            if (clazz == Integer.class || clazz == int.class)
                type = FieldType.INTEGER;
                // long
            else if (clazz == Long.class || clazz == long.class)
                type = FieldType.BIGINT;
                // double
            else if (clazz == Double.class || clazz == double.class)
                type = FieldType.DOUBLE;
                // float
            else if (clazz == Float.class || clazz == float.class)
                type = FieldType.FLOAT;
                // datetime
            else if (clazz == Date.class || clazz == java.util.Date.class)
                type = FieldType.DATETIME;
                // 其他
            else
                type = FieldType.VARCHAR;
        }
        if (type.equals(FieldType.INT)) {
            type = FieldType.INTEGER;
        }
        return type;
    }

    /**
     * 是否自增长
     * @param idField id字段
     * @return 是否
     */
    public static boolean isAutoIncrease(java.lang.reflect.Field idField) {
        if (idField.isAnnotationPresent(Id.class)) {
            Id fieldAnnotation = idField.getAnnotation(Id.class);
            return fieldAnnotation.autoIncrease();
        }
        return false;
    }

}
