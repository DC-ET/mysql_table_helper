package com.zoi7.mysql.comment;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({}) @Retention(RUNTIME)
public @interface Index {

    /**
     * @return 是否启用索引
     */
    boolean index() default true;

    /**
     * @return 索引名称, 默认与字段同名
     */
	String name() default "";

    /**
     * @return 是否唯一, 默认false
     */
	boolean unique() default false;

}
