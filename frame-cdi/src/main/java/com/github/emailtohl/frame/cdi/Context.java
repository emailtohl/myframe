package com.github.emailtohl.frame.cdi;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import com.github.emailtohl.frame.transition.TransitionProxy;
import com.github.emailtohl.frame.util.BeanUtil;
import com.github.emailtohl.frame.util.PackageScanner;

/**
 * cdi的容器，模仿Spring，可以通过name（id）获取实例，也可以通过类型获取实例
 * 实现反转控制和依赖注入功能
 * 
 * @author HeLei
 * @date 2016.09.15 中秋节
 */
public class Context {
	private static final Logger logger = LogManager.getLogManager().getLogger(Context.class.getName());
	/**
	 * 存储所有被Component注解的Class的模型集合
	 */
	private Set<InstanceModel> instanceModelSet = new HashSet<InstanceModel>();
	/**
	 * 通过name（id）查找实例模型
	 */
	private Map<String, InstanceModel> nameModelMap = new HashMap<String, InstanceModel>();
	/**
	 * 通过class查找实例模型
	 */
	private Map<Class<?>, InstanceModel> typeModelMap = new HashMap<Class<?>, InstanceModel>();

	public Context() {
		super();
	}

	public Context(String packagePath) {
		super();
		Set<Class<?>> classSet = PackageScanner.getClasses(packagePath);
		// 第一步，过滤无关的Class
		filter(classSet);
		// 第二步，为每个被Component注解的Class创建InstanceModel
		addInstanceModelSet(classSet);
		// 第三步，对依赖关系进行建模
		TreeSet<InstanceModel> dependenciesSet = getTreeSet();
		// 第四步，实例化，依赖注入
		newInstance(dependenciesSet);
	}
	
	/**
	 * 将包下所有注解了Component的类提取到容器中管理
	 * @param packagePath 包名
	 */
	public synchronized void register(String packagePath) {
		Set<Class<?>> classSet = PackageScanner.getClasses(packagePath);
		// 第一步，过滤无关的Class
		filter(classSet);
		// 第二步，为每个被Component注解的Class创建InstanceModel
		addInstanceModelSet(classSet);
		// 第三步，对依赖关系进行建模
		TreeSet<InstanceModel> dependenciesSet = getTreeSet();
		// 第四步，实例化，依赖注入
		newInstance(dependenciesSet);
	}
	
	/**
	 * 将实例纳入容器统一管理，所有依赖将被自动注入
	 * @param name 实例名
	 * @param instance 实例对象
	 */
	public synchronized void register(String name, Object instance) {
		if (nameModelMap.containsKey(name)) {
			throw new RuntimeException("已有相同实例名");
		}
		Class<?> clz = instance.getClass();
		InstanceModel model = new InstanceModel();
		model.setInstance(instance);
		model.setName(name);
		model.setType(clz);
		// 下面创建依赖列表
		Set<Class<?>> classSet = new HashSet<Class<?>>();
//		既然已经实例化了，所以不依赖构造器的参数
//		classSet.addAll(getDependenciesByConstructor(clz));
		classSet.addAll(getDependenciesByProperties(clz));
		classSet.addAll(getDependenciesByField(clz));
		Set<Class<?>> concrete = concrete(classSet);
		model.getDependencies().addAll(concrete);
		instanceModelSet.add(model);
		// 为该实例执行依赖注入
		injectProperty(instance);
		injectField(instance);
		// 最后添加进创建好的表中
		nameModelMap.put(name, model);
		typeModelMap.put(clz, model);
	}
	
	/**
	 * 通过实例名查询实例
	 * @param name 实例名
	 * @return 实例，若未查到，则返回null
	 */
	public Object getInstance(String name) {
		Object instance = null;
		InstanceModel m = nameModelMap.get(name);
		if (m != null) {
			instance = m.getInstance();
		}
		// 若Transactional注解在接口上，这种通过name（id）获取的实例的方式，不会返回代理
		if (instance != null && instance.getClass().getAnnotation(Transactional.class) != null) {
			instance = TransitionProxy.getProxy(instance);
		}
		return instance;
	}
	
