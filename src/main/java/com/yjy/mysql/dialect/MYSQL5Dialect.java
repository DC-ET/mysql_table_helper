package com.yjy.mysql.dialect;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import com.yjy.mysql.analysis.ScanPackage;
import com.yjy.mysql.comment.Entity;
import com.yjy.mysql.comment.Field;
import com.yjy.mysql.comment.Id;
import com.yjy.mysql.config.Config;
import com.yjy.mysql.driverManager.DriverManagerDataSource;
import com.yjy.mysql.exception.DaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import static com.yjy.mysql.config.Config.*;

public class MYSQL5Dialect {
	
	private final Logger log = LoggerFactory.getLogger(MYSQL5Dialect.class);
	
	private List<String> sql = new ArrayList<String>();
	private List<String> alterUpdates = new ArrayList<String>();
	public String[] packages;
	public String auto;
	public boolean showSql;
	public DataSource dataSource;
	public Connection connect;

	public MYSQL5Dialect(String configPath) {
		Config.loadConfig(configPath);
		this.dataSource = new DriverManagerDataSource(DB_DRIVER_NAME, DB_URL, DB_USERNAME, DB_PASSWORD);
		this.packages = DB_PACKAGES;
		this.auto = "create".equals(DB_AUTO) ? DB_AUTO : "update";
		this.showSql = DB_SHOW_SQL;
	}
	
	public void init() throws SQLException, DaoException {
		String[] arrayOfString;
		if ((!(this.auto.equalsIgnoreCase("create"))) && (!(this.auto.equalsIgnoreCase("update")))) {
			return;
		}
		List<Class<?>> clazzs = new ArrayList<Class<?>>();
		int j = (arrayOfString = this.packages).length;
		for (int i = 0; i < j; ++i) {
			String ps = arrayOfString[i];
			clazzs.addAll(ScanPackage.getClassesByPackageName(ps));
		}
		this.connect = this.dataSource.getConnection();
		if (this.auto.equalsIgnoreCase("create")) {
			this.log.info("开始初使化数据库，初使化类型为：CREATE");
			create(clazzs);
		} else if (this.auto.equalsIgnoreCase("update")) {
			this.log.info("开始初使化数据库，初使化类型为：UPDATE");
			update(clazzs);
		}
		this.sql.addAll(this.alterUpdates);
		Statement statement = this.connect.createStatement();
		for (Iterator<String> localIterator = this.sql.iterator(); localIterator.hasNext();) {
			String obj = localIterator.next();
			if (this.showSql) {
				System.out.println(obj);
			}
			statement.addBatch(obj);
		}
		statement.executeBatch();
		this.connect.close();
	}

	public void update(List<Class<?>> clazzs) throws DaoException {
		for (Iterator<Class<?>> localIterator = clazzs.iterator(); localIterator.hasNext(); ) { 
			java.lang.reflect.Field[] arrayOfField;
			Class<?> clazz = (Class<?>)localIterator.next();
			StringBuffer sqls = new StringBuffer("");
			String idField = "";
			if (!(clazz.isAnnotationPresent(Entity.class))) {
				continue;
			}
			// 获取注解实体
			Entity entity = clazz.getAnnotation(Entity.class);
			// 表名
			String tableName = entity.tableName();
			// 是否需要检测表结构
			boolean check = entity.check();
			if (!check) {
				continue;
			}
			if (tableName.equals("")) {
				throw new DaoException("类:[" + clazz.getName() + "]未指明正确的表名!");
			}
			sqls.append("CREATE TABLE IF NOT EXISTS " + tableName + "(\n");
			java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
			int j = (arrayOfField = fields).length; 
			for (int i = 0; i < j; ++i) { 
				java.lang.reflect.Field field = arrayOfField[i];
				if (!(field.isAnnotationPresent(Field.class))) {
					continue;
				}
				Field fieldAnnotion = (Field)field.getAnnotation(Field.class);
				if (fieldAnnotion.field().equals("")) {
					throw new DaoException("类：" + clazz.getName() + "的属性[" + field.getName() + "]未指定正确的字段名!");
				}
				boolean hasIdAnnotion = field.isAnnotationPresent(Id.class);
				if (hasIdAnnotion) {
					sqls.append("\t" + fieldAnnotion.field() + " INT(11) NOT NULL AUTO_INCREMENT");
				} else {
					sqls.append("\t" + fieldAnnotion.field() + " " + fieldAnnotion.type().toString());
					int length = fieldAnnotion.length();
					int decimalLength = fieldAnnotion.decimalLength();
					String type = fieldAnnotion.type().toString();
					if (type.equals("INT")) {
						sqls.append("(" + ((length == 255) ? 11 : length) + ")");
					} else if (type.equals("VARCHAR")) {
						sqls.append("(" + length + ")");
					} else if (type.equals("DECIMAL")) {
						sqls.append("(" + ((length == 255) ? 12 : length) + ", " + decimalLength + ")");
					}
					sqls.append(((fieldAnnotion.nullable() && !type.equals("TIMESTAMP")) ? " " : " NOT NULL") +
							(!fieldAnnotion.nullable() && type.indexOf("INT") != -1 ? (" default " + fieldAnnotion.defaultValue()) : " "));
				}
				if (idField.equals("")) {
					idField = (hasIdAnnotion) ? fieldAnnotion.field() : "";
				}
				try {
					String assertField = "DESCRIBE " + ((Entity)clazz.getAnnotation(Entity.class)).tableName() + " " + fieldAnnotion.field();
					PreparedStatement ps = this.connect.prepareStatement(assertField, 1004, 1007);
					ResultSet resultSet = ps.executeQuery();
					resultSet.last();
					if (resultSet.getRow() == 0) {
						String type = fieldAnnotion.type().toString();
						int length = fieldAnnotion.length();
						int decimalLength = fieldAnnotion.decimalLength();
						String typeSql = "";
						if (type.equalsIgnoreCase("INT")) {
							typeSql = "INT(" + ((length == 255) ? 11 : length) + ") ";
						} else if (type.equalsIgnoreCase("VARCHAR")) {
							typeSql = "VARCHAR(" + length + ") ";
						} else if (type.equalsIgnoreCase("DECIMAL")) {
							typeSql = "DECIMAL(" + ((length == 255) ? 12 : length) + ", " + decimalLength + ") ";
						} else {
							typeSql = fieldAnnotion.type().toString() + " ";
						}
						String alterSql = "ALTER TABLE " + 
										((Entity)clazz.getAnnotation(Entity.class)).tableName() +
										" ADD COLUMN " + fieldAnnotion.field() + 
										" " + typeSql + 
										(fieldAnnotion.nullable() && !type.equals("TIMESTAMP") ? " " : "NOT NULL " ) +
										(!fieldAnnotion.nullable() && type.indexOf("INT") != -1 ? (" default " + fieldAnnotion.defaultValue()) : " ");
						this.alterUpdates.add(alterSql);
					}
				} catch (MySQLSyntaxErrorException e) {
					String message = e.getMessage();
					if (message == null || !Pattern.matches("Table '.*' doesn't exist", message))
						log.error(e.getLocalizedMessage());
				} catch (Exception e2) {
					log.error("更新字段出错了", e2);
				}
				sqls.append(",\n");
			}
			if (!(idField.equals(""))) {
				sqls.append("\tPRIMARY KEY (" + idField + ")");
			}
			sqls.append("\n);\n");
			this.sql.add(sqls.toString());
    }
  }

