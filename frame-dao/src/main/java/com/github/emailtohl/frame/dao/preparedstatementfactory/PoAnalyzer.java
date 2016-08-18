package com.github.emailtohl.frame.dao.preparedstatementfactory;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *************************************************
 * 本类实现了OrmAnalyzer，将po对象中所有被Orm注解的信息解析到SqlAndArgs对象中
 * 
 * @author helei
 * 2015.07.22
 * 
 * 修改重要逻辑，对Object的分析，从在Class上注解了@Orm上才开始分析，不再将扩展类中的属性分析出来（一般扩展类中被注解@Orm的属性是用于接收查询的结果，并不用用于分析SQL）
 * 2016.02.21
 *************************************************
 */
public class PoAnalyzer implements OrmAnalyzer {
	private static final Logger logger = Logger.getLogger(PoAnalyzer.class.getName());

	/**
	 * 本方法将po中被注解且有值的属性存储到columnPropertyMap中，供后续SqlBuilder分析成PreparedStatement
	 * 注意： （1）执行本方法，若传入参数是null，或tableName是null，则抛出异常，所以客户程序就无需判断null了
	 * （2）SqlAndArgs.columnPropertyMap也一定不为空，客户程序可直接使用
	 */
	@Override
	public SqlAndArgs parse(Object po) {
		if (po == null) {
			logger.log(Level.WARNING, "传入po是null");
			throw new NullPointerException("传入po是null");
		}
		SqlAndArgs sa = new SqlAndArgs();
		setTableName(po, sa);
		Class<?> entityClass = sa.getEntityClass();
		setColumnLabel(po, entityClass, sa);
		return sa;
	}

	/**
	 * 一般po会被其他JavaBean继承，本方法寻找继承结构上哪个class被注解了表名，并且从该class开始分析属性的注解
	 * 注意：以@Table中的name注解为准
	 * 
	 * @param po
	 * @param sa
	 */
	private void setTableName(Object po, SqlAndArgs sa) {
		String tableName = null;
		Class<?> entityClass = null;
		Class<?> clazz = po.getClass();
		while (tableName == null && clazz != Object.class) {// 分析po各个层次上的注解信息，直到到达Object顶级
			Orm ormAnno = clazz.getAnnotation(Orm.class);
			if (ormAnno != null) {
				tableName = ormAnno.tableName().trim();
				if (tableName.length() == 0) {
					tableName = clazz.getSimpleName();
				}
				entityClass = clazz;
			}
			clazz = clazz.getSuperclass();
		}
		if (tableName == null || entityClass == null) {
			logger.log(Level.SEVERE, "未注解表名");
			throw new IllegalArgumentException("未注解表名");
		} else {
			sa.setTableName(tableName);
			sa.setEntityClass(entityClass);
		}
	}

	/**
	 * 获取实例域上注解的columnLabel信息
	 * 
	 * @param po
	 * @param entityClass
	 * @param sa
	 */
	private void setColumnLabel(Object po, Class<?> entityClass, SqlAndArgs sa) {
		String keyColumnLabel = null;// 主键的columnLabel
		String markDeletedColumnLabel = null;// 标记删除的columnLabel
		Map<String, PropertyBean> columnPropertyMap = sa.getColumnPropertyMap();
		while (entityClass != null && entityClass != Object.class) {
			Field[] fields = entityClass.getDeclaredFields();
			AccessibleObject.setAccessible(fields, true);
			for (Field field : fields) {
				Orm ormAnno = field.getAnnotation(Orm.class);
				if (ormAnno == null) {
					continue;
				}
				String columnLabel = ormAnno.columnLabel().trim();
				// 分析column和field对应关系
				// 若导出类覆盖了基类的注解，则以导出类为准
				if (columnLabel.length() == 0 || columnPropertyMap.containsKey(columnLabel)) {
					continue;
				}
				boolean isKey = ormAnno.isKey();
				boolean isMarkDeleted = ormAnno.isMarkDeleted();
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
			entityClass = entityClass.getSuperclass();
		}
	}
}
