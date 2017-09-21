package com.yjy.mysql.comment;

import java.lang.annotation.*;

/**
 * 字段
 * @author yjy
 * 2017年2月24日上午9:36:45
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD})
public @interface Field {
	
	/**
	 * 字段名
	 */
	String field();

	/**
	 * 字段类型
	 * 默认：varchar
	 */
	FieldType type() default FieldType.VARCHAR;

	/**
	 * 字段长度 
	 * 默认：255
	 */
	int length() default 255;
	
	/**
	 * 小数点长度
	 * 默认： 2
	 */
	int decimalLength() default 2;

	/**
	 * 是否可以为空 
	 * 默认：可以为空
	 */
	boolean nullable() default true;
	
	/**
	 * 新加字段不为空 时 旧数据填补默认值
	 * 仅限int类型字段
	 */
	int defaultValue() default 0;
	
}