package com.github.emailtohl.frame.ioc.testsite.dao;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.github.emailtohl.frame.ioc.testsite.util.SomeOneUtil;
/**
 * 构造器注入
 * @author HeLei
 */
@Component("someRepository")
public class SomeDao extends BaseDao {

	@Inject
	public SomeDao(SomeOneUtil util) {
		super(util);
	}
	

}
