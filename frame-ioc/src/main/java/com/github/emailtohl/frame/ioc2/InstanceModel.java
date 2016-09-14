package com.github.emailtohl.frame.ioc2;

import java.util.HashSet;
import java.util.Set;
/**
 * 为实例对象进行建模，为ioc容器提供必要的信息
 * @author helei
 */
public class InstanceModel implements Comparable<InstanceModel> {
	/**
	 * 实例的类型
	 */
	private Class<?> type;
	/**
	 * 实例名（id）
	 */
	private String name;
	/**
	 * 实例
	 */
	private Object instance;
	/**
	 * 依赖列表
	 */
	private Set<Class<?>> dependencies = new HashSet<Class<?>>();
	
	/**
	 * 为了在TreeMap中排序
	 */
	@Override
	public int compareTo(InstanceModel other) {
		if (this.dependencies.isEmpty()) {
			return -1;
		}
		if (other.dependencies.isEmpty()) {
			return 1;
		}
		if (this.dependencies.contains(other.type)) {
			return 1;
		}
		if (other.dependencies.contains(this.type)) {
			return -1;
		}
		if (this.equals(other)) {
			return 0;
		} else {
			return 1;// 互不依赖不能返回0，否则会被TreeMap认为它们是相等的
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	/**
	 * 以name（id）作为InstanceModel的主键
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InstanceModel other = (InstanceModel) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getInstance() {
		return instance;
	}

	public void setInstance(Object instance) {
		this.instance = instance;
	}

	public Set<Class<?>> getDependencies() {
		return dependencies;
	}

	public void setDependencies(Set<Class<?>> dependencies) {
		this.dependencies = dependencies;
	}

	@Override
	public String toString() {
		return "InstanceModel [name=" + name + ", type=" + type + ", instance=" + instance + ", dependencies="
				+ dependencies + "]";
	}

}
