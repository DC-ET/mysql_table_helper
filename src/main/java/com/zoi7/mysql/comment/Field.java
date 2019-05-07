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
	 * 整数类型的字段默认值可使用此属性, 当然也可以使用defaultCharVal
	 */
	int defaultValue() default -1;

    /**
     * @return 新字段不为空时, 填补默认值
     * 可以指定任意类型字段的默认值
     */
	String defaultCharValue() default "";

    /**
     * @return 字段注释
     */
	String comment() default "";

    /**
     * @return 唯一索引名称 (默认不创建唯一索引)
     */
	Index index() default @Index(index = false);

    /**
     * 针对整型字段
     * 注意unsigned可能带来的问题:
     * 1.无法存入负数
     * 2.两个无符号字段相减时可能会出现意想不到的结果, 如: [a=1, b=2] => a - b = ERROR
     * @return 无符号字段
     */
	boolean unsigned() default false;

}