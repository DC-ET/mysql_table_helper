package com.yjy.mysql.dialect;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import com.yjy.mysql.analysis.ScanPackage;
import com.yjy.mysql.comment.Entity;
import com.yjy.mysql.comment.Field;
import com.yjy.mysql.comment.Id;
import com.yjy.mysql.config.Config;
import com.yjy.mysql.driverManager.DriverManagerDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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

    public MYSQL5Dialect(String configPath) {
        // 加载配置参数
        Config.loadConfig(configPath);
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
            this.connect = this.dataSource.getConnection();
            List<Class<?>> clazzList = new ArrayList<Class<?>>();
            for (String package1 : this.packages) {
                clazzList.addAll(ScanPackage.getClassesByPackageName(package1));
            }
            if ("create".equals(this.auto)) {
                create(clazzList);
            } else if ("update".equals(this.auto)) {
                update(clazzList);
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
     * @param clazzList 表实体列表
     */
    private void create(List<Class<?>> clazzList) {
        for (Class<?> clazz : clazzList) {
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
            if (StringUtils.isBlank((tableName = entity.tableName()))) {
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
        String idField = null;
        boolean firstColumn = true;
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(\n";
        // 遍历字段
        for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
            // 非注解字段 > 跳过
            if (!field.isAnnotationPresent(Field.class)) {
                continue;
            }
            // 验证字段名
            Field fieldAnnotation = field.getAnnotation(Field.class);
            if (StringUtils.isBlank(fieldAnnotation.field())) {
                throw new RuntimeException(clazz.getName() + " > " + field.getName() + " 未指定或指定了错误的字段名 : " + fieldAnnotation.field());
            }
            // 如果是不是第一个字段, 则sql先加','
            if (firstColumn) {
                firstColumn = false;
            } else {
                sql += ",\n";
            }
            // 如果是id字段 > 标记 & not null
            if (field.isAnnotationPresent(Id.class)) {
                if (idField == null)
                    idField = fieldAnnotation.field();
                sql += "\t" + fieldAnnotation.field() + " INT(11) NOT NULL AUTO_INCREMENT";
            }
            // 普通字段
            else {
                sql += "\t" + fieldAnnotation.field() + " " + fieldAnnotation.type().toString();
                int length = fieldAnnotation.length();
                int decimalLength = fieldAnnotation.decimalLength();
                String type = fieldAnnotation.type().toString();
                if (type.equals("INT")) {
                    sql += "(" + ((length == 255) ? 11 : length) + ")";
                } else if (type.equals("VARCHAR")) {
                    sql += "(" + length + ")";
                } else if (type.equals("DECIMAL")) {
                    sql += "(" + ((length == 255) ? 12 : length) + ", " + decimalLength + ")";
                }
                sql += ((fieldAnnotation.nullable() && !type.equals("TIMESTAMP")) ? " " : " NOT NULL") +
                        (!fieldAnnotation.nullable() && type.contains("INT") ? (" default " + fieldAnnotation.defaultValue()) : " ");
            }
        }
        if (idField != null) {
            sql += ", PRIMARY KEY (" + idField + ")";
        }
        sql += ");";
        this.sqlList.add(sql);
    }

    /**
     * 更新表结构
     * @param clazzList 表实体列表
     */
    private void update(List<Class<?>> clazzList) throws Exception {
        for (Class<?> clazz : clazzList) {
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
            if (StringUtils.isBlank((tableName = entity.tableName()))) {
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
            if (StringUtils.isBlank(fieldAnnotation.field())) {
                throw new RuntimeException(clazz.getName() + " > " + field.getName() + " 未指定或指定了错误的字段名 : " + fieldAnnotation.field());
            }
            // 检查字段是否存在
            String assertField = "DESCRIBE " + (clazz.getAnnotation(Entity.class)).tableName() + " " + fieldAnnotation.field();
            ps = this.connect.prepareStatement(assertField, 1004, 1007);
            resultSet = ps.executeQuery();
            // 不存在则新增字段
            if (!resultSet.last()) {
                String type = fieldAnnotation.type().toString();
                int length = fieldAnnotation.length();
                int decimalLength = fieldAnnotation.decimalLength();
                String typeSql;
                if (type.equalsIgnoreCase("INT")) {
                    typeSql = "INT(" + ((length == 255) ? 11 : length) + ") ";
                } else if (type.equalsIgnoreCase("VARCHAR")) {
                    typeSql = "VARCHAR(" + length + ") ";
                } else if (type.equalsIgnoreCase("DECIMAL")) {
                    typeSql = "DECIMAL(" + ((length == 255) ? 12 : length) + ", " + decimalLength + ") ";
                } else {
                    typeSql = fieldAnnotation.type().toString() + " ";
                }
                String alterSql = "ALTER TABLE " + (clazz.getAnnotation(Entity.class)).tableName() +
                        " ADD COLUMN " + fieldAnnotation.field() + " " + typeSql +
                        (fieldAnnotation.nullable() && !type.equals("TIMESTAMP") ? " " : "NOT NULL ") +
                        (!fieldAnnotation.nullable() && type.contains("INT") ? (" default " + fieldAnnotation.defaultValue()) : " ");
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

}