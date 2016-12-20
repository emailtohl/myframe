package com.github.emailtohl.frame.util;

import static com.github.emailtohl.frame.util.BeanUtil.compareProperties;
import static com.github.emailtohl.frame.util.BeanUtil.copyList;
import static com.github.emailtohl.frame.util.BeanUtil.copyProperties;
import static com.github.emailtohl.frame.util.BeanUtil.deepCopy;
import static com.github.emailtohl.frame.util.BeanUtil.getAnnotation;
import static com.github.emailtohl.frame.util.BeanUtil.getDeclaredField;
import static com.github.emailtohl.frame.util.BeanUtil.getFieldMap;
import static com.github.emailtohl.frame.util.BeanUtil.getFieldNameValueMap;
import static com.github.emailtohl.frame.util.BeanUtil.getModifiedField;
import static com.github.emailtohl.frame.util.BeanUtil.getPropertyMap;
import static com.github.emailtohl.frame.util.BeanUtil.getPropertyNameValueMap;
import static com.github.emailtohl.frame.util.BeanUtil.injectField;
import static com.github.emailtohl.frame.util.BeanUtil.injectFieldWithString;
import static com.github.emailtohl.frame.util.BeanUtil.merge;
import static com.github.emailtohl.frame.util.BeanUtil.saveListToMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Embedded;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.emailtohl.frame.util.SerializableTest.Relation1;
import com.github.emailtohl.frame.util.SerializableTest.Relation2;
import com.github.emailtohl.frame.util.entities.Contact;
import com.github.emailtohl.frame.util.entities.Permission;
import com.github.emailtohl.frame.util.entities.Person;
import com.github.emailtohl.frame.util.entities.Person.Gender;
import com.github.emailtohl.frame.util.entities.Role;
import com.github.emailtohl.frame.util.entities.User;

public class BeanUtilTest {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	Permission p1 = new Permission(), p2 = new Permission(), p3 = new Permission(), p4 = new Permission();
	Role admin = new Role(), user = new Role();
	User emailtohl = new User(), foo = new User(), bar = new User();

	@Test
	public void testGetFieldNameValueMap() {
		Map<String, Object> map = getFieldNameValueMap(emailtohl);
		System.out.println(map);
		assertTrue((boolean) map.get("enabled"));
		assertEquals("emailtohl@163.com", map.get("nickname"));
		assertEquals(emailtohl.getContact(), map.get("contact"));
		assertEquals(emailtohl.getRoles(), map.get("roles"));
	}
	
	@Test
	public void testGetPropertyMap() {
		Map<String, PropertyDescriptor> map = getPropertyMap(emailtohl);
		System.out.println(map.keySet());
		assertNotNull(map.get("id"));
	}
	
	@Test
	public void testGetPropertyNameValueMap() {
		Map<String, Object> map = getPropertyNameValueMap(emailtohl);
		System.out.println(map);
		assertEquals("hl", map.get("name"));
	}

	@Test
	public void testGetFieldMap() {
		Pattern p = Pattern.compile("<(\\w+(\\.\\w+)*)>");
		Map<String, Field> map = getFieldMap(emailtohl);
		Field f = map.get("authority");
		Type t = f.getGenericType();
		System.out.println(t);
		
		Matcher m = p.matcher(t.toString());
		if (m.find())
			System.out.println(m.group(1));
		assertEquals("java.lang.String", m.group(1));
		
		f = map.get("nickname");
		t = f.getGenericType();
		System.out.println(t);
		
		m = p.matcher(t.toString());
		if (m.find())
			System.out.println(m.group(1));
	}

	@Test
	public void testGetDeclaredField() {
		Field f = getDeclaredField(foo, "authority");
		Class<?> c = f.getType();
		assertTrue(c.isAssignableFrom(List.class));
	}

