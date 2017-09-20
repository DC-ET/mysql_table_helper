package com.yjy.mysql.comment;

import java.lang.annotation.*;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Entity {
	
	/**
	 * 表名
	 * @author yjy
	 * @date 2017年2月24日上午9:36:17
	 * @return
	 */
	public abstract String tableName();
	
	/**
	 * 是否检测并更新表结构
	 * 默认：true
	 * 建议表实体没有变动的情况下 将其设置成false，这样启动的时候就不会再去检测更新这个表了
	 * @author yjy
	 * @date 2017年2月24日上午9:35:50
	 * @return
	 */
	public abstract boolean check() default true;
	
}