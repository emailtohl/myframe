package com.github.emailtohl.frame.ioc2;

import static org.junit.Assert.*;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.github.emailtohl.frame.ioc.testsite.dao.SomeDao;
import com.github.emailtohl.frame.ioc.testsite.service.OtherService;
import com.github.emailtohl.frame.ioc.testsite.service.SomeService;
import com.github.emailtohl.frame.ioc.testsite.util.OtherUtil;
import com.github.emailtohl.frame.ioc.testsite.util.SomeOneUtil;
import com.github.emailtohl.frame.ioc2.Context;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ContextTest {

	static Context context;
	
	@Test
	public void test001Context() {
		context = new Context("com.github.emailtohl.frame.ioc");
		assertNotNull(context.getInstance("someController"));
		assertNotNull(context.getInstance("someRepository"));
		assertNotNull(context.getInstance("someOneUtil"));
		assertNotNull(context.getInstance(SomeService.class));
		assertNotNull(context.getInstance(OtherService.class));
		assertNotNull(context.getInstance("someRepository", SomeDao.class));
	}

	@Test
	public void test002Register() {
		OtherUtil otherUtil = new OtherUtil();
		context.register("otherUtil", otherUtil);
		SomeOneUtil someOneUtil = context.getInstance(OtherUtil.class).getSomeOneUtil();
		assertNotNull(someOneUtil);
		assertEquals(context.getInstance("someOneUtil"), someOneUtil);
	}

}
