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
	public int compareTo(InstanceModel o) {
		if (isDependInterface()) {
			return 1;
		}
		int result = 0;
		if (o instanceof InstanceModel) {
			InstanceModel model = (InstanceModel) o;
			if (model.isDependInterface()) {
				result = -1;
			} else {
				if (dependencies.contains(model.type)) {
					result = 1;
				} else if (model.dependencies.contains(type)) {
					result = -1;
				}
			}
		}
		return result;
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

	/**
	 * 判断是否依赖接口，若是，则需要等具体类实例化完之后，再将实例注入到本类中，所以本类的实例化应该在最后
	 * @return
	 */
	public boolean isDependInterface() {
		for (Class<?> c : dependencies) {
			if (c.isInterface()) {
				return true;
			}
		}
		return false;
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
		return "InstanceModel [type=" + type.getSimpleName() + ", name=" + name + ", instance=" + instance + ", dependencies="
				+ dependencies + "]";
	}

}
