package com.taolong.mymvc.annotatioin;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE})//作用范围
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyController {
	String value() default "";
}
