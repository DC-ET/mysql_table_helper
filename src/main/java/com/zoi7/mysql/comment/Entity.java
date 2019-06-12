package com.zoi7.mysql.comment;

import java.lang.annotation.*;

/**
 * 表实体注解
 * 支持创建单列字段索引 {@link com.zoi7.mysql.comment.Field}
 * @author yjy
 * 2017年2月24日上午9:36:17
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE})
public @interface Entity {
	
	/**
	 * 表名
	 * @return tableName
	 */
	String tableName();
	
	/**
	 * 默认：true
	 * 是否检测并更新表结构
	 * 建议表实体没有变动的情况下 将其设置成false，这样启动的时候就不会再去检测更新这个表了
	 * @return if check
	 */
	boolean check() default true;

    /**
     * @return 表注释
     */
	String comment() default "";

    /**
     * @return 联合索引集合 see{@link UniteIndex}
     */
	UniteIndex[] indices() default {};

    /**
     * @return 表编码
     */
	String charset() default "";

}