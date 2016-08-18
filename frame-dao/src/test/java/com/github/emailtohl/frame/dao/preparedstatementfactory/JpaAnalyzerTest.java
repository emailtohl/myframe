package com.github.emailtohl.frame.dao.preparedstatementfactory;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.emailtohl.frame.entities.Employee;
import com.github.emailtohl.frame.entities.Person.Gender;
import com.github.emailtohl.frame.entities.User;

public class JpaAnalyzerTest {
	OrmAnalyzer ormAnalyzer;
	
	@Before
	public void setUp() throws Exception {
		ormAnalyzer = new JpaAnalyzer();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testParse() {
		User user = new User();
		user.setAge(12);
		user.setAuthority("ADMIN");
		user.setEmail("foo@test.com");
		user.setEnabled(true);
		user.setGender(Gender.MAN);
		user.setIcon(null);
		user.setId(123);
		user.setName("foo");
		user.setPassword("123456");
		user.setUsername("bar");
		SqlAndArgs sa = ormAnalyzer.parse(user);
		_assert(sa);
		
		Employee emp = new Employee();
		emp.setAge(12);
		emp.setAuthority("ADMIN");
		emp.setEmail("foo@test.com");
		emp.setEnabled(true);
		emp.setGender(Gender.MAN);
		emp.setIcon(null);
		emp.setId(123);
		emp.setName("foo");
		emp.setPassword("123456");
		emp.setUsername("bar");
		sa = null;
		sa = ormAnalyzer.parse(emp);
		_assert(sa);
	}

	private void _assert(SqlAndArgs sa) {
		Map<String, PropertyBean> map = sa.getColumnPropertyMap();
		for (Entry<String, PropertyBean> entry : map.entrySet()) {
			System.out.println(entry.toString());
		}
		Assert.assertTrue("t_user".equals(sa.getTableName()));
		Assert.assertTrue("id".equals(sa.getKeyColumnLabel()));
		Assert.assertTrue(new Integer(123).equals(map.get(sa.getKeyColumnLabel()).getPropertyValue()));
		Assert.assertSame("bar", map.get("nickname").getPropertyValue());
		Assert.assertSame(Gender.MAN, map.get("gender").getPropertyValue());
		Assert.assertNull(sa.getMarkDeletedColumnLabel());
	}
}
