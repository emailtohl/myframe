package com.github.emailtohl.frame.ioc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.github.emailtohl.frame.ioc.Context;
import com.github.emailtohl.frame.ioc.InstanceModel;
import com.github.emailtohl.frame.ioc.testsite.controller.SomeController;
import com.github.emailtohl.frame.ioc.testsite.dao.SomeDao;
import com.github.emailtohl.frame.ioc.testsite.service.OtherService;
import com.github.emailtohl.frame.ioc.testsite.service.OtherServiceImpl;
import com.github.emailtohl.frame.ioc.testsite.service.SomeService;
import com.github.emailtohl.frame.ioc.testsite.service.SomeServiceImpl;
import com.github.emailtohl.frame.ioc.testsite.util.OtherUtil;
import com.github.emailtohl.frame.ioc.testsite.util.SomeOneUtil;
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
	 * 具有依赖顺序的InstanceModel集合
	 */
	static TreeSet<InstanceModel> treeSet;
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
		assertEquals(9, classSet.size());
		m.invoke(context, classSet);
		assertEquals(5, classSet.size());
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
		assertFalse(nameModelMap.containsKey("otherUtil"));
		assertTrue(typeModelMap.containsKey(SomeDao.class));
		assertFalse(typeModelMap.containsKey(OtherUtil.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test003GetDependencies() throws Exception {
		Method m = Context.class.getDeclaredMethod("getTreeSet");
		m.setAccessible(true);
		treeSet = (TreeSet<InstanceModel>) m.invoke(context);
		for (InstanceModel model : treeSet) {
			System.out.println(model);
			if (model.getName().equals("someOneUtil")) {
				assertTrue(model.getDependencies().isEmpty());
			} else if (model.getName().equals("otherServiceImpl")) {
				assertTrue(model.getDependencies().isEmpty());
			} else if (model.getName().equals("someController")) {
				List<Class<?>> ls = Arrays.asList(SomeService.class, OtherService.class, SomeServiceImpl.class,
						OtherServiceImpl.class, SomeDao.class, SomeOneUtil.class);
				assertTrue(model.getDependencies().containsAll(ls));
			} else if (model.getName().equals("someServiceImpl")) {
				assertTrue(model.getDependencies().contains(SomeDao.class));
			} else if (model.getName().equals("someRepository")) {
				assertTrue(model.getDependencies().contains(SomeOneUtil.class));
			}
		}
	}
	
	@Test
	public void test004NewInstance() throws Exception {
		Method m = Context.class.getDeclaredMethod("newInstance", TreeSet.class);
		m.setAccessible(true);
		m.invoke(context, treeSet);
		for (InstanceModel model : instanceModelSet) {
			System.out.println(model);
			assertNotNull(model.getInstance());
			if (model.getName().equals("someOneUtil")) {
			} else if (model.getName().equals("someController")) {
				SomeController someController = (SomeController) model.getInstance();
				assertNotNull(someController.getSomeService());
				assertNotNull(someController.getOtherService());
			} else if (model.getName().equals("someServiceImpl")) {
				SomeServiceImpl someServiceImpl = (SomeServiceImpl) model.getInstance();
				assertNotNull(someServiceImpl.getSomeDao());
			} else if (model.getName().equals("someRepository")) {
				SomeDao someDao = (SomeDao) model.getInstance();
				assertNotNull(someDao.getUtil());
			}
		}
	}
	
	/**
	 * 主要是debug查看构造器的行为
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testNewInstanceByConstructor() throws Exception {
		@Component
		class TestClass {
			@Inject
			public TestClass(@Named("a") int a, @Named("b") long b) {}
		}
		
		Method m = Context.class.getDeclaredMethod("newInstanceByConstructor", Class.class);
		m.setAccessible(true);
		m.invoke(context, TestClass.class);
	}
}

