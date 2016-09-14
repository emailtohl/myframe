package com.github.emailtohl.frame.ioc2;

import static org.junit.Assert.*;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.github.emailtohl.frame.ioc.testsite.service.OtherService;
import com.github.emailtohl.frame.ioc.testsite.service.SomeService;
import com.github.emailtohl.frame.ioc2.Context;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)  
public class ContextTest {

	Context context;
	
	@Test
	public void test001Context() {
		context = new Context("com.github.emailtohl.frame.ioc");
		System.out.println(context.getInstance("someController"));
//		System.out.println(context.getInstance("baseRepository"));
		System.out.println(context.getInstance("someRepository"));
		System.out.println(context.getInstance("someOneUtil"));
		System.out.println(context.getInstance(SomeService.class));
		System.out.println(context.getInstance(OtherService.class));
	}

	@Test
	public void test002Register() {
		fail("Not yet implemented");
	}

	@Test
	public void test003GetInstanceClassOfT() {
		fail("Not yet implemented");
	}

	@Test
	public void test004GetInstanceClassOfTString() {
		fail("Not yet implemented");
	}

	@Test
	public void test005GetInstanceString() {
		fail("Not yet implemented");
	}

}
