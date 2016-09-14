package com.github.emailtohl.frame.ioc2;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.github.emailtohl.frame.ioc.testsite.controller.SomeController;
import com.github.emailtohl.frame.ioc.testsite.dao.SomeDao;
import com.github.emailtohl.frame.ioc.testsite.util.OtherUtil;
import com.github.emailtohl.frame.util.BeanTools;
import com.github.emailtohl.frame.util.PackageScanner;
/**
 * 专门测试私有方法
 * @author helei
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ContextPrivateTest {
	static Context context = new Context();
	/**
	 * 扫描包下的Class集合
	 */
	static Set<Class<?>> classSet;
	/**
	 * 存储所有被Component注解的Class的模型集合
	 */
	static Set<InstanceModel> instanceModelSet;
	/**
	 * 通过name（id）查找实例模型
	 */
	static Map<String, InstanceModel> nameModelMap;
	/**
	 * 通过class查找实例模型
	 */
	static Map<Class<?>, InstanceModel> typeModelMap;
	
	/**
	 * 设置为静态后，就不必在每执行一次测试方法时，重复初始化了
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUp() throws Exception {
		classSet = PackageScanner.getClasses("com.github.emailtohl.frame.ioc.testsite");
		Map<String, Object> map = BeanTools.getFieldNameValueMap(context);
		instanceModelSet = (Set<InstanceModel>) map.get("instanceModelSet");
		nameModelMap = (Map<String, InstanceModel>) map.get("nameModelMap");
		typeModelMap = (Map<Class<?>, InstanceModel>) map.get("typeModelMap");
	}
	
	@Test
	public void test001Filter() throws Exception {
		Method m = Context.class.getDeclaredMethod("filter", Set.class);
		m.setAccessible(true);
		m.invoke(context, classSet);
		assertTrue(classSet.contains(SomeController.class));
		assertFalse(classSet.contains(OtherUtil.class));
	}
	
	@Test
	public void test002AddInstanceModelSet() throws Exception {
		Method m = Context.class.getDeclaredMethod("addInstanceModelSet", Set.class);
		m.setAccessible(true);
		m.invoke(context, classSet);
		System.out.println(instanceModelSet);
		System.out.println(nameModelMap);
		System.out.println(typeModelMap);
		InstanceModel model = new InstanceModel();
		model.setName("someRepository");
		assertTrue(instanceModelSet.contains(model));
		assertTrue(nameModelMap.containsKey("someRepository"));
		assertTrue(typeModelMap.containsKey(SomeDao.class));
	}

}
