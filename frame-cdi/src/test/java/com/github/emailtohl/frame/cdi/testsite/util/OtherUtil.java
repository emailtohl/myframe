package com.github.emailtohl.frame.cdi.testsite.util;

import javax.inject.Inject;

/**
 * 未被注解为Component，手动注册进容器中
 * @author HeLei
 */
public class OtherUtil {
	@Inject
	SomeOneUtil someOneUtil;

	public SomeOneUtil getSomeOneUtil() {
		return someOneUtil;
	}

	public void setSomeOneUtil(SomeOneUtil someOneUtil) {
		this.someOneUtil = someOneUtil;
	}
	
}
