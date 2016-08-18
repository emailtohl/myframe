package com.github.emailtohl.frame.dao.datasource;

/**
 * Connection的代理类同时需实现本接口，在调用Connection的方法时，代理会根据isThreadStrategy决定行为
 * 例如，在事务管理过程中，调用Connection的close方法时，代理会根据isThreadStrategy为true而不真正执行close，直到事务管理器来执行
 * 
 * @author helei
 * 2015.11.03
 */
public interface ConnectionStrategy {
	boolean isThreadStrategy();
	void setThreadStrategy(boolean isThreadStrategy);
}
