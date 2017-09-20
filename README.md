# mysql_table_helper
mysql自动更新表结构工具

# 工具介绍
这款工具支持用户通过注解配置实体类及其字段, 来自动更新当前数据库的表结构

举个栗子:
<br/><em/><em/> 1.我们代码中新建了一个表的实体, 数据库中还未创建该表, 那么我们可以通过配置, 在程序启动时, 让其自动在数据库中创建该表
<br/><em/><em/> 2.我们在代码的表实体中增加了一个字段, 同样可以通过注解, 让数据库表更新增加此字段.
<br/><em/><em/> <font color="red" >3.删除字段和修改字段名,暂不支持!</font>

# 使用方法
1.你需要有一个配置文件 db.properties, 内容如下图: 
<br/>
![image](https://raw.githubusercontent.com/15058126273/mysql_table_helper/master/resources/images/properties1.png)
<br/>
2.在项目启动时想尽办法执行以下代码
<br/>
<em/><em/><b>new com.yjy.mysql.dialect.MYSQL5Dialect(CONFIG_PATH + "db.properties").init();</b>
<br/>
举个栗子:
<br/>
<em/><em/> 1.Main函数入口项目, 我们则只需在Main函数适当的地方加上这么一句就好了, 要注意的是, 必须在初始化log4j配置之后
<br/>
<em/><em/> 2.Web项目, 我们需要在web.xml中 配置一个监听器(自己实现一个), 在系统启动时, 执行该代码
<br/>
<em/><em/> 3.不知道, 自己想办法
<br/>
3.最后, 给我们的实体类加上注解, 就大功告成了, 例子如下图:
<br/>
![image](https://raw.githubusercontent.com/15058126273/mysql_table_helper/master/resources/images/entity1.png)