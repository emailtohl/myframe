package com.github.emailtohl.frame.dao.preparedstatementfactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 本类实现了OrmAnalyzer，可将JPA注解的信息解析到SqlAndArgs对象中
 * 
 * @author helei 2016.02.18
 */
public class JpaAnalyzer implements OrmAnalyzer {
	private static final Logger logger = Logger.getLogger(JpaAnalyzer.class.getName());

	@Override
	public SqlAndArgs parse(Object po) {
		if (po == null) {
			logger.log(Level.WARNING, "传入po是null");
			throw new NullPointerException("传入po是null");
		}
		SqlAndArgs sa = new SqlAndArgs();
		setTableName(po, sa);
		Class<?> entityClass = sa.getEntityClass();
		Access accessAnno = entityClass.getAnnotation(Access.class);
		if (accessAnno != null && accessAnno.value() == AccessType.FIELD) {
			setColumnByField(po, entityClass, sa);
		} else {
			setColumnByProperty(po, entityClass, sa);
		}
		return sa;
	}

	/**
	 * 一般po会被其他JavaBean继承，本方法寻找继承结构上哪个class被注解了表名，并且从该class开始分析属性的注解
	 * 注意：以@Table中的name注解为准
	 * 
	 * @param clazz
	 * @return
	 */
	private void setTableName(Object po, SqlAndArgs sa) {
		String tableName = null;
		Class<?> entityClass = null;
		Class<?> clazz = po.getClass();
		while (clazz != Object.class) {// 分析po各个层次上的注解信息，直到到达Object顶级
			Table tableAnno = clazz.getAnnotation(Table.class);
			Entity entityAnno = clazz.getAnnotation(Entity.class);
			if (entityAnno != null) {
				tableName = entityAnno.name().trim();
				if (tableName.length() == 0) {
					tableName = clazz.getSimpleName();
				}
				if (entityClass == null) {
					entityClass = clazz;
				}
			}
			if (tableAnno != null) {
				tableName = tableAnno.name().trim();
				if (tableName.length() == 0) {
					tableName = clazz.getSimpleName();
				}
				if (entityClass == null) {
					entityClass = clazz;
				}
			}
			if (tableName != null) {
				break;
			}
			clazz = clazz.getSuperclass();
		}
		if (tableName == null) {
			logger.log(Level.SEVERE, "未注解表名");
			throw new IllegalArgumentException("未注解表名");
		} else {
			sa.setTableName(tableName);
			sa.setEntityClass(clazz);
		}
	}

