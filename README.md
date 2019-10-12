# mysql_table_helper
* Java Language Level : 1.6
* Project Type : Maven


## 工具介绍
这款工具支持用户通过注解配置实体类及其字段, 来自动创建与更新当前数据库的表结构

### 举个栗子:
* 1.我们代码中新建了一个表的实体, 数据库中还未创建该表, 那么我们可以通过配置, 在程序启动时, 让其自动在数据库中创建该表
* 2.我们在代码的表实体中增加了一个字段, 同样可以通过注解, 让数据库表更新增加此字段.
* <font color="red" >3.删除字段和修改字段名,暂不支持!</font>

## 使用方法

一、在你的maven项目中引用依赖:
```xml
<!-- 在maven仓库 https://mvnrepository.com/artifact/com.2oi7/mysql-table-helper 查看最新版本 -->
<dependency>
    <groupId>com.2oi7</groupId>
    <artifactId>mysql-table-helper</artifactId>
    <version>2.5.0</version>
    <exclusions>
        <exclusion>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
        </exclusion>
        <exclusion>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

二、你需要有一个配置文件 db.properties ( 后面介绍了一种方式可以不需要配置文件 ), 内容如下: 
```properties
# 驱动类
db.driver = com.mysql.cj.jdbc.Driver
# 数据库地址
db.url = jdbc:mysql://192.168.1.72:3306/table_helper_test?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT
# 数据库用户
db.username = yjy
# 数据库密码
db.password = yyyyyy

# 需要扫描的包, 也就是表实体类所在的包, 包地址无需精确, 如下面这个配置也可以直接写成 com.cardgame.manager.entity
db.packages = com.zoi7.mysql.example
# create(如果当前存在该表名, 则删除原有表, 然后创建新表, 会丢失数据) or update(如果当前存在该表名, 则更新表字段, 否则创建表)
db.auto = update
# 是否打印执行的sql语句
db.showSql = true
# 表字段是否大写
db.uppercase=true
```
    
如果你项目中已经有类似的数据库配置信息, 那么你也可以直接将其拿来封装成 Properties<br/>
需要注意的是, Properties中的key需要以上面配置文件的key为标准
   
   
三、给我们的实体类加上注解, 例子如下:
```java
/**
 * @author yjy
 * 2018-05-28 17:49
 */
@Entity(tableName = "user_utf8mb4", comment = "测试用户表1111", indices = {
        @UniteIndex(fields = {"username", "sex"}, unique = true),
        @UniteIndex(name = "customName", fields = {"type", "intToStr"})
}, charset = "utf8mb4")
public class UserWithCharset {

    @Id(autoIncrease = false)
    @Field
    private Long id;
    @Field(nullable = false, defaultValue = "hello world")
    private String username;
    @Field(length = 30)
    private String nickName;
    @Field
    private Integer sex;
    @Field(nullable = false, defaultValue = "1")
    private int type;
    @Field
    private Date addTime;
    @Field
    private Double money;
    @Field(type = FieldType.VARCHAR, length = 50)
    private Integer intToStr;
    @Field(nullable = false)
    private Integer notNull;
    @Field(comment = "测试注释1", index = @Index(name = "uniquetettttttt", unique = true))
    private String testComment;
    @Field(comment = "测试注释2", index = @Index(name = "tettttttt", unique = false))
    private String testComment2;
    @Field
    private String testComment3;
    
}
```

四、在项目启动时想尽办法执行以下代码(三选一)

<b>1 > TableInitializer.init(CONFIG_PATH + "db.properties");</b><br/>
<b>2 > TableInitializer.init(properties).init();</b><br/>
<b>3 > TableInitializer.init(config).init();</b>

其中 CONFIG_PATH 是你的 db.properties 目录

### 举个栗子:

* 1.Springboot项目, 创建一个实现了 CommandLineRunner 的 Bean, 并增加注解 @Order(Ordered.HIGHEST_PRECEDENCE)
* 2.Web项目, 我们需要在web.xml中 配置一个监听器(自己实现一个), 在系统启动时, 执行该代码
* 3.Main函数, 我们则只需在Main函数适当的地方加上这么一句就好了, 要注意的是, 必须在初始化log4j配置之后

### 调用例子

* [simple example](https://github.com/15058126273/mysql_table_helper/tree/master/src/test/java/com/zoi7/mysql/example/main/SimpleTest.java)

* [maven地址](http://mvnrepository.com/artifact/com.2oi7/mysql-table-helper)
* [github地址](https://github.com/15058126273/mysql_table_helper)

### 2018-08-14 更新版本 1.0.1
* 新增Mybatis自动生成mapper.xml工具包 com.yjy.mysql.util.mybatis, 使用方法可以参考[测试例子](https://github.com/15058126273/mysql_table_helper/tree/master/src/test/java/com/zoi7/mysql/example/mybatis/SimpleTest.java)

### 2018-09-03 更新版本 1.0.2
* 增强 Mybatis自动生成mapper.xml工具包, 增加配置 自定义二级缓存类 相关的属性; 相关配置: customCachePros

### 2018-10-09 更新版本 2.0.0
* 重命名包名 com.yjy.mysql -> com.zoi7.mysql (由于 2oi7 不符合规范, 故将 z作为2)
* 配置TYPE新增 none, 表示不自动更新表结构

### 2018-12-12 更新版本 2.0.1
* 增强功能: 可为任意类型的非空字段填补默认值, 使用 defaultCharValue 属性

### 2019-1-2 更新版本 2.1.0
* 更新mysql驱动版本至: 5.1.30 -> 8.0.13
    1. 需要注意的地方: 
       1. driver需要修改 com.mysql.jdbc.Driver -> com.mysql.cj.jdbc.Driver
       1. url中需要增加参数 serverTimezone=GMT
       1. url中原 &amp; 需要改为 &

### 2019-1-8 更新版本 2.1.1
* 新增支持注解配置表/字段注释, 相关属性 @Entity | @Field > comment
* 新增支持注解配置单列索引, 相关属性 @Field > index
* 新增支持配置表字段是否大写模式

### 2019-4-10 更新版本 2.2.0
* 新增支持注解配置表的联合索引, 相关属性 @Entity > indices

### 2019-5-7 更新版本 2.3.0
* 新增支持注解配置字段无符号, 相关属性 @Field > unsigned

### 2019-6-12 更新版本 2.4.0
* @Entity注解中新增属性 charset 可为自动创建的表指定字符集
* @Field注解中的 defaultValue 属性类型更改为 String, 并将 defaultCharValue 属性移除
* FieldType枚举类移除 INT 类型, 请使用 INTEGER 代替, 功能相同
* FieldType枚举类新增 TINYTEXT / MEDIUMTEXT / LONGTEXT 类型
* 修复DATE/DATETIME类型 nullable = false 时不设置默认值无法创建表的问题

### 2019-7-10 更新版本 2.5.0
* 修改AbstractMapperUtils, 当实体类 Entity 注解中的 check = false时, 不生成默认xml文件

### 2019-10-12 更新版本 2.6.0
* FieldType增加枚举类型 BIT,CHAR,LONGVARCHAR,CLOB,BLOB
