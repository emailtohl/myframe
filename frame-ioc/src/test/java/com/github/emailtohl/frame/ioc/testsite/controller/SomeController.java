package com.github.emailtohl.frame.ioc.testsite.controller;

import javax.inject.Inject;

import com.github.emailtohl.frame.ioc.Component;
import com.github.emailtohl.frame.ioc.testsite.service.OtherService;
import com.github.emailtohl.frame.ioc.testsite.service.SomeService;

/**
 * Field和Property注入
 * @author HeLei
 */
@Component
public class SomeController {
	@Inject
	SomeService someService;
	
	OtherService otherService;

	public SomeService getSomeService() {
		return someService;
	}

	public void setSomeService(SomeService someService) {
		this.someService = someService;
	}

	public OtherService getOtherService() {
		return otherService;
	}

	@Inject
	public void setOtherService(OtherService otherService) {
		this.otherService = otherService;
	}
	
}
