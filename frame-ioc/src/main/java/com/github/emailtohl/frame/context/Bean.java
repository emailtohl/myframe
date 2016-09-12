package com.github.emailtohl.frame.context;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
/**
 * 对容器进行建模，包含依赖注入的必要信息
 * @author HeLei
 */
public class Bean implements Serializable, Comparable<Object> {
	private static final long serialVersionUID = 6030804224172961375L;
	/**
	 * 对象实例
	 */
	private Object instance;
	/**
	 * 对象的类型
	 */
	private Class<?> type;
	/**
	 * 依赖列表
	 */
	private Set<Bean> dependencies = new HashSet<Bean>();
	/**
	 * Bean的名字，默认为其类名，小写首字母
	 */
	private String name;

	@Override
	public int compareTo(Object o) {
		return 0;
	}
	
	public Object getInstance() {
		return instance;
	}
	public void setInstance(Object instance) {
		this.instance = instance;
	}
	public Class<?> getType() {
		return type;
	}
	public void setType(Class<?> type) {
		this.type = type;
	}
	public Set<Bean> getDependencies() {
		return dependencies;
	}
	public void setDependencies(Set<Bean> dependencies) {
		this.dependencies = dependencies;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