	public void create(List<Class<?>> clazzs) {
	this.sql.add("SET FOREIGN_KEY_CHECKS=0;");
	for (Iterator<Class<?>> localIterator = clazzs.iterator(); localIterator.hasNext(); ) { 
		java.lang.reflect.Field[] arrayOfField;
		Class<?> clazz = (Class<?>)localIterator.next();
		StringBuffer sqls = new StringBuffer("");
		String idField = "";
		if (!(clazz.isAnnotationPresent(Entity.class))) {
			continue;
		}
		// 获取注解实体
		Entity entity = clazz.getAnnotation(Entity.class);
		// 表名
		String tableName = entity.tableName();
		// 是否需要检测表结构
		boolean check = entity.check();
		if (!check) {
			continue;
		}
		if (tableName.equals("")) {
			throw new DaoException("类：[" + clazz.getName() + "]未指定正确的表名!");
		}
		this.sql.add("DROP TABLE IF EXISTS " + tableName + ";");
		sqls.append("CREATE TABLE " + ((Entity)clazz.getAnnotation(Entity.class)).tableName() + "(\n");
		java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
		int j = (arrayOfField = fields).length; 
		for (int i = 0; i < j; ++i) { 
			java.lang.reflect.Field field = arrayOfField[i];
			if (!(field.isAnnotationPresent(Field.class))) {
				continue;
			}
			Field fieldAnnotion = (Field)field.getAnnotation(Field.class);
			if (fieldAnnotion.field().equals("")) {
				throw new DaoException("类：" + clazz.getName() + "的属性[" + field.getName() + "]未指定正确的字段名!");
			}
			boolean hasIdAnnotion = field.isAnnotationPresent(Id.class);
			if (hasIdAnnotion) {
				sqls.append("\t" + fieldAnnotion.field() + " INT(11) NOT NULL AUTO_INCREMENT");
			} else {
				sqls.append("\t" + fieldAnnotion.field() + " " + fieldAnnotion.type().toString());
				if (fieldAnnotion.type().toString().endsWith("INT")) {
					sqls.append("(" + ((fieldAnnotion.length() == 255) ? 11 : fieldAnnotion.length()) + ")");
				} else if (fieldAnnotion.type().toString().equals("VARCHAR")) {
					sqls.append("(" + fieldAnnotion.length() + ")");
				}
				sqls.append((fieldAnnotion.nullable() && !fieldAnnotion.type().toString().equals("TIMESTAMP")) ? " " : " NOT NULL");
			}
			if (idField.equals("")) {
				idField = (hasIdAnnotion) ? fieldAnnotion.field() : "";
			}
			sqls.append(",\n");
		}
		if (!(idField.equals(""))) {
			sqls.append("\tPRIMARY KEY (" + idField + ")");
		}
		sqls.append("\n);\n");
		this.sql.add(sqls.toString());
	}
}

	public String[] getPackages() {
		return this.packages;
	}

	public void setPackages(String[] packages) {
		this.packages = packages;
	}

	public String getAuto() {
		return this.auto;
	}

	public void setAuto(String auto) {
		this.auto = auto;
	}

	public boolean isShowSql() {
		return this.showSql;
	}

	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}

	public DataSource getDataSource() {
		return this.dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}