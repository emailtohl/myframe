package com.github.emailtohl.frame.ioc;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.emailtohl.frame.util.BeanTools;
import com.github.emailtohl.frame.util.PackageScanner;
/**
 * ioc的容器，仿造如Spring一样，可以通过name（id）获取实例，也可以通过类型获取实例
 * 实现依赖注入功能
 * 
 * @author Helei
 *
 */
public class Context {
	private static final Logger logger = LogManager.getLogManager().getLogger(BeanTools.class.getName());
	/**
	 * 通过name（id）查找实例模型
	 */
	private Map<String, InstanceModel> nameModelMap = new HashMap<String, InstanceModel>();
	/**
	 * 通过class查找实例模型
	 */
	private Map<Class<?>, InstanceModel> typeModelMap = new HashMap<Class<?>, InstanceModel>();
	/**
	 * 通过那么（id）查找具体实例
	 */
	private Map<String, Object> nameInstanceMap = new HashMap<String, Object>();
	/**
	 * 通过class查找具体实例
	 */
	private Map<Class<?>, Object> typeInstanceMap = new HashMap<Class<?>, Object>();
	
	public Context(String packagePath) {
		super();
		Set<Class<?>> classSet = PackageScanner.getClasses(packagePath);
		// 第一步，过滤无关的Class
		filter(classSet);
		// 第二步，对依赖关系进行建模
		TreeSet<InstanceModel> instanceModelSet = getDependencies(classSet);
		// 第三步，实例化，依赖注入
		newInstanceAndInjectDependencies(instanceModelSet);
	}
	
	/**
	 * 将实例纳入容器统一管理，所有依赖将被自动注入
	 * @param name 实例名
	 * @param instance 实例对象
	 */
	public void register(String name, Object instance) {
		if (nameInstanceMap.containsKey(name)) {
			throw new RuntimeException("已有相同实例名");
		}
		Class<?> clz = instance.getClass();
		InstanceModel model = new InstanceModel();
		model.setInstance(instance);
		model.setName(name);
		model.setType(clz);
		// 下面创建依赖列表
		Set<Class<?>> dependencyClassSet = new HashSet<Class<?>>();
//		既然已经实例化了，所以不依赖构造器的参数
//		dependencyClassSet.addAll(getDependenciesByConstructor(clz));
		dependencyClassSet.addAll(getDependenciesByProperties(clz));
		dependencyClassSet.addAll(getDependenciesByField(clz));
		TreeSet<InstanceModel> ts = getDependencies(dependencyClassSet);
		model.setDependencies(ts);
		// 为该实例执行依赖注入
		injectProperty(instance);
		injectField(instance);
		// 最后添加进创建好的表中
		nameModelMap.put(name, model);
		typeModelMap.put(clz, model);
		nameInstanceMap.put(name, instance);
		typeInstanceMap.put(clz, instance);
	}
	
	/**
	 * 通过类型查询实例对象
	 * 
	 * @param clz 类型，包括interface接口
	 * @return 实例对象，如果查找有多个实例对象，则抛运行时异常
	 */
	@SuppressWarnings("unchecked")
	public <T> T getInstance(Class<T> clz) {
		Map<String, Object> instanceMap = getInstanceMapByType(clz);
		int size = instanceMap.size();
		if (size == 0) {
			return null;
		} else if (size > 1) {
			throw new RuntimeException("有多个实例满足该类型：" + instanceMap);
		}
		T instance = null;
		for (Entry<String, Object> e : instanceMap.entrySet()) {
			instance = (T) e.getValue();
		}
		return instance;
	}
	
	/**
	 * 通过类型查询实例模型
	 * 
	 * @param clz 类型，包括interface接口
	 * @return 实例对象，如果查找有多个实例对象，则抛运行时异常
	 */
	@SuppressWarnings("unused")
	private InstanceModel getInstanceModel(Class<?> clz) {
		Map<String, InstanceModel> instanceModelMap = getInstanceModelMapByType(clz);
		int size = instanceModelMap.size();
		if (size == 0) {
			return null;
		} else if (size > 1) {
			throw new RuntimeException("有多个实例模型满足该类型：" + instanceModelMap);
		}
		InstanceModel instanceModel = null;
		for (Entry<String, InstanceModel> e : instanceModelMap.entrySet()) {
			instanceModel = e.getValue();
		}
		return instanceModel;
	}
	