	/**
	 * 获取setter、getter方法上注解的column信息
	 * @param po
	 * @param clazz
	 * @param sa
	 */
	private void setColumnByProperty(Object po, Class<?> clazz, SqlAndArgs sa) {
		String keyColumnLabel = null;// 主键的columnLabel
		String markDeletedColumnLabel = null;// 标记删除的columnLabel
		Map<String, PropertyBean> columnPropertyMap = sa.getColumnPropertyMap();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz, Object.class);
			for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
				Id idAnno = null;
				Column columnAnno = null;
				Orm ormAnno = null;
				Method writeMethod = pd.getWriteMethod();
				Method readMethod = pd.getReadMethod();
				if (readMethod != null) {
					idAnno = readMethod.getAnnotation(Id.class);
					columnAnno = readMethod.getAnnotation(Column.class);
					ormAnno = readMethod.getAnnotation(Orm.class);
				} else if (writeMethod != null) {
					idAnno = writeMethod.getAnnotation(Id.class);
					columnAnno = writeMethod.getAnnotation(Column.class);
					ormAnno = writeMethod.getAnnotation(Orm.class);
				}
				// 分析column和property对应关系
				if (columnAnno == null) {
					continue;
				}
				String columnLabel = columnAnno.name().trim();
				if (columnLabel.length() == 0) {
					columnLabel = pd.getName();
				}
				// 若导出类覆盖了基类的注解，则以导出类为准
				if (columnPropertyMap.containsKey(columnLabel)) {
					continue;
				}
				boolean isKey = false;
				if (idAnno != null) {
					isKey = true;
				}
				boolean isMarkDeleted = false;
				if (ormAnno != null) {
					isMarkDeleted = ormAnno.isMarkDeleted();
				}
				try {
					String propertyName = pd.getName();
					Object propertyValue = pd.getReadMethod().invoke(po, new Object[] {});
					Class<?> propertyClazz = pd.getPropertyType();
					PropertyBean prop = new PropertyBean();
					prop.setPropertyName(propertyName);
					prop.setPropertyValue(propertyValue);
					prop.setPropertyClazz(propertyClazz);
					columnPropertyMap.put(columnLabel, prop);
				} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
					logger.log(Level.SEVERE, "分析po属性时出现异常", e);
				}
				// 判断此属性是否主键或标记删除
				if (isKey && keyColumnLabel == null) {
					keyColumnLabel = columnLabel;
					sa.setKeyColumnLabel(keyColumnLabel);
					// 此处写成else if实际上可以防止既注解了isKey，又注解了isMarkDeleted的情况
				} else if (isMarkDeleted && markDeletedColumnLabel == null) {
					markDeletedColumnLabel = columnLabel;
					sa.setMarkDeletedColumnLabel(markDeletedColumnLabel);
				}
			}
		} catch (IntrospectionException e) {
			logger.log(Level.SEVERE, "分析po属性时出现异常", e);
			throw new IllegalArgumentException("传入po错误");
		}
	}

	/**
	 * 获取实例域上注解的column信息 实例域必须标注@Column注解后才有效，@Id也必须在有@Column标注的实例域才有效
	 * 
	 * @param po
	 * @param clazz
	 * @param sa
	 */
	private void setColumnByField(Object po, Class<?> clazz, SqlAndArgs sa) {
		String keyColumnLabel = null;// 主键的columnLabel
		String markDeletedColumnLabel = null;// 标记删除的columnLabel
		Map<String, PropertyBean> columnPropertyMap = sa.getColumnPropertyMap();
		while (clazz != null && clazz != Object.class) {
			Field[] fields = clazz.getDeclaredFields();
			AccessibleObject.setAccessible(fields, true);
			for (Field field : fields) {
				Id idAnno = field.getAnnotation(Id.class);
				Column columnAnno = field.getAnnotation(Column.class);
				Orm ormAnno = field.getAnnotation(Orm.class);
				if (columnAnno == null) {
					continue;
				}
				// 分析column和field对应关系
				String columnLabel = columnAnno.name().trim();
				if (columnLabel.length() == 0) {
					columnLabel = field.getName();
				}
				// 若导出类覆盖了基类的注解，则以导出类为准
				if (columnPropertyMap.containsKey(columnLabel)) {
					continue;
				}
				boolean isKey = false;
				if (idAnno != null) {
					isKey = true;
				}
				boolean isMarkDeleted = false;
				if (ormAnno != null) {
					isMarkDeleted = ormAnno.isMarkDeleted();
				}
				try {
					String fieldName = field.getName();
					Object fieldValue = field.get(po);
					Class<?> fieldClass = field.getType();
					PropertyBean prop = new PropertyBean();
					prop.setPropertyName(fieldName);
					prop.setPropertyValue(fieldValue);
					prop.setPropertyClazz(fieldClass);
					columnPropertyMap.put(columnLabel, prop);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					logger.log(Level.SEVERE, "分析po属性时出现异常", e);
				}
				// 判断此属性是否主键或标记删除
				if (isKey && keyColumnLabel == null) {
					keyColumnLabel = columnLabel;
					sa.setKeyColumnLabel(keyColumnLabel);
					// 此处写成else if实际上可以防止既注解了isKey，又注解了isMarkDeleted的情况
				} else if (isMarkDeleted && markDeletedColumnLabel == null) {
					markDeletedColumnLabel = columnLabel;
					sa.setMarkDeletedColumnLabel(markDeletedColumnLabel);
				}
			}
			clazz = clazz.getSuperclass();
		}
	}
}
