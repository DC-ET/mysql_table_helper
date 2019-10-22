package com.zoi7.mysql.util.generate;

import com.zoi7.mysql.comment.FieldType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

/**
 * @author yjy
 * @date 2019/10/12 10:36
 **/
public class EntitiesGenerator {

    private SqlHelper sqlHelper;
    /**
     * 文件输出地址
     */
    private String outputPath;
    /**
     * 实体包
     */
    private String entityPackage;
    /**
     * 数据库
     */
    private String database;
    /**
     * 创建人
     */
    private String author;
    /**
     * 当前时间
     */
    private String currentTime;
    /**
     * 实体基类全路径
     */
    private String baseEntity;
    /** 禁用注解 */
    private boolean disableAnnotation = false;

    /**
     * 构造函数
     */
    public EntitiesGenerator(String outputPath, String entityPackage, String url, String driverName, String database, String username, String password, String superEntityClass, String author) {
        String url1 = url + "/" + database;
        if (outputPath.endsWith("/")) {
            outputPath += "/";
        }
        this.outputPath = outputPath;
        this.entityPackage = entityPackage;
        this.database = database;
        this.baseEntity = superEntityClass;
        this.author = author;
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm");
        this.currentTime = formater.format(new Date());
        sqlHelper = new SqlHelper(url1, driverName, username, password);
    }

