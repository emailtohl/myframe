package com.github.emailtohl.frame.dao.preparedstatementfactory;

/**
 *************************************************
 * 此bean用于描述po对象某字段的属性，例如Person PO对象：
 * public class Person {
 * 		@Orm(columnLabel="name")
 * 		String name = "tom";
 * 		@Orm(columnLabel="age")
 * 		Integer age = 18;
 * }
 * 对于age字段:
 * propertyName=age
 * propertyValue=18
 * clazz=String.class
 * 
 * @author helei
 *************************************************
 */
public class PropertyBean {
	private String propertyName;
	private Object propertyValue;
	private Class<?> propertyClazz;

	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public Object getPropertyValue() {
		return propertyValue;
	}
	public void setPropertyValue(Object propertyValue) {
		this.propertyValue = propertyValue;
	}
	public Class<?> getPropertyClazz() {
		return propertyClazz;
	}
	public void setPropertyClazz(Class<?> propertyClazz) {
		this.propertyClazz = propertyClazz;
	}

	@Override
	public String toString() {
		return "PropertyBean [propertyName=" + propertyName + ", propertyValue=" + propertyValue + ", propertyClazz="
				+ propertyClazz + "]";
	}

}
