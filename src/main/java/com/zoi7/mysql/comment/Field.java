package com.zoi7.mysql.comment;

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
	 * @return 字段名
	 */
	String field() default "";

	/**
	 * @return 字段类型
	 * 默认：varchar
	 */
	FieldType type() default FieldType.AUTO;

	/**
	 * @return 字段长度
	 * 默认：255
	 */
	int length() default 255;
	
	/**
	 * @return 小数点长度
	 * 默认： 2
	 */
	int decimalLength() default 2;

	/**
	 * @return 是否可以为空
	 * 默认：可以为空
	 */
	boolean nullable() default true;
	
	/**
	 * @return 新加字段不为空 时 旧数据填补默认值
	 * 仅限int类型字段
	 */
	int defaultValue() default -1;
	
}