    public void generate() {
        List<String> tableNames = sqlHelper.get(
                "SELECT * FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA='" + database + "';", "TABLE_NAME");
        Connection con = null;
        try {
            con = sqlHelper.getConnection();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (String table : tableNames) {
            generate(table, con);
            System.out.println("generated: "+table );
        }
        try {
            sqlHelper.closeConnection(con);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void generate(String tablename, Connection con) {
        if (con == null) {
            System.out.println("------------------Connection to database was not set up------------------");
            return;
        }
        // 查要生成实体类的表
        String sql = "select COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, IS_NULLABLE, COLUMN_COMMENT, COLUMN_DEFAULT, COLUMN_KEY " +
                " FROM INFORMATION_SCHEMA.COLUMNS " +
                " where table_name = '"+ tablename + "'";
        PreparedStatement pStemt = null;
        try {
            pStemt = con.prepareStatement(sql);
            ResultSet set = pStemt.executeQuery();

            List<Map<String, Object>> columns = convertList(set);
            if (!columns.isEmpty()) {
                String tableSql = "select TABLE_COMMENT FROM INFORMATION_SCHEMA.TABLES where TABLE_NAME = '"+ tablename + "'";
                pStemt = con.prepareStatement(tableSql);
                set = pStemt.executeQuery();
                List<Map<String, Object>> list = convertList(set);
                String tableComment = (String)list.get(0).get("TABLE_COMMENT");
                String content = parse(tablename, columns, tableComment);
                try {
                    File dir = new File(outputPath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    String outputPath = dir.getAbsolutePath() + "/" + initcap(tablename) + ".java";
                    FileWriter fw = new FileWriter(outputPath);
                    PrintWriter pw = new PrintWriter(fw);
                    pw.println(content);
                    pw.flush();
                    pw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pStemt != null) {
                    pStemt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static List<Map<String, Object>> convertList(ResultSet rs) throws SQLException{
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        //获取键名
        ResultSetMetaData md = rs.getMetaData();
        //获取行的数量
        int columnCount = md.getColumnCount();
        while (rs.next()) {
            //声明Map
            Map<String, Object> rowData = new HashMap<String, Object>();
            for (int i = 1; i <= columnCount; i++) {
                //获取键名及值
                rowData.put(md.getColumnName(i), rs.getObject(i));
            }
            list.add(rowData);
        }
        return list;
    }

    /**
     * 功能：生成实体类主体代码
     *
     * @return
     */
    protected String parse(String tablename, List<Map<String, Object>> columns, String tableComment) {
        StringBuffer sb = new StringBuffer();
        sb.append("package ").append(this.entityPackage).append(";\n\n");
        sb.append("import com.zoi7.mysql.comment.*;\r\n");
        sb.append("import ").append(baseEntity).append(";\r\n");
        sb.append("import java.math.BigDecimal;\r\n");
        sb.append("import java.sql.Blob;\r\n");
        sb.append("import java.util.Date;\r\n");
        sb.append("import lombok.Data;\r\n");
        sb.append("\r\n");
        // 注释部分
        sb.append("/**\r\n");
        sb.append(" * ").append(tablename);
        sb.append("  generated at ").append(currentTime).append(" by: ").append(author).append("\r\n");
        sb.append(" */\n");
        sb.append("@Data\n");
        // 实体部分
        sb.append(disableAnnotation ? "/** " : "")
                .append("@Entity(tableName = \"").append(tablename)
                .append("\", comment = \"").append(tableComment)
                .append("\", check = true)").append(disableAnnotation ? " */" : "").append("\n");
        sb.append("public class ").append(initcap(tablename)).append(" extends BaseEntity {\r\n\n");
        sb.append("\tprivate static final long serialVersionUID = 1L;\n\n");
        processAllAttrs(sb, columns);// 属性
        sb.append("}");
        return sb.toString();
    }

    /**
     * 功能：生成所有属性
     *
     * @param sb
     */
    protected void processAllAttrs(StringBuffer sb, List<Map<String, Object>> columns) {
        for (Map<String, Object> map : columns) {
            boolean priKey = false;
            String columnName = map.get("COLUMN_NAME").toString();
            String type = (String)map.get("DATA_TYPE");
            BigInteger length = (BigInteger)map.get("CHARACTER_MAXIMUM_LENGTH");
            String nullable = (String)map.get("IS_NULLABLE");
            String comment = (String) map.get("COLUMN_COMMENT");
            String defaultValue = (String) map.get("COLUMN_DEFAULT");
            String key = (String) map.get("COLUMN_KEY");
            String javaType = sqlType2JavaType(type);
            FieldType fieldType = sqlType2FieldType(type);

            sb.append("\t").append(disableAnnotation ? "/** " : "").append("@Field(field = \"").append(columnName).append("\"");
            if (fieldType != null) {
                sb.append(", type = FieldType.").append(fieldType.toString());
            }
            if (length != null && length.longValue() < Integer.MAX_VALUE) {
                sb.append(", length = ").append(length);
            }
            if (nullable != null) {
                sb.append(", nullable = ").append("NO".equals(nullable) ? "false" : "true");
            }
            if (defaultValue != null) {
                sb.append(", defaultValue = \"").append(defaultValue).append("\"");
            }
            if (key != null) {
                if ("PRI".equals(key)) {
                    priKey = true;
                } else if ("UNI".equals(key)) {
                    sb.append(", index = @Index(unique = true)");
                } else if ("MUL".equals(key)) {
                    sb.append(", index = @Index");
                }
            }
            sb.append(", comment = \"").append(comment.replace("\"", "\\\"").trim())
                    .append("\")").append(disableAnnotation ? " */" : "").append("\n");
            if (priKey) {
                sb.append("\t").append(disableAnnotation ? "/** " : "").append("@Id(autoIncrease = false)").append(disableAnnotation ? " */" : "").append("\n");
            }
            sb.append("\tprivate ").append(javaType).append(" ").append(initcap(columnName, false)).append(";\r\n");
        }
        sb.append("\n");
    }

    protected static String initcap(String str) {
        return initcap(str, true);
    }

    /**
     * 功能：将输入字符串的首字母改成大写
     *
     * @param str
     * @return
     */
    protected static String initcap(String str, boolean upperFirst) {
        str = str.toLowerCase();
        char[] ch = str.toCharArray();
        if (upperFirst) {
            if (ch[0] >= 'a' && ch[0] <= 'z') {
                ch[0] = (char) (ch[0] - 32);
            }
        }
        for (int i = 1; i < str.length() - 2; i++) {
            if (ch[i] == '_') {
                if (ch[i + 1] >= 'a' && ch[i + 1] <= 'z') {
                    ch[i + 1] = (char) (ch[i + 1] - 32);
                }
            }
        }
        str = new String(ch).replace("_", "");
        return str;
    }

    /**
     * 功能：获得列的数据类型
     *
     * @param sqlType
     * @return
     */
    protected String sqlType2JavaType(String sqlType) {
        if ("bit".equalsIgnoreCase(sqlType)) {
            return "Boolean";
        } else if ("tinyint".equalsIgnoreCase(sqlType) || "tinyINT UNSIGNED".equalsIgnoreCase(sqlType)) {
            return "Integer";
        } else if ("smallint".equalsIgnoreCase(sqlType)) {
            return "Integer";
        } else if ("int".equalsIgnoreCase(sqlType) || "INT UNSIGNED".equalsIgnoreCase(sqlType)) {
            return "Integer";
        } else if ("bigint".equalsIgnoreCase(sqlType)) {
            return "Long";
        } else if ("float".equalsIgnoreCase(sqlType)) {
            return "Float";
        } else if ("decimal".equalsIgnoreCase(sqlType) || "money".equalsIgnoreCase(sqlType)
                || "smallmoney".equalsIgnoreCase(sqlType)) {
            return "BigDecimal";
        } else if ("numeric".equalsIgnoreCase(sqlType)
                || "real".equalsIgnoreCase(sqlType) || "DOUBLE".equalsIgnoreCase(sqlType) ) {
            return "Double";
        } else if ("varchar".equalsIgnoreCase(sqlType) || "char".equalsIgnoreCase(sqlType)
                || "nvarchar".equalsIgnoreCase(sqlType) || "nchar".equalsIgnoreCase(sqlType)
                || "text".equalsIgnoreCase(sqlType) || "longtext".equalsIgnoreCase(sqlType)
                || "json".equalsIgnoreCase(sqlType)) {
            return "String";
        } else if ("datetime".equalsIgnoreCase(sqlType) || "date".equalsIgnoreCase(sqlType)) {
            return "Date";
        } else if ("image".equalsIgnoreCase(sqlType) || "blob".equalsIgnoreCase(sqlType)) {
            return "Blob";
        } else if ("TIMESTAMP".equalsIgnoreCase(sqlType)){
            return "java.sql.Timestamp";
        }
        return null;
    }

    /**
     * 功能：获得列的数据类型
     *
     * @param sqlType
     * @return
     */
    protected FieldType sqlType2FieldType(String sqlType) {
        if ("bit".equalsIgnoreCase(sqlType)) {
            return FieldType.BIT;
        } else if ("tinyint".equalsIgnoreCase(sqlType) || "tinyINT UNSIGNED".equalsIgnoreCase(sqlType)) {
            return FieldType.TINYINT;
        } else if ("smallint".equalsIgnoreCase(sqlType)) {
            return FieldType.SMALLINT;
        } else if ("int".equalsIgnoreCase(sqlType) || "INT UNSIGNED".equalsIgnoreCase(sqlType)) {
            return FieldType.INTEGER;
        } else if ("bigint".equalsIgnoreCase(sqlType)) {
            return FieldType.BIGINT;
        } else if ("float".equalsIgnoreCase(sqlType)) {
            return FieldType.FLOAT;
        } else if ("decimal".equalsIgnoreCase(sqlType) || "money".equalsIgnoreCase(sqlType)
                || "smallmoney".equalsIgnoreCase(sqlType)) {
            return FieldType.DECIMAL;
        } else if ("numeric".equalsIgnoreCase(sqlType) || "real".equalsIgnoreCase(sqlType) || "DOUBLE".equalsIgnoreCase(sqlType) ) {
            return FieldType.DOUBLE;
        } else if ("varchar".equalsIgnoreCase(sqlType) || "nvarchar".equalsIgnoreCase(sqlType) || "nchar".equalsIgnoreCase(sqlType)
                || "json".equalsIgnoreCase(sqlType)) {
            return FieldType.VARCHAR;
        }  else if ("char".equalsIgnoreCase(sqlType)) {
            return FieldType.CHAR;
        } else if ("text".equalsIgnoreCase(sqlType)) {
            return FieldType.TEXT;
        } else if ("longtext".equalsIgnoreCase(sqlType)) {
            return FieldType.LONGTEXT;
        } else if ("date".equalsIgnoreCase(sqlType)) {
            return FieldType.DATE;
        } else if ("datetime".equalsIgnoreCase(sqlType)) {
            return FieldType.DATETIME;
        } else if ("image".equalsIgnoreCase(sqlType) || "blob".equalsIgnoreCase(sqlType)) {
            return FieldType.BLOB;
        } else if ("TIMESTAMP".equalsIgnoreCase(sqlType)){
            return FieldType.TIMESTAMP;
        }
        return null;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getEntityPackage() {
        return entityPackage;
    }

    public void setEntityPackage(String entityPackage) {
        this.entityPackage = entityPackage;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBaseEntity() {
        return baseEntity;
    }

    public void setBaseEntity(String baseEntity) {
        this.baseEntity = baseEntity;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public boolean isDisableAnnotation() {
        return disableAnnotation;
    }

    public void setDisableAnnotation(boolean disableAnnotation) {
        this.disableAnnotation = disableAnnotation;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }
}