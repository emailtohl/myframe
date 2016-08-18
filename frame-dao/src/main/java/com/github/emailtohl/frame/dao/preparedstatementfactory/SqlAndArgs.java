package com.github.emailtohl.frame.dao.preparedstatementfactory;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 存储po中的映射关系 存储po对应的preparedstatement语句和参数数组
 */
public class SqlAndArgs {
	private String tableName;// 此po对应的数据库表名
	private Class<?> entityClass;// 标记继承层次上从哪个class开始与数据表对应
	private String keyColumnLabel;// 主键的columnLabel
	private String markDeletedColumnLabel;// 标记删除的columnLabel
	private final Map<String, PropertyBean> columnPropertyMap = new LinkedHashMap<String, PropertyBean>();
	private String preparedSQL;
	private List<PropertyBean> argsList;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public Class<?> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}
	
	public String getKeyColumnLabel() {
		return keyColumnLabel;
	}

	public void setKeyColumnLabel(String keyColumnLabel) {
		this.keyColumnLabel = keyColumnLabel;
	}

	public String getMarkDeletedColumnLabel() {
		return markDeletedColumnLabel;
	}

	public void setMarkDeletedColumnLabel(String markDeletedColumnLabel) {
		this.markDeletedColumnLabel = markDeletedColumnLabel;
	}

	public Map<String, PropertyBean> getColumnPropertyMap() {
		return columnPropertyMap;
	}

	public String getPreparedSQL() {
		return preparedSQL;
	}

	public void setPreparedSQL(String preparedSQL) {
		this.preparedSQL = preparedSQL;
	}

	public List<PropertyBean> getArgsList() {
		return argsList;
	}

	public void setArgsList(List<PropertyBean> argsList) {
		this.argsList = argsList;
	}

	public Object[] getParamValues() {
		if (argsList == null)
			return new Object[0];
		int length = argsList.size();
		Object[] paramValues = new Object[length];
		Iterator<PropertyBean> iterator = argsList.iterator();
		for (int i = 0; i < length; i++) {
			paramValues[i] = iterator.next().getPropertyValue();
		}
		return paramValues;
	}

	public Class<?>[] getPropertyClazz() {
		if (argsList == null)
			return new Class<?>[0];
		int length = argsList.size();
		Class<?>[] paramClasses = new Class<?>[length];
		Iterator<PropertyBean> iterator = argsList.iterator();
		for (int i = 0; i < length; i++) {
			paramClasses[i] = iterator.next().getPropertyClazz();
		}
		return paramClasses;
	}
}
