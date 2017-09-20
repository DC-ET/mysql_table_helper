# mysql_table_helper
mysql自动更新表结构工具

# 工具介绍
这款工具支持用户通过注解配置实体类及其字段, 来自动更新当前数据库的表结构

举个栗子:
<br/><em/><em/> 1.我们代码中新建了一个表的实体, 数据库中还未创建该表, 那么我们可以通过配置, 在程序启动时, 让其自动在数据库中创建该表
<br/><em/><em/> 2.我们在代码的表实体中增加了一个字段, 同样可以通过注解, 让数据库表更新增加此字段.
<br/><em/><em/> <font color="red" >3.删除字段和修改字段名,暂不支持!</font>

# 使用方法
![image](https://raw.githubusercontent.com/15058126273/mysql_table_helper/master/resources/images/properties1.png)

