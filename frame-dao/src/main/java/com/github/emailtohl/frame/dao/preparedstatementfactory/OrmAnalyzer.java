package com.github.emailtohl.frame.dao.preparedstatementfactory;

/**
 * 提供解析Orm对象的接口，将注解信息解析到SqlAndArgs中，供SQLBuilder使用
 * 
 * @author helei
 */
public interface OrmAnalyzer {
	SqlAndArgs parse(Object po);
}
