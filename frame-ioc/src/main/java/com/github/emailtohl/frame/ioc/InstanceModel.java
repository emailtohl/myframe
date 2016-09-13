package com.github.emailtohl.frame.ioc;

import java.util.Set;
import java.util.TreeSet;
/**
 * 为实例对象进行建模，为ioc容器提供必要的信息
 * 只存储可实例化的类，忽略接口、抽象类
 * 
 * @author Helei
 *
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
	private Set<InstanceModel> dependencies = new TreeSet<InstanceModel>();
	/**
	 * 该类是否依赖于接口，若是，则应该最后才实例化
	 */
	private boolean isDependInterface = false;
	
	/**
	 * 为了在TreeMap中排序
	 */
	@Override
	public int compareTo(InstanceModel o) {
		int result = 0;
		if (isDependInterface) {// 如果被注入的是接口，则等其他实例化完了之后再创建
			result = 1;
		} else {
			if (o instanceof InstanceModel) {
				InstanceModel model = (InstanceModel) o;
				if (model.isDependInterface()) {
					result = -1;
				} else {
					if (dependencies.contains(model)) {
						result = 1;
					} else if (model.dependencies.contains(this)) {
						result = -1;
					}
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

	public Set<InstanceModel> getDependencies() {
		return dependencies;
	}

	public void setDependencies(Set<InstanceModel> dependencies) {
		this.dependencies = dependencies;
	}

	public boolean isDependInterface() {
		return isDependInterface;
	}

	public void setDependInterface(boolean isDependInterface) {
		this.isDependInterface = isDependInterface;
	}

}
