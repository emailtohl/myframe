package com.github.emailtohl.frame.mvc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.github.emailtohl.frame.util.PackageScanner;

/**
 *************************************************
 * 分析客户的控制器，建立注解到对应方法的映射关系，以提供给DispatcherServlet调用
 * 
 * @author helei
 * @version 2.0 更新为包扫描
 * 2015.10.13
 * 
 * @version 2.1 更新http方法
 * 2016.7.2 
 *************************************************
 */
public class MvcParser {
	public Map<String, ClassAndMethodBean> getActionHandleMap(String pack) throws InstantiationException, IllegalAccessException {
		Set<Class<?>> classSet = PackageScanner.getClasses(pack);
		Mvc mvc;
		Map<String, ClassAndMethodBean> map = new HashMap<String, ClassAndMethodBean>();
		for (Class<?> clz : classSet) {
			mvc = clz.getAnnotation(Mvc.class);
			if (mvc == null)
				continue;
			Object controller = clz.newInstance();
			String preAction = mvc.action();
			if (preAction.length() > 0 && !preAction.endsWith("/")) {
				preAction += "/";
			}
			for (Method method : clz.getDeclaredMethods()) {
				mvc = method.getAnnotation(Mvc.class);
				if (mvc == null)
					continue;
				ClassAndMethodBean cam = new ClassAndMethodBean();
				String action = mvc.action();
				if (action.startsWith("/")) {
					action = action.substring(1);
				}
				action = preAction + action;
				if (map.containsKey(action))
					throw new RuntimeException("Mvc注解的映射有重复");
				cam.setClazz(clz);
				cam.setController(controller);
				cam.setMethod(method);
				cam.setHttpMethod(mvc.method());
				map.put(action, cam);
			}
		}
		return map;
	}
}