	@Test
	public void testGetAnnotation() throws IntrospectionException {
		for (PropertyDescriptor p : Introspector.getBeanInfo(User.class, Object.class).getPropertyDescriptors()) {
			getAnnotation(p, ManyToOne.class);
			getAnnotation(p, OneToOne.class);
			getAnnotation(p, Embedded.class);
		}
	}
	
	@Test
	public void testCopyProperties() {
		Person p = copyProperties(bar, Person.class);
		System.out.println(p);
		assertEquals("bar", p.getName());
	}

	@Test
	public void testCopyList() {
		List<User> ls = Arrays.asList(emailtohl, foo, bar);
		List<Person> lsp = copyList(ls, Person.class);
		System.out.println(lsp);
		assertEquals(Gender.MALE, lsp.get(0).getGender());
	}

	@Test
	public void testDeepCopy() {
		User clone;
		clone = copyProperties(foo, User.class);
		// 普通复制的是对象的引用，所以内存地址相等
		assertTrue(clone.getAuthority() == foo.getAuthority());
		// 深度复制出来的对象，内存地址不相等
		clone = deepCopy(foo);
		System.out.println(clone);
		assertFalse(clone.getAuthority() == foo.getAuthority());

		Relation1 r1 = new Relation1();
		Relation2 r2 = new Relation2();
		r1.setRelation2(r2);
		r2.setRelation1(r1);
		// 测试对象间的交叉引用会不会出现无限递归的情况
		Relation1 rClone = deepCopy(r1);
		System.out.println(rClone);
		assertFalse(rClone.getRelation2() == r1.getRelation2());
	}

	@Test
	public void testGetModifiedField() {
		Map<String, Object> map = getModifiedField(foo, bar);
		System.out.println(map);
		assertTrue(map.containsKey("nickname"));
	}

	@Test
	public void testInjectField() {
		class TestBean extends User {
			private static final long serialVersionUID = 2602043693538454711L;
			int i = 1;
			boolean b1 = true;
			Boolean b2 = true;
			byte by1 = 1;
			Byte by2 = 1;
			char c1 = 1;
			Character c2 = 1;
			@Override
			public String toString() {
				return "TestBean [i=" + i + ", b1=" + b1 + ", b2=" + b2 + ", by1=" + by1 + ", by2=" + by2 + ", c1=" + c1
						+ ", c2=" + c2 + "]";
			}
		}
		TestBean t = new TestBean();
		Map<String, Field> map = getFieldMap(t);
		injectField(map.get("i"), t, 2);
		injectField(map.get("b1"), t, false);
		injectField(map.get("b2"), t, false);
		injectField(map.get("by1"), t, 2);
		injectField(map.get("by2"), t, 2);
		injectField(map.get("c1"), t, 97);
		injectField(map.get("c2"), t, 97);
		System.out.println(t);
		assertEquals(2, t.i);
		assertFalse(t.b1);
		assertFalse(t.b2);
		assertTrue(2 == t.by1);
		assertTrue(2 == t.by2);
		assertEquals(97, t.c1);
		assertEquals(new Character('a'), t.c2);
	}

	@Test
	public void testInjectFieldWithString() {
		class TestBean extends User {
			private static final long serialVersionUID = 6465392695061494961L;
			int i = 1;
			boolean b = true;
			byte by = 1;
			char c = 1;
			Gender gender = Gender.UNSPECIFIED;

			@Override
			public String toString() {
				return "TestBean [i=" + i + ", b=" + b + ", by=" + by + ", c=" + c + ", gender=" + gender + "]";
			}
		}
		TestBean t = new TestBean();
		Map<String, Field> map = getFieldMap(t);
		injectFieldWithString(map.get("i"), t, "2");
		injectFieldWithString(map.get("b"), t, "false");
		injectFieldWithString(map.get("by"), t, "2");
		injectFieldWithString(map.get("c"), t, "a");
		injectFieldWithString(map.get("gender"), t, "FEMALE");
		injectFieldWithString(map.get("birthday"), t, "1982-02-12");
		System.out.println(t);
		assertEquals(2, t.i);
		assertFalse(t.b);
		assertTrue(2 == t.by);
		assertEquals('a', t.c);
		assertEquals(Gender.FEMALE, t.gender);
		assertEquals(Date.valueOf("1982-02-12"), t.getBirthday());
	}

