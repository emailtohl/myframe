package com.github.emailtohl.frame.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.github.emailtohl.frame.util.entities.Contact;
import com.github.emailtohl.frame.util.entities.Permission;
import com.github.emailtohl.frame.util.entities.Person.Gender;
import com.github.emailtohl.frame.util.entities.Role;
import com.github.emailtohl.frame.util.entities.User;

public class SerializableTest {
	Serializing util = new Serializing();
	class TestUser extends User {
		private static final long serialVersionUID = -8145051855829227968L;
		Contact[] testArrayContact = new Contact[] {new Contact("address", "email", "telephone")};
		public final Map<String, Contact> testMapContact = new HashMap<String, Contact>();
		Calendar c = Calendar.getInstance(Locale.CHINESE);
	}
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	Permission p1 = new Permission(), p2 = new Permission(), p3 = new Permission(), p4 = new Permission();
	Role admin = new Role(), user = new Role();
	TestUser emailtohl = new TestUser(), foo = new TestUser(), bar = new TestUser();
	Relation1 r1 = new Relation1();
	Relation2 r2 = new Relation2();

	@Test
	public void testToString() {
		Timestamp t = Timestamp.valueOf("2016-04-18 20:21:00");
		System.out.println(t);
		Date d = new Date();
		System.out.println(d);
		Calendar c = Calendar.getInstance();
		System.out.println(c);
	}
	
	@Test
	public void testToJson() throws ParseException {
		String json = util.toJson(emailtohl);
		System.out.println(json);
		json = util.toJson(foo);
		System.out.println(json);
		json = util.toJson(bar);
		System.out.println(json);
		json = util.toJson(p1);
		System.out.println(json);
		json = util.toJson(p2);
		System.out.println(json);
		json = util.toJson(p3);
		System.out.println(json);
		json = util.toJson(p4);
		System.out.println(json);
		json = util.toJson(admin);
		System.out.println(json);
		json = util.toJson(user);
		System.out.println(json);
		
		json = util.toJson(r1);
		System.out.println(json);
		json = util.toJson(r2);
		System.out.println(json);
	}

	@Test
	public void testToXml() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc;
		
		String xml = util.toXml(emailtohl, true);
		System.out.println(xml);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(xml.getBytes());
		out.flush();
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		doc = builder.parse(in);
		assertEquals("true", doc.getElementsByTagName("enabled").item(0).getTextContent());
		out.close();
		in.close();
		
		xml = util.toXml(r1, true);
		System.out.println(xml);
		out = new ByteArrayOutputStream();
		out.write(xml.getBytes());
		out.flush();
		in = new ByteArrayInputStream(out.toByteArray());
		doc = builder.parse(in);
		assertTrue(doc.getElementsByTagName("relation1").item(0).getTextContent().length() == 0);
		out.close();
		in.close();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFromJson() {
		String json = util.toJson(foo);
		Object o = util.fromJson("{  }");
		o = util.fromJson(" [  ] ");
		o = util.fromJson(json);
		System.out.println(o);
		assertEquals("true", ((Map<String, Object>) o).get("enabled"));
		
		json = util.toJson(r1);
		o = util.fromJson(json);
		System.out.println(o);
		assertNull(((Map<String, Object>) o).get("relation1"));
		
		json = util.toJson(r2);
		o = util.fromJson(json);
		System.out.println(o);
	}
	
	@Test
	public void testJava8Time() {
		class NewTime extends User {
			private static final long serialVersionUID = -3315653063724798737L;
			@SuppressWarnings("unused")
			Instant timestamp = Instant.now();
			@SuppressWarnings("unused")
			Duration thirtyDay = Duration.ofDays(30);
		}
		NewTime nt = new NewTime();
		String json = util.toJson(nt);
		System.out.println(json);
		Object o = util.fromJson(json);
		System.out.println(o);
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
		
		emailtohl.testMapContact.put("contact", c1);
		foo.testMapContact.put("contact", c2);
		bar.testMapContact.put("contact", c3);
		
		r1.setRelation2(r2);
		r2.setRelation1(r1);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	public static class Relation1 implements Serializable {
		private static final long serialVersionUID = 9107784769781908565L;
		private String me = "Relation1";
		private Relation2 relation2;
		public String getMe() {
			return me;
		}
		public void setMe(String me) {
			this.me = me;
		}
		public Relation2 getRelation2() {
			return relation2;
		}
		public void setRelation2(Relation2 relation2) {
			this.relation2 = relation2;
		}
	}
	public static class Relation2 implements Serializable {
		private static final long serialVersionUID = -3114234776366877130L;
		private String me = "Relation2";
		private Relation1 relation1;
		public String getMe() {
			return me;
		}
		public void setMe(String me) {
			this.me = me;
		}
		public Relation1 getRelation1() {
			return relation1;
		}
		public void setRelation1(Relation1 relation1) {
			this.relation1 = relation1;
		}
	}
}