	/**
	 * 通过类型和实例名查询实例
	 * @param clz 类型名
	 * @param name 实例名
	 * @return 实例，若未查到，则返回null
	 */
	@SuppressWarnings("unchecked")
	public <T> T getInstance(Class<T> clz, String name) {
		Map<String, Object> instanceMap = getInstanceMapByType(clz);
		return (T) instanceMap.get(name);
	}
	
	/**
	 * 通过实例名查询实例
	 * @param name 实例名
	 * @return 实例，若未查到，则返回null
	 */
	public Object getInstance(String name) {
		return nameModelMap.get(name);
	}
	
	/**
	 * 一个类型下有可能有多个实例，所以返回一个Map，key是实例名，value是实例
	 * @param clz
	 * @return
	 */
	private Map<String, Object> getInstanceMapByType(Class<?> clz) {
		Map<String, Object> instanceMap = new HashMap<String, Object>();
		for (Entry<Class<?>, InstanceModel> e : typeModelMap.entrySet()) {
			if (clz.isAssignableFrom(e.getKey())) {
				instanceMap.put(e.getValue().getName(), e.getValue().getInstance());
			}
		}
		return instanceMap;
	}
	
	/**
	 * 一个类型下有可能有多个实例，所以返回一个Map，key是实例名，value是实例模型
	 * @param clz
	 * @return
	 */
	private Map<String, InstanceModel> getInstanceModelMapByType(Class<?> clz) {
		Map<String, InstanceModel> instanceModelMap = new HashMap<String, InstanceModel>();
		for (Entry<Class<?>, InstanceModel> e : typeModelMap.entrySet()) {
			if (clz.isAssignableFrom(e.getKey())) {
				instanceModelMap.put(e.getValue().getName(), e.getValue());
			}
		}
		return instanceModelMap;
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
	 * 对依赖关系进行建模
	 * 同时将实例模型维护到相应查询Map中
	 * @param classSet 扫描出来，带有Component注解的Class集合
	 */
	private TreeSet<InstanceModel> getDependencies(Set<Class<?>> classSet) {
		TreeSet<InstanceModel> instanceModels = new TreeSet<InstanceModel>();
		for (Class<?> clz : classSet) {
			// 如果是接口，则忽略
			if (!clz.isInterface()) {
				continue;
			}
			String name = getNameByClass(clz);
			InstanceModel model = nameModelMap.get(name);
			// 如果已经被创建过，则继续分析下一个class
			if (model == null) {
				model = new InstanceModel();
				model.setName(name);
				model.setType(clz);
				// 下面创建依赖列表
				Set<Class<?>> dependencyClassSet = new HashSet<Class<?>>();
				dependencyClassSet.addAll(getDependenciesByConstructor(clz));
				dependencyClassSet.addAll(getDependenciesByProperties(clz));
				dependencyClassSet.addAll(getDependenciesByField(clz));
				TreeSet<InstanceModel> ts = getDependencies(dependencyClassSet);
				model.setDependencies(ts);
				// 最后添加进创建好的表中
				nameModelMap.put(name, model);
				typeModelMap.put(clz, model);
			}
			// 添加进TreeMap时，顺序会按照InstanceModel的compareTo规则排序
			instanceModels.add(model);
		}
		return instanceModels;
	}
	
	/**
	 * 从构造器中获取依赖的Class
	 */
	private Set<Class<?>> getDependenciesByConstructor(Class<?> clz) {
		Set<Class<?>> set = new HashSet<Class<?>>();
		for (Constructor<?> constructor : clz.getConstructors()) {
			Component c = constructor.getAnnotation(Component.class);
			if (c == null) {
				continue;
			}
			for (Class<?> cl : constructor.getParameterTypes()) {
				set.add(cl);
			}
		}
		return set;
	}
	
	/**
	 * 从JavaBean属性中获取依赖的Class
	 */
	private Set<Class<?>> getDependenciesByProperties(Class<?> clz) {
		Set<Class<?>> set = new HashSet<Class<?>>();
		if (!clz.isInterface()) {
			try {
				for (PropertyDescriptor p : Introspector.getBeanInfo(clz, Object.class).getPropertyDescriptors()) {
					Inject inject = p.getWriteMethod().getAnnotation(Inject.class);
					if (inject != null) {
						set.add(p.getPropertyType());
					}
				}
			} catch (IntrospectionException e) {
				e.printStackTrace();
				logger.log(Level.SEVERE, "分析Bean的Setter方法发生异常，依赖注入可能失败", e);
			}
		}
		return set;
	}
	
	/**
	 * 从Field字段中获取依赖的Class
	 */
	private Set<Class<?>> getDependenciesByField(Class<?> clz) {
		Set<Class<?>> set = new HashSet<Class<?>>();
		if (!clz.isInterface()) {
			Class<?> clazz = clz;
			while (clazz != Object.class) {
				Field[] fields = clazz.getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					Inject inject = fields[i].getAnnotation(Inject.class);
					if (inject != null) {
						set.add(fields[i].getType());
					}
				}
				clazz = clz.getSuperclass();
			}
		}
		return set;
	}
	
