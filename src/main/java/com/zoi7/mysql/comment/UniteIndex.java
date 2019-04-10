package com.zoi7.mysql.comment;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({}) @Retention(RUNTIME)
public @interface UniteIndex {

    /**
     * 此为实体属性名
     * 当设置了 columns 后, fields 设置将无效
     * @return 相关属性名集合
     */
    String[] fields() default {};

    /**
     * 此为数据库表字段名
     * 当设置了 columns 后, fields 设置将无效
     * @return 相连字段名集合
     */
    String[] columns() default {};

    /**
     * @return 索引名称
     */
	String name() default "";

    /**
     * @return 是否唯一, 默认false
     */
	boolean unique() default false;

}
