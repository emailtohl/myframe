package com.github.emailtohl.frame.context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/**
 * 对容器进行建模，包含依赖注入的必要信息
 * @author HeLei
 */
public class Bean implements Serializable, Comparable<Object> {
	private static final long serialVersionUID = 6030804224172961375L;
	/**
	 * 对象实例map，一个类型下有多个实例，他们以实例名为key
	 */
	private Map<String, Object> instances = new HashMap<String, Object>();
	/**
	 * 对象的类型
	 */
	private Class<?> type;
	/**
	 * 依赖列表
	 */
	private Set<Bean> dependencies = new HashSet<Bean>();

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	
	public Map<String, Object> getInstances() {
		return instances;
	}

	public void setInstances(Map<String, Object> instances) {
		this.instances = instances;
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

}
