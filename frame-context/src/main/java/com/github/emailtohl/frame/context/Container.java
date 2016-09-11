package com.github.emailtohl.frame.context;

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
public class Container extends TreeMap<Class<?>, Object> {
	private static final long serialVersionUID = -5858462858295708970L;
	private static final Logger logger = LogManager.getLogManager().getLogger(BeanTools.class.getName());
	
	private Set<Bean> beanSet = new HashSet<Bean>();
	
	public Container(String packagePath) {
		super();
		Set<Class<?>> classSet = PackageScanner.getClasses(packagePath);
		filter(classSet);
		for (Class<?> clz : classSet) {
			beanSet.add(analyze(clz));
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
	 * 分析类的信息，然后填充进Bean对象中，进行建模工作
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
		b.setName(name);
		b.setType(clz);
		Object instance;
		try {
			instance = clz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			logger.log(Level.SEVERE, clz.getName() + "不能实例化，必须提供一个无参构造器", e);
			e.printStackTrace();
			throw new IllegalArgumentException(clz.getName() + "不能实例化，必须提供一个无参构造器", e);
		}
		b.setInstance(instance);
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
