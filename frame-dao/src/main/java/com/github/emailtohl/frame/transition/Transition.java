package com.github.emailtohl.frame.transition;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 实现事务，目前只注解在类级别上
 * @author helei
 */
@Documented
@Target({TYPE/*, METHOD*/})
@Retention(RetentionPolicy.RUNTIME)
public @interface Transition {

}