	/**
	 * 通过类型查询实例对象
	 * 
	 * @param clz 类型，包括interface接口
	 * @return 实例对象，如果查找有多个实例对象，则抛运行时异常
	 */
	@SuppressWarnings("unchecked")
	public <T> T getInstance(Class<T> clz) {
		Map<String, Object> instanceMap = getInstanceMap(clz);
		int size = instanceMap.size();
		if (size == 0) {
			return null;
		} else if (size > 1) {
			throw new RuntimeException("有多个实例满足该类型：" + instanceMap);
		}
		T instance = (T) instanceMap.values().iterator().next();
		// 若Transactional注解在接口上，则可以返回代理
		if (clz.getAnnotation(Transactional.class) != null && instance != null) {
			instance = TransitionProxy.getProxy(instance);
		}
		return instance;
	}
	
	/**
	 * 通过name和类型查询实例对象
	 * @param name
	 * @param clz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name, Class<T> clz) {
		Object instance = null;
		if (name.isEmpty()) {// 如果未注明了name（id），则通过类型来获取
			InstanceModel m = typeModelMap.get(clz);
			if (m != null) {
				instance = m.getInstance();
			} else {// 如果类型获取不到，则扫描容器查找子类
				instance = getInstance(clz);
			}
		} else {
			InstanceModel m = nameModelMap.get(name);
			if (m != null) {
				instance = m.getInstance();
			}
		}
		// 若Transactional注解在接口上，则可以返回代理
		if (clz.getAnnotation(Transactional.class) != null && instance != null) {
			instance = TransitionProxy.getProxy(instance);
		}
		return (T) instance;
	}
	
	/**
	 * 一个类型下有可能有多个实例，所以返回一个Map，key是实例名，value是实例
	 * @param clz
	 * @return
	 */
	private Map<String, Object> getInstanceMap(Class<?> clz) {
		Map<String, Object> instanceMap = new HashMap<String, Object>();
		for (Entry<Class<?>, InstanceModel> e : typeModelMap.entrySet()) {
			if (clz.isAssignableFrom(e.getKey())) {
				instanceMap.put(e.getValue().getName(), e.getValue().getInstance());
			}
		}
		return instanceMap;
	}
	
	/**
	 * 查找某接口或超类下的实现类集合
	 * @param clz 接口的class
	 * @return 实现类的集合
	 */
	private Set<Class<?>> getDerivedClassSet(Class<?> clz) {
		Set<Class<?>> set = new HashSet<Class<?>>();
		for (InstanceModel m : instanceModelSet) {
			if (clz.isAssignableFrom(m.getType())) {
				set.add(m.getType());
			}
		}
		return set;
	}
	
	/**
	 * 过滤无关的Class
	 */
	private void filter(Set<Class<?>> classSet) {
		Iterator<Class<?>> i = classSet.iterator();
		while (i.hasNext()) {
			Class<?> clz = i.next();
			Component c = clz.getAnnotation(Component.class);
			if (c == null) {
				i.remove();
			}
		}
	}
	
	/**
	 * 添加InstanceModel集合
	 * 此时，只将实例名和class写入该模型中
	 * @param classSet 带有Component注解的Class集合
	 */
	private void addInstanceModelSet(Set<Class<?>> classSet) {
		for (Class<?> clz : classSet) {
			InstanceModel model = new InstanceModel();
			String name = getNameByClass(clz);
			model.setName(name);
			model.setType(clz);
			instanceModelSet.add(model);
			nameModelMap.put(name, model);
			typeModelMap.put(clz, model);
		}
	}
	
	/**
	 * 为依赖关系建模，并得到一个按依赖关系排序的集合
	 */
	private TreeSet<InstanceModel> getTreeSet() {
		TreeSet<InstanceModel> modelSet = new TreeSet<InstanceModel>();
		for (InstanceModel model : instanceModelSet) {
			Set<Class<?>> dependencies = getDependencies(model.getType());
			model.getDependencies().addAll(dependencies);
			modelSet.add(model);
		}
		return modelSet;
	}
	
