package com.github.emailtohl.frame.ioc.testsite.dao;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.emailtohl.frame.ioc.Component;
import com.github.emailtohl.frame.ioc.testsite.util.SomeOneUtil;

/**
 * JavaBean属性注入
 * @author HeLei
 */
@Component(name = "baseRepository")
public abstract class BaseDao {
	SomeOneUtil util;
	
	public BaseDao() {
		super();
	}

	public BaseDao(SomeOneUtil util) {
		super();
		this.util = util;
	}

	public SomeOneUtil getUtil() {
		return util;
	}

	@Inject
	@Named(value = "someOneUtil")
	public void setUtil(SomeOneUtil util) {
		this.util = util;
	}
	
}
