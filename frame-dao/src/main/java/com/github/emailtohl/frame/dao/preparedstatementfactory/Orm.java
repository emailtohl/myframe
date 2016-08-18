package com.github.emailtohl.frame.dao.preparedstatementfactory;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *************************************************
 * ORM : ObjectRelationalMapping
 * 注意：
 * （1）被注解的bean一定不要使用基本数据类型，原因有二：
 *  ① 基本数据类型不能以对象注入进bean的属性字段中
 *  ② 程序会根据bean的属性字段是否为null拼写SQL，而基本数据类型的初始值不为null
 *  
 * （2）特别对于PostgreSQL数据库，类型一定要匹配，否则会引发异常，下面是基本对照表：
 * Java类型				PostgreSQL类型
 * Long					int8
 * String				varchar
 * Integer				int4
 * Float				float4
 * Double				float8
 * java.sql.Date		date
 * java.sql.time		time
 * java.sql.Timestamp	Datetime
 * java.sql.Timestamp	Timestamp
 * 
 * @author helei
 *************************************************
 */
@Documented
@Target({TYPE, FIELD, METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Orm {
	/**
	 * 注解在类上 po对应的数据库表名
	 */
	String tableName() default "";

	/**
	 * 注解在属性上 对应表的表头名
	 */
	String columnLabel() default "";

	/**
	 * 注解在属性上 对应表的表头列号
	 */
	int columnNo() default 0;

	/**
	 * 注解在属性上 说明此属性对应数据库表的主键
	 */
	boolean isKey() default false;

	/**
	 * 有的项目不能删除记录，对于删除只做标记，此注解表示该字段是否用做标记删除
	 * 由于很多数据库不支持boolean数据类型，所以设此字段的属性为int，数据库中该字段一定是整型
	 * 标记删除为“1”，查询时会过滤该字段标记为“1”的记录，删除时，会将此字段置为“1”
	 */
	boolean isMarkDeleted() default false;
}