	/**
	 * 从具体类class中查找其依赖关系
	 * 注意：需要递归查找出依赖的传递关系
	 * @param clz
	 * @return
	 */
	private Set<Class<?>> getDependencies(Class<?> clz) {
		Set<Class<?>> dependencies = new HashSet<Class<?>>();
		// 从构造器查找依赖
		dependencies.addAll(getDependenciesByConstructor(clz));
		// 从JavaBean属性查找依赖
		dependencies.addAll(getDependenciesByProperties(clz));
		// 从Field字段查找依赖
		dependencies.addAll(getDependenciesByField(clz));
		// 过滤掉接口，获取对具体类的依赖
		Set<Class<?>> concrete = concrete(dependencies);
		dependencies.addAll(concrete);
		// 继续寻找依赖的依赖
		for (Class<?> c : concrete) {
			dependencies.addAll(getDependencies(c));
		}
		return dependencies;
	}
	
	/**
	 * 通过注解的类名获取实例的name（id）
	 * 若不存在，则返回空字符串
	 * @param clz
	 * @return
	 */
	private String getNameByClass(Class<?> clz) {
		Component c = clz.getAnnotation(Component.class);
		if (c == null) {
			return "";
		}
		String name = c.name();
		if (name.isEmpty()) {
			name = clz.getSimpleName();
			name = name.substring(0, 1).toLowerCase() + name.substring(1);
		}
		return name;
	}
	
	/**
	 * 从构造器中获取依赖关系
	 * @param clz
	 */
	private Set<Class<?>> getDependenciesByConstructor(Class<?> clz) {
		Set<Class<?>> set = new HashSet<Class<?>>();
		for (Constructor<?> constructor : clz.getDeclaredConstructors()) {
			Component c = constructor.getAnnotation(Component.class);
			if (c == null) {
				continue;
			}
			constructor.setAccessible(true);
			for (Class<?> cl : constructor.getParameterTypes()) {
				set.add(cl);
			}
		}
		return set;
	}
	
	/**
	 * 从JavaBean属性中获取依赖关系
	 * @param clz
	 */
	private Set<Class<?>> getDependenciesByProperties(Class<?> clz) {
		Set<Class<?>> set = new HashSet<Class<?>>();
		try {
			for (PropertyDescriptor p : Introspector.getBeanInfo(clz, Object.class).getPropertyDescriptors()) {
				Method m = p.getWriteMethod();
				if (m != null) {
					Inject inject = p.getWriteMethod().getAnnotation(Inject.class);
					if (inject != null) {
						set.add(p.getPropertyType());
					}
				}
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "分析Bean的Setter方法发生异常，依赖注入可能失败", e);
		}
		return set;
	}
	
