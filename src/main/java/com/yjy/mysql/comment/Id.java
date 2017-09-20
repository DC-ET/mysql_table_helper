package com.yjy.mysql.comment;

import java.lang.annotation.*;

@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Id {
	
}