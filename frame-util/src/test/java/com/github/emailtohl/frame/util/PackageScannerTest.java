package com.github.emailtohl.frame.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

public class PackageScannerTest {

	@Test
	public void testGetClasses() {
		Set<Class<?>> set = PackageScanner.getClasses("com.github.emailtohl.frame");
		assertFalse(set.isEmpty());
		assertTrue(set.contains(PackageScanner.class));
	}
	
	@Test
	public void testScanJar() {
		Set<Class<?>> set = PackageScanner.getClasses("org.junit");
		assertFalse(set.isEmpty());
		assertTrue(set.contains(Test.class));
	}

}
