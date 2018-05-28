package com.yjy.mysql.dialect;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import com.yjy.mysql.analysis.ScanPackage;
import com.yjy.mysql.comment.Entity;
import com.yjy.mysql.comment.Field;
import com.yjy.mysql.comment.FieldType;
import com.yjy.mysql.comment.Id;
import com.yjy.mysql.driverManager.DriverManagerDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.yjy.mysql.config.Config.*;

public class MYSQL5Dialect {

    private static final Logger log = LoggerFactory.getLogger(MYSQL5Dialect.class);

    private List<String> sqlList = new ArrayList<String>();
    private List<String> alterUpdates = new ArrayList<String>();
    private String[] packages; // 表实体所在包
    private String auto; // 更新方式
    private boolean showSql; // 是否打印执行的sql语句
    private DataSource dataSource; // 数据库连接
    private Connection connect;

    {
        this.dataSource = new DriverManagerDataSource(DB_DRIVER_NAME, DB_URL, DB_USERNAME, DB_PASSWORD);
        this.packages = DB_PACKAGES;
        this.auto = "create".equalsIgnoreCase(DB_AUTO) ? DB_AUTO : "update";
        this.showSql = DB_SHOW_SQL;
    }

    /**
     * 初始化入口
     */
    public void init() {
        try {
            log.info("MYSQL5Dialect init...");
            this.connect = this.dataSource.getConnection();
            Set<Class<?>> clazzSet = new HashSet<Class<?>>();
            for (String package1 : this.packages) {
                clazzSet.addAll(ScanPackage.getClassesByPackageName(package1));
            }
            log.info("MYSQL5Dialect init > packagesSize: {}, auto : {}, classListSize : {}", packages.length, this.auto, clazzSet.size());
            if ("create".equals(this.auto)) {
                create(clazzSet);
            } else if ("update".equals(this.auto)) {
                update(clazzSet);
            }
            this.sqlList.addAll(this.alterUpdates);
            Statement statement = this.connect.createStatement();
            for (String sql : this.sqlList) {
                if (this.showSql) {
                    System.out.println(sql);
                }
                statement.addBatch(sql);
            }
            statement.executeBatch();
            this.connect.close();
            log.info("MYSQL5Dialect init finished...");
        } catch (Exception e) {
            log.error("init throw an error", e);
        } finally {
            try {
                this.connect.close();
            } catch (Exception e) {
                log.error("init > close connection failed", e);
            }
        }
    }

    /**
     * 重新创建表
     * @param clazzSet 表实体列表
     */
    private void create(Set<Class<?>> clazzSet) {
        log.info("MYSQL5Dialect create...");
        for (Class<?> clazz : clazzSet) {
            Entity entity; // 表实体
            String tableName;
            // 是否表实体
            if (!clazz.isAnnotationPresent(Entity.class)) {
                continue;
            }
            // 是否需要检测表结构, 如果不需, 则跳过该表实体
            if (!(entity = clazz.getAnnotation(Entity.class)).check()) {
                continue;
            }
            // 验证表名
            if ("".equals((tableName = entity.tableName()).trim())) {
                throw new RuntimeException(clazz.getName() + " 未指定或指定了错误的表名 : " + tableName);
            }
            // 删除原有表
            this.sqlList.add("DROP TABLE IF EXISTS " + tableName + ";");
            // 创建新表
            createTable(tableName, clazz);
        }
    }