	/**
	 * 按照依赖顺序，进行实例化，同时设置nameInstanceMap和typeInstanceMap两种数据结构
	 * @param instanceModelSet
	 */
	private void newInstanceAndInjectDependencies(TreeSet<InstanceModel> instanceModelSet) {
		for (InstanceModel model : instanceModelSet) {
			Class<?> clz = model.getType();
			Object instance = null;
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
			// 创建好实例后，先调用JavaBean的setter方法注入实例
			injectProperty(instance);
			// 最后在Field字段中注入
			injectField(instance);
			nameInstanceMap.put(getNameByClass(clz), instance);
			typeInstanceMap.put(clz, instance);
		}
	}
	
	/**
	 * 通过构造器来创建实例
	 * @param clz
	 * @return
	 */
	private Object newInstanceByConstructor(Class<?> clz) {
		Object instance = null;
		for (Constructor<?> constructor : clz.getConstructors()) {
			Inject inject = constructor.getAnnotation(Inject.class);
			if (inject == null) {
				continue;
			}
			Object[] initargs = new Object[constructor.getParameterTypes().length];
			int i = 0;
			for (Class<?> pt : constructor.getParameterTypes()) {
				Object injectObj = null;
				String name = getNameByClass(pt);
				if (name.isEmpty()) {// 如果注明了name（id），则通过name获取
					injectObj = nameInstanceMap.get(name);
				} else {
					injectObj = typeInstanceMap.get(pt);
				}
				if (injectObj == null) {
					injectObj = getInstance(pt);
				}
				if (injectObj == null) {
					throw new RuntimeException("未找到Bean实例");
				}
				initargs[i++] = injectObj;
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
				Object injectObj = null;
				if (name.isEmpty()) {// 如果注明了name（id），则通过name获取
					injectObj = nameInstanceMap.get(name);
				} else {
					injectObj = typeInstanceMap.get(p.getPropertyType());
				}
				if (injectObj == null) {
					injectObj = getInstance(p.getPropertyType());
				}
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
		for (Entry<String, Field> entry : BeanTools.getFieldMap(instance).entrySet()) {
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
			Object param = null;
			if (name.isEmpty()) {// 如果注明了name（id），则通过name获取
				param = nameInstanceMap.get(name);
			} else {
				param = typeInstanceMap.get(f.getType());
			}
			if (param == null) {
				throw new RuntimeException("没有找到Bean实例");
			}
			try {
				f.set(instance, param);
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
				logger.log(Level.SEVERE, "访问Field字段发生异常", e1);
			}
		}
	}
}
