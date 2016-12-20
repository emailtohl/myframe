package com.github.emailtohl.frame.util;

import static com.github.emailtohl.frame.util.CommonUtil.isEmpty;
import static com.github.emailtohl.frame.util.CommonUtil.join;
import static com.github.emailtohl.frame.util.CommonUtil.mergeArray;
import static com.github.emailtohl.frame.util.CommonUtil.unescape;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CommonUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIsEmpty() {
		assertTrue(isEmpty(null));
		assertTrue(isEmpty(""));
		assertTrue(isEmpty(0));
		assertTrue(isEmpty(false));
		assertTrue(isEmpty((byte) 0));
		assertTrue(isEmpty(new Byte((byte) 0)));
		assertTrue(isEmpty(' '));
		assertTrue(isEmpty(new Character(' ')));
		assertTrue(isEmpty(new HashSet<Object>()));
		assertTrue(isEmpty(new HashMap<Object, Object>()));
		assertTrue(isEmpty(new String[] {}));
	}

	@Test
	public void testJoinStringArrayString() {
		String s = join(new String[] {"abc", "bcd", "edf"}, ".");
		assertEquals("abc.bcd.edf", s);
	}

	@Test
	public void testJoinCollectionOfStringString() {
		String s = join(Arrays.asList("abc", "bcd", "edf"), ".");
		assertEquals("abc.bcd.edf", s);
	}

	@Test
	public void testMergeArray() {
		Object[] s = mergeArray(new String[] {"abc", "bcd", "edf"}, new String[] {"ghi", "jkl", "mno"});
		assertTrue(s.length == 6);
	}

	@Test
	public void testUnescape() {
		String s = unescape("%u60A8%u597D%uFF0C%u4E16%u754C");
		assertEquals("您好，世界", s);
	}

}
