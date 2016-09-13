package com.github.emailtohl.frame.ioc2;

import static org.junit.Assert.*;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.github.emailtohl.frame.ioc2.Context;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)  
public class ContextTest {

	Context ctx;
	
	@Test
	public void test001Context() {
		ctx = new Context("com.github.emailtohl.frame.ioc");
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
