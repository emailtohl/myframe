package com.github.emailtohl.frame.context;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.inject.Inject;

import com.github.emailtohl.frame.util.BeanTools;
import com.github.emailtohl.frame.util.PackageScanner;
/**
 * 作为容器，存储依赖关系的Bean对象，它继承TreeMap，key是Bean对象的类型，value是Bean对象的实例
 * @author HeLei
 */
public class Container implements Serializable {
	private static final long serialVersionUID = -5858462858295708970L;
	private static final Logger logger = LogManager.getLogManager().getLogger(BeanTools.class.getName());
	private TreeMap<Class<?>, Bean> context = new TreeMap<Class<?>, Bean>();
	private Set<Bean> beanSet = new HashSet<Bean>();
	
	public Container(String packagePath) {
		super();
		Set<Class<?>> classSet = PackageScanner.getClasses(packagePath);
		// 第一步，过滤无关的Class
		filter(classSet);
		// 第二步，对依赖关系进行建模
		for (Class<?> clz : classSet) {
			Bean b = new Bean();
			setDependencies(b, clz);
		}
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
	 * 对依赖关系进行建模
	 * @param classSet
	 */
	private void setDependencies(Bean b, Class<?> clz) {
		// 首先，在构造器中查找依赖
		Set<Bean> sb = new HashSet<Bean>();
		for (Constructor<?> constructor : clz.getConstructors()) {
			Component c = constructor.getAnnotation(Component.class);
			if (c == null) {
				continue;
			}
			for (Class<?> cl : constructor.getParameterTypes()) {
				Bean be = context.get(cl);
				if (be == null) {
					be = new Bean();
				}
				sb.add(be);
			}
			b.getDependencies().addAll(sb);
		}
		
		// 然后，在符合JavaBean规范的setter方法中查找依赖
		sb.clear();
		try {
			for (PropertyDescriptor p : Introspector.getBeanInfo(clz, Object.class).getPropertyDescriptors()) {
				Bean be = context.get(p.getPropertyType());
				if (be == null) {
					be = new Bean();
				}
				sb.add(be);
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "分析Bean的Setter方法发生异常，依赖注入可能失败", e);
		}
		b.getDependencies().addAll(sb);
		
		// 最后，在Field字段上查找依赖
		sb.clear();
		for (Entry<String, Field> e : BeanTools.getFieldMap(clz).entrySet()) {
			Bean be = context.get(e.getValue());
			if (be == null) {
				be = new Bean();
			}
			sb.add(be);
		}
		b.getDependencies().addAll(sb);
	}
	
	/**
	 * 获取Bean的名字
	 * @param clz
	 * @return
	 */
	private String getBeanName(Class<?> clz) {
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
	 * 第二步，分析类的依赖关系，进行建模
	 */
	private Bean analyze(Class<?> clz) {
		Bean b = new Bean();
		Component c = clz.getAnnotation(Component.class);
		if (c == null) {
			return null;
		}
		String name = c.name();
		if (name.isEmpty()) {
			name = clz.getSimpleName();
			name = name.substring(0, 1).toLowerCase() + name.substring(1);
		}
		b.setType(clz);
		Object instance;
		try {
			instance = clz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			logger.log(Level.SEVERE, clz.getName() + "不能实例化，必须提供一个无参构造器", e);
			e.printStackTrace();
			throw new IllegalArgumentException(clz.getName() + "不能实例化，必须提供一个无参构造器", e);
		}
		// 处理依赖关系
		for (Entry<String, Field> e : BeanTools.getFieldMap(instance).entrySet()) {
			Field f = e.getValue();
			f.setAccessible(true);
			Inject inj = f.getAnnotation(Inject.class);
			if (inj == null) {
				continue;
			}
			Bean subBean = analyze(e.getValue().getType());
			if (subBean != null) {
				b.getDependencies().add(new Bean());
			}
		}
		return b;
	}

}
