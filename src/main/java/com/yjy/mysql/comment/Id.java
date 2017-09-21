package com.yjy.mysql.comment;

import java.lang.annotation.*;

/**
 * 主键
 * @author yjy
 * 2017年2月24日上午9:36:45
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD})
public @interface Id {
	
}