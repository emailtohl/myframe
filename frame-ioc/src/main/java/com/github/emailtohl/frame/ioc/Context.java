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

public class Context {
	private static final Logger logger = LogManager.getLogManager().getLogger(BeanTools.class.getName());
	private Map<String, InstanceModel> modelMap = new HashMap<String, InstanceModel>();
	private Map<String, Object> nameMap = new HashMap<String, Object>();
	private Map<Class<?>, Object> typeMap = new HashMap<Class<?>, Object>();
	
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
	 * 过滤无关的Class
	 */
	private void filter(Set<Class<?>> classSet) {
		Iterator<Class<?>> i = classSet.iterator();
		while (i.hasNext()) {
			Component c = i.next().getAnnotation(Component.class);
			if (c == null) {
				i.remove();
			}
		}
	}
	
	/**
	 * 通过注解的类名获取实例的name（id）
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
	 * @param classSet
	 */
	private TreeSet<InstanceModel> getDependencies(Set<Class<?>> classSet) {
		TreeSet<InstanceModel> instanceModels = new TreeSet<InstanceModel>();
		for (Class<?> clz : classSet) {
			String name = getNameByClass(clz);
			InstanceModel model = modelMap.get(name);
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
				modelMap.put(name, model);
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
		try {
			for (PropertyDescriptor p : Introspector.getBeanInfo(clz, Object.class).getPropertyDescriptors()) {
				set.add(p.getPropertyType());
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "分析Bean的Setter方法发生异常，依赖注入可能失败", e);
		}
		return set;
	}
	
	/**
	 * 从Field字段中获取依赖的Class
	 */
	private Set<Class<?>> getDependenciesByField(Class<?> clz) {
		Set<Class<?>> set = new HashSet<Class<?>>();
		for (Entry<String, Field> e : BeanTools.getFieldMap(clz).entrySet()) {
			set.add(e.getValue().getType());
		}
		return set;
	}
	
	/**
	 * 当通过类型无法获取到实例时，查看是否有其子类存在于容器中
	 */
	private Object getInstanceFromSubType(Class<?> clz) {
		for (Entry<Class<?>, Object> e : typeMap.entrySet()) {
			if (clz.isAssignableFrom(e.getKey())) {
				return e.getValue();
			}
		}
		return null;
	}
	
	/**
	 * 按照依赖顺序，进行实例化，同时设置typeMap和nameMap两种数据结构
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
					throw new IllegalArgumentException();
				}
			}
			// 创建好实例后，先调用JavaBean的setter方法注入实例
			injectProperty(clz, instance);
			// 最后在Field字段中注入
			injectField(instance);
			nameMap.put(getNameByClass(clz), instance);
			typeMap.put(clz, instance);
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
					injectObj = nameMap.get(name);
				} else {
					injectObj = typeMap.get(pt);
				}
				if (injectObj == null) {
					injectObj = getInstanceFromSubType(pt);
				}
				if (injectObj == null) {
					throw new RuntimeException("未找到Bean实例");
				}
				initargs[i++] = injectObj;
			}
			try {
				instance = constructor.newInstance(initargs);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e1) {
				e1.printStackTrace();
				logger.log(Level.SEVERE, "构造器创建实例时发生异常", e1);
			}
			break;
		}
		return instance;
	}
	
	/**
	 * 通过JavaBean属性注入实例
	 * @param clz
	 * @param instance
	 */
	private void injectProperty(Class<?> clz, Object instance) {
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
					injectObj = nameMap.get(name);
				} else {
					injectObj = typeMap.get(p.getPropertyType());
				}
				if (injectObj == null) {
					injectObj = getInstanceFromSubType(p.getPropertyType());
				}
				if (injectObj == null) {
					throw new RuntimeException("未找到Bean实例");
				}
				m.invoke(instance, injectObj);
			}
		} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
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
				param = nameMap.get(name);
			} else {
				param = typeMap.get(f.getType());
			}
			if (param == null) {
				throw new RuntimeException("没有找到Bean实例");
			}
			try {
				f.set(instance, param);
			} catch (IllegalArgumentException | IllegalAccessException e1) {
				e1.printStackTrace();
				logger.log(Level.SEVERE, "访问Field字段发生异常", e1);
			}
		}
	}
}