    /**
     * 新建表
     * @param tableName 表名
     * @param clazz 表实体
     */
    private void createTable(String tableName, Class<?> clazz) {
        log.info("MYSQL5Dialect createTable: {}", tableName);
        String idField = null;
        boolean firstColumn = true;
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + "(\n");
        // 遍历字段
        for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
            // 非注解字段 > 跳过
            if (!field.isAnnotationPresent(Field.class)) {
                continue;
            }
            // 验证字段名
            Field fieldAnnotation = field.getAnnotation(Field.class);
            // 如果是不是第一个字段, 则sql先加','
            if (firstColumn) {
                firstColumn = false;
            } else {
                sql.append(",\n");
            }
            // 如果是id字段 > 标记 & not null
            if (field.isAnnotationPresent(Id.class)) {
                if (idField == null)
                    idField = getColumn(field);
                String type = getType(field);
                sql.append("\t")
                        .append(getColumn(field))
                        .append(" ")
                        .append(type)
                        .append("(")
                        .append(fieldAnnotation.length())
                        .append((isDecimal(type) ? "," + fieldAnnotation.decimalLength() : ""))
                        .append(")")
                        .append(" NOT NULL AUTO_INCREMENT");
            }
            // 普通字段
            else {
                sql.append("\t").append(getColumnSql(field));
            }
        }
        if (idField != null) {
            sql.append(", PRIMARY KEY (").append(idField).append(")");
        }
        sql.append(");");
        this.sqlList.add(sql.toString());
    }

    /**
     * 更新表结构
     * @param clazzSet 表实体列表
     */
    private void update(Set<Class<?>> clazzSet) throws Exception {
        log.info("MYSQL5Dialect update...");
        for (Class<?> clazz : clazzSet) {
            Entity entity; // 表实体
            String tableName;
            // 是否表实体
            if (!clazz.isAnnotationPresent(Entity.class)) {
                continue;
            }
            // 是否需要检测表结构, 如果不需, 则跳过该表实体
            if (!(entity = clazz.getAnnotation(Entity.class)).check()) {
                continue;
            }
            // 验证表名
            if ("".equals((tableName = entity.tableName().trim()))) {
                throw new RuntimeException(clazz.getName() + " 未指定或指定了错误的表名 : " + tableName);
            }
            // 如果表不存在, 则新建表
            if (!checkTableExist(tableName)) {
                createTable(tableName, clazz);
            }
            // 如果表已存在, 则检查并更新字段
            else {
                checkForAddColumn(clazz);
            }
        }
    }

    /**
     * 检测表中是否含有该字段, 如不包含, 则新增该字段
     * @param clazz 表实体
     */
    private void checkForAddColumn(Class<?> clazz) throws Exception {
        log.info("MYSQL5Dialect checkForAddColumn ...");
        // 遍历字段
        for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
            PreparedStatement ps;
            ResultSet resultSet;
            // 非注解字段 > 跳过
            if (!field.isAnnotationPresent(Field.class)) {
                continue;
            }
            // 验证字段名
            Field fieldAnnotation = field.getAnnotation(Field.class);
            // 检查字段是否存在
            String assertField = "DESCRIBE " + (clazz.getAnnotation(Entity.class)).tableName() + " " + fieldAnnotation.field();
            ps = this.connect.prepareStatement(assertField, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = ps.executeQuery();
            // 不存在则新增字段
            if (!resultSet.last()) {
                String alterSql = "ALTER TABLE " + (clazz.getAnnotation(Entity.class)).tableName() +
                        " ADD COLUMN " + getColumnSql(field);
                this.alterUpdates.add(alterSql);
            }
            ps.close();
            resultSet.close();
        }
    }

    /**
     * 检查表是否存在
     * @param name 表名
     * @return 是否存在
     */
    private boolean checkTableExist(String name) {
        log.info("MYSQL5Dialect checkTableExist...");
        PreparedStatement ps;
        ResultSet resultSet;
        try {
            String sql = "desc " + name;
            ps = this.connect.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            resultSet = ps.executeQuery();
            boolean hasRow = resultSet.last();
            ps.close();
            resultSet.close();
            return hasRow;
        } catch (MySQLSyntaxErrorException e1) {
            String message = e1.getMessage();
            if (message != null && Pattern.matches("Table '.*' doesn't exist", message))
                return false;
            else
                throw new RuntimeException(e1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取字段相对应的sql语句
     * @param field 属性
     * @return 字段sql
     */
    private String getColumnSql(java.lang.reflect.Field field) {
        Field fieldAnnotation = field.getAnnotation(Field.class);
        String type = getType(field);
        return " " + getColumn(field) +
                " " + type + "(" +
                fieldAnnotation.length() + (isDecimal(type) ? "," + fieldAnnotation.decimalLength() : " ") +
                ") " +
                (fieldAnnotation.nullable() ? " " : " NOT NULL ") +
                (!fieldAnnotation.nullable() ? " default " + fieldAnnotation.defaultValue() : "");
    }

    /**
     * 获取字段名
     * @param field 属性名
     * @return 字段名
     */
    private static String getColumn(java.lang.reflect.Field field) {
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
    private static String getColumnByField(String fieldName) {
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
    private static String getType(java.lang.reflect.Field field) {
        Field fieldAnnotation = field.getAnnotation(Field.class);
        FieldType type = fieldAnnotation.type();
        if (FieldType.AUTO.equals(type)) {
            Class clazz = field.getType();
            // int
            if (clazz == Integer.class || clazz == int.class)
                type = FieldType.INT;
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
            else if (clazz == Date.class)
                type = FieldType.DATETIME;
            // 其他
            else
                type = FieldType.VARCHAR;
        }
        return type.toString();
    }

    /**
     * 判断类型是否是小数
     * @param type 类型
     * @return 是否小数
     */
    private boolean isDecimal(String type) {
        return "float".equalsIgnoreCase(type)
                ||
                "double".equalsIgnoreCase(type)
                ||
                "decimal".equalsIgnoreCase(type);

    }

}