	@Test
	public void testInjectPropertyWithString() throws IntrospectionException {
		@SuppressWarnings("unused")
		class TestBean extends User {
			private static final long serialVersionUID = -3673169944292160591L;
			int i = 1;
			boolean b = true;
			byte by = 1;
			char c = 1;
			Gender gender = Gender.UNSPECIFIED;

			public int getI() {
				return i;
			}
			public void setI(int i) {
				this.i = i;
			}
			public boolean isB() {
				return b;
			}
			public void setB(boolean b) {
				this.b = b;
			}
			public byte getBy() {
				return by;
			}
			public void setBy(byte by) {
				this.by = by;
			}
			public char getC() {
				return c;
			}
			public void setC(char c) {
				this.c = c;
			}
			public Gender getGender() {
				return gender;
			}
			public void setGender(Gender gender) {
				this.gender = gender;
			}
			@Override
			public String toString() {
				return "TestBean [i=" + i + ", b=" + b + ", by=" + by + ", c=" + c + ", gender=" + gender + "]";
			}
		}

		TestBean t = new TestBean();
		for (PropertyDescriptor pd : Introspector.getBeanInfo(TestBean.class).getPropertyDescriptors()) {
			String name = pd.getName();
			switch (name) {
			case "i":
				BeanUtil.injectPropertyWithString(pd, t, "2");
				break;
			case "b":
				BeanUtil.injectPropertyWithString(pd, t, "false");
				break;
			case "by":
				BeanUtil.injectPropertyWithString(pd, t, "2");
				break;
			case "c":
				BeanUtil.injectPropertyWithString(pd, t, "a");
				break;
			case "gender":
				BeanUtil.injectPropertyWithString(pd, t, "FEMALE");
				break;
			case "birthday":
				BeanUtil.injectPropertyWithString(pd, t, "1982-02-12");
				break;
			default:

			}
		}
		System.out.println(t);
		assertEquals(2, t.i);
		assertFalse(t.b);
		assertTrue(2 == t.by);
		assertEquals('a', t.c);
		assertEquals(Gender.FEMALE, t.gender);
		assertEquals(Date.valueOf("1982-02-12"), t.getBirthday());
	}

	@Test
	public void testMerge() {
		Person p = new Person();
		p = merge(p, emailtohl, foo, bar);
		System.out.println(p);
		assertEquals(p.getName(), bar.getName());
	}
	
	@Test
	public void testSaveListToMap() {
		emailtohl.setId(1L);
		foo.setId(2L);
		bar.setId(3L);
		Map<Long, User> map = saveListToMap(Arrays.asList(emailtohl, foo, bar), "id");
		System.out.println(map);
		assertEquals(emailtohl, map.get(1L));
		assertEquals(foo, map.get(2L));
		assertEquals(bar, map.get(3L));
	}
	
	class TestGenericType {
		Set<Gender> set;
		List<Role> ls;
		Map<Integer, Contact> map;
		public Set<Gender> getSet() {
			return set;
		}
		public void setSet(Set<Gender> set) {
			this.set = set;
		}
		public List<Role> getLs() {
			return ls;
		}
		public void setLs(List<Role> ls) {
			this.ls = ls;
		}
		public Map<Integer, Contact> getMap() {
			return map;
		}
		public void setMap(Map<Integer, Contact> map) {
			this.map = map;
		}
	}
	
	@Test
	public void testGetGenericClassField() throws NoSuchFieldException, SecurityException {
		Class<TestGenericType> c = TestGenericType.class;
		assertEquals(Gender.class, BeanUtil.getGenericClass(c.getDeclaredField("set"))[0]);
		assertEquals(Role.class, BeanUtil.getGenericClass(c.getDeclaredField("ls"))[0]);
		assertEquals(Integer.class, BeanUtil.getGenericClass(c.getDeclaredField("map"))[0]);
		assertEquals(Contact.class, BeanUtil.getGenericClass(c.getDeclaredField("map"))[1]);
	}

	@Test
	public void testGetGenericClassPropertyDescriptor() throws IntrospectionException {
		BeanInfo bi = Introspector.getBeanInfo(TestGenericType.class, Object.class);
		for (PropertyDescriptor p : bi.getPropertyDescriptors()) {
			if ("set".equals(p.getName())) {
				assertEquals(Gender.class, BeanUtil.getGenericClass(p)[0]);
			}
			if ("ls".equals(p.getName())) {
				assertEquals(Role.class, BeanUtil.getGenericClass(p)[0]);
			}
			if ("map".equals(p.getName())) {
				assertEquals(Integer.class, BeanUtil.getGenericClass(p)[0]);
			}
			if ("map".equals(p.getName())) {
				assertEquals(Contact.class, BeanUtil.getGenericClass(p)[1]);
			}
		}
	}

	@Test
	public void testCompareProperties() {
		boolean b = compareProperties(foo, new String[] {"gender", "roles"}, emailtohl);
		assertTrue(b);
	}
	
	@Before
	public void setUp() throws Exception {
		p1.setName("增加");
		p2.setName("删除");
		p3.setName("修改");
		p4.setName("查询");
		
		admin.setName("ADMIN");
		user.setName("USER");
		
		admin.getPermissions().addAll(Arrays.asList(p1, p2, p3, p4));
		user.getPermissions().addAll(Arrays.asList(p4));
		
		p1.getRoles().addAll(Arrays.asList(admin));
		p2.getRoles().addAll(Arrays.asList(admin));
		p3.getRoles().addAll(Arrays.asList(admin));
		p4.getRoles().addAll(Arrays.asList(admin, user));

		Contact c1 = new Contact("重庆", "emailtohl@163.com", "18702392682")
				, c2 = new Contact("重庆", "foo@test.com", "18702392680")
				, c3 = new Contact("重庆", "bar@test.com", "18702392681");
		
		emailtohl.setName("hl");
		emailtohl.setBirthday(sdf.parse("1982-02-12"));
		emailtohl.setContact(c1);
		emailtohl.setGender(Gender.MALE);
		emailtohl.getRoles().add(admin);
		emailtohl.getRoles().add(user);
		
		emailtohl.setNickname("emailtohl@163.com");
		emailtohl.setEnabled(true);
		emailtohl.setIcon(null);
		emailtohl.setPassword("123456");
		emailtohl.getAuthority().addAll(Arrays.asList("ADMIN", "USER"));
		emailtohl.setDescription("test");
		
		foo.setName("foo");
		foo.setBirthday(sdf.parse("1985-03-17"));
		foo.setContact(c2);
		foo.setGender(Gender.MALE);
		foo.getRoles().add(admin);
		foo.getRoles().add(user);
		
		foo.setNickname("foo@test.com");
		foo.setEnabled(true);
		foo.setPassword("123456");
		foo.getAuthority().addAll(Arrays.asList("ADMIN", "USER"));
		foo.setDescription("test");
		
		bar.setName("bar");
		bar.setBirthday(sdf.parse("1985-05-22"));
		bar.setContact(c3);
		bar.setGender(Gender.FEMALE);
		bar.getRoles().add(user);
		
		bar.setNickname("bar@test.com");
		bar.setEnabled(true);
		bar.setPassword("123456");
		bar.getAuthority().addAll(Arrays.asList("USER"));
		bar.setDescription("test");
		
		admin.getUsers().addAll(Arrays.asList(emailtohl, foo));
		user.getUsers().addAll(Arrays.asList(emailtohl, foo, bar));
	}

	@After
	public void tearDown() throws Exception {
	}
}