	/**
	 * 从Field字段中获取依赖关系
	 * @param clz
	 */
	private Set<Class<?>> getDependenciesByField(Class<?> clz) {
		Set<Class<?>> set = new HashSet<Class<?>>();
		Class<?> clazz = clz;
		while (clazz != Object.class) {
			Field[] fields = clazz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Inject inject = fields[i].getAnnotation(Inject.class);
				if (inject != null) {
					set.add(fields[i].getType());
				}
			}
			clazz = clazz.getSuperclass();
		}
		return set;
	}
	
	/**
	 * 当某个class依赖接口时，需要继续查找依赖的具体类
	 * 
	 * @param dependencies 某个类所依赖的Class集合，该集合包括接口、抽象类等
	 * @return 具体的依赖关系
	 */
	private Set<Class<?>> concrete(Set<Class<?>> dependencies) {
		Set<Class<?>> concrete = new HashSet<Class<?>>();
		for (Class<?> c : dependencies) {
			if (typeModelMap.containsKey(c)) {
				concrete.add(c);
			} else {// 如果不存在，则可能是接口，这就需要在容器中查询哪些class是该接口的实现类
				concrete.addAll(getDerivedClassSet(c));
			}
		}
		return concrete;
	}
	
	/**
	 * 按照依赖顺序，进行实例化，同时将具体实例写进InstanceModel中
	 * @param treeSet
	 */
	private void newInstance(TreeSet<InstanceModel> treeSet) {
		for (InstanceModel model : treeSet) {
			Object instance = model.getInstance();
			if (instance == null) {// 还有种情况是构造时没有扫描包，先将部分实例注册进容器
				Class<?> clz = model.getType();
				// 首先查看构造器是否有注入注解
				instance = newInstanceByConstructor(clz);
				// 如果不是构造器创建的实例，那么就直接new一个出来
				if (instance == null) {
					try {
						instance = clz.newInstance();
					} catch (InstantiationException | IllegalAccessException e1) {
						e1.printStackTrace();
						logger.log(Level.SEVERE, "默认构造器创建实例时发生异常，需提供无惨的默认构造器", e1);
						throw new RuntimeException();
					}
				}
			}
			// 创建好实例后，先调用JavaBean的setter方法注入实例
			injectProperty(instance);
			// 最后在Field字段中注入
			injectField(instance);
			// 最后，将具体实例存储进InstanceModel中
			model.setInstance(instance);
		}
	}
	
	/**
	 * 通过构造器来创建实例
	 * @param clz
	 * @return
	 */
	private Object newInstanceByConstructor(Class<?> clz) {
		Object instance = null;
		for (Constructor<?> constructor : clz.getDeclaredConstructors()) {
			Inject inject = constructor.getAnnotation(Inject.class);
			if (inject == null) {
				continue;
			}
			constructor.setAccessible(true);
			Object[] initargs = new Object[constructor.getParameterCount()];
			Annotation[][] pas = constructor.getParameterAnnotations();
			int i = 0;
			for (Class<?> pts : constructor.getParameterTypes()) {
				String name = "";
				for (Annotation a : pas[i]) {
					if (Named.class.equals(a.annotationType())) {
						name = ((Named) a).value();
					}
				}
				Object injectObj = getInstance(name, pts);
				if (injectObj == null) {
					throw new RuntimeException("未找到Bean实例");
				}
				initargs[i] = injectObj;
				i++;
			}
			try {
				instance = constructor.newInstance(initargs);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e1) {
				e1.printStackTrace();
				logger.log(Level.SEVERE, "构造器创建实例时发生异常", e1);
			}
			break;
		}
		return instance;
	}
	
	/**
	 * 通过JavaBean属性注入实例
	 * @param instance
	 */
	private void injectProperty(Object instance) {
		Class<?> clz = instance.getClass();
		try {
			for (PropertyDescriptor p : Introspector.getBeanInfo(clz, Object.class).getPropertyDescriptors()) {
				Method m = p.getWriteMethod();
				if (m == null) {
					continue;
				}
				Inject inject = m.getAnnotation(Inject.class);
				if (inject == null) {
					continue;
				}
				m.setAccessible(true);
				String name = "";
				Named named = m.getAnnotation(Named.class);
				if (named != null) {
					name = named.value();
				}
				Object injectObj = getInstance(name, p.getPropertyType());
				if (injectObj == null) {
					throw new RuntimeException("未找到Bean实例");
				}
				m.invoke(instance, injectObj);
			}
		} catch (IntrospectionException | IllegalAccessException | InvocationTargetException e1) {
			e1.printStackTrace();
			logger.log(Level.SEVERE, "访问JavaBean属性发生异常", e1);
		}
	}
	
	/**
	 * 通过Field字段注入实例
	 * @param clz
	 * @param instance
	 */
	private void injectField(Object instance) {
		for (Entry<String, Field> entry : BeanUtil.getFieldMap(instance).entrySet()) {
			Field f = entry.getValue();
			Inject inject = f.getAnnotation(Inject.class);
			if (inject == null) {
				continue;
			}
			f.setAccessible(true);
			String name = "";
			Named named = f.getAnnotation(Named.class);
			if (named != null) {
				name = named.value();
			}
			Object injectObj = getInstance(name, f.getType());
			if (injectObj == null) {
				throw new RuntimeException("没有找到Bean实例");
			}
			try {
				f.set(instance, injectObj);
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
				logger.log(Level.SEVERE, "访问Field字段发生异常", e1);
			}
		}
	}
	
}
