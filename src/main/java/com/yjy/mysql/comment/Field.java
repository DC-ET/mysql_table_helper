package com.yjy.mysql.comment;

import java.lang.annotation.*;

@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Field {
	
	/**
	 * 字段名
	 * @author yjy
	 * @date 2017年2月24日上午9:36:45
	 * @return
	 */
	public abstract String field();

	/**
	 * 字段类型
	 * 默认：varchar
	 * @author yjy
	 * @date 2017年2月24日上午9:36:52
	 * @return
	 */
	public abstract FieldType type() default FieldType.VARCHAR;

	/**
	 * 字段长度 
	 * 默认：255
	 * @author yjy
	 * @date 2017年2月24日上午9:37:03
	 * @return
	 */
	public abstract int length() default 255;
	
	/**
	 * 小数点长度
	 * 默认： 2
	 * @author yjy
	 * @date 2017年2月27日下午5:33:18
	 * @return
	 */
	public abstract int decimalLength() default 2;

	/**
	 * 是否可以为空 
	 * 默认：可以为空
	 * @author yjy
	 * @date 2017年2月24日上午9:37:15
	 * @return
	 */
	public abstract boolean nullable() default true;
	
	/**
	 * 新加字段不为空 时 旧数据填补默认值
	 * 仅限int类型字段
	 * @author yjy
	 * @date 2017年3月3日下午4:56:46
	 * @return
	 */
	public abstract int defaultValue() default 0;
	
}