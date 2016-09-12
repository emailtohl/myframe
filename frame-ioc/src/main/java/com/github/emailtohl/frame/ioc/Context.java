package com.github.emailtohl.frame.ioc;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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
			return null;
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
}
