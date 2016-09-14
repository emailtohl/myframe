package com.github.emailtohl.frame.ioc2;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.github.emailtohl.frame.util.BeanTools;

public class InstanceModelTest {
	Context context = new Context("com.github.emailtohl.frame.ioc.testsite");
	
	@Before
	public void setUp() {
		
	}

	@Test
	public void testCompareTo() {
		BeanTools.getDeclaredField(context, "");
	}

}
