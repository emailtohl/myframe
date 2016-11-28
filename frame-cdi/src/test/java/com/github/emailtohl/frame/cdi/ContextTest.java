package com.github.emailtohl.frame.cdi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.github.emailtohl.frame.cdi.testsite.dao.SomeDao;
import com.github.emailtohl.frame.cdi.testsite.service.OtherService;
import com.github.emailtohl.frame.cdi.testsite.service.SomeService;
import com.github.emailtohl.frame.cdi.testsite.util.OtherUtil;
import com.github.emailtohl.frame.cdi.testsite.util.SomeOneUtil;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ContextTest {

	static Context context;
	
	@Test
	public void test001Context() {
		context = new Context("com.github.emailtohl.frame.cdi");
		assertNotNull(context.getInstance("someController"));
		assertNotNull(context.getInstance("someRepository"));
		assertNotNull(context.getInstance("someOneUtil"));
		assertNotNull(context.getInstance(SomeService.class));
		assertNotNull(context.getInstance(OtherService.class));
		assertNotNull(context.getInstance("someRepository", SomeDao.class));
	}

	@Test
	public void test002RegisterOne() {
		OtherUtil otherUtil = new OtherUtil();
		context.register("otherUtil", otherUtil);
		SomeOneUtil someOneUtil = context.getInstance(OtherUtil.class).getSomeOneUtil();
		assertNotNull(someOneUtil);
		assertEquals(context.getInstance("someOneUtil"), someOneUtil);
	}
	
	@Test
	public void test003RegisterAll() {
		Context c = new Context();
		c.register("com.github.emailtohl.frame.cdi.testsite");
		assertNotNull(c.getInstance("someController"));
		assertNotNull(c.getInstance("someRepository"));
		assertNotNull(c.getInstance("someOneUtil"));
		assertNotNull(c.getInstance(SomeService.class));
		assertNotNull(c.getInstance(OtherService.class));
		assertNotNull(c.getInstance("someRepository", SomeDao.class));
	}

}
