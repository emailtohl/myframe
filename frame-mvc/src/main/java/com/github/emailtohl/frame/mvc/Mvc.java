package com.github.emailtohl.frame.mvc;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *************************************************
 * 注解类，提供类似SpringMvc功能
 * 
 * @author helei
 * @version 1.0
 *************************************************
 */
@Documented
@Target({TYPE, METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mvc {
	String action() default "";
	RequestMethod method() default RequestMethod.ANY;// 默认是任何HTTP方法都响应
}
