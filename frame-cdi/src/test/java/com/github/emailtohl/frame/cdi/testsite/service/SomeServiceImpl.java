package com.github.emailtohl.frame.cdi.testsite.service;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.emailtohl.frame.cdi.Component;
import com.github.emailtohl.frame.cdi.testsite.dao.SomeDao;

@Component
public class SomeServiceImpl implements SomeService {
	@Inject
	@Named("someRepository")
	SomeDao someDao;

	public SomeDao getSomeDao() {
		return someDao;
	}

	public void setSomeDao(SomeDao someDao) {
		this.someDao = someDao;
	}
	
}
