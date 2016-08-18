package com.github.emailtohl.frame.dao.preparedstatementfactory;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * 
 *************************************************
 * 本类实现了OrmAnalyzer，仅将po基类中的Orm注解的信息解析到SqlAndArgs对象中
 * 使用本类主要考虑到一般与数据库对应的注解信息往往都是在基类中，而导出类中的注解往往仅作为查询时接收信息所用
 * 
 * @author helei 2015.07.22
 *************************************************
 */
@Deprecated
public class PoBaseAnalyzer implements OrmAnalyzer {
	/**
	 * 一般po基类注解的属性才对应数据库真实表字段，本类只找出基类中被注解且有值的属性，并存储到columnPropertyMap中 注意：
	 * （1）执行本方法，若传入参数是null，或tableName是null，则抛出异常，所以客户程序就无需判断null了
	 * （2）SqlAndArgs.columnPropertyMap也一定不为空，客户程序可直接使用
	 */
	public SqlAndArgs parse(Object po) {
		if (po == null)
			throw new NullPointerException("传入po是null");
		SqlAndArgs sa = new SqlAndArgs();
		String tableName = null;// 此po对应的数据库表名
		String keyColumnLabel = null;// 主键的columnLabel
		String markDeletedColumnLabel = null;// 标记删除的columnLabel
		Map<String, PropertyBean> columnPropertyMap = sa.getColumnPropertyMap();
		Class<?> clazz = po.getClass();
		Class<?> base = null;
		Orm poAnno = null;
		Field[] fields = null;

		while (clazz != Object.class) {// 分析po各个层次上的注解信息，直到到达Object顶级
			// 首先在类注解上分析是否有tableName信息，但如果基类有表名的注解信息，则以基类为准
			poAnno = clazz.getAnnotation(Orm.class);
			if (poAnno != null && poAnno.tableName().length() != 0) {
				tableName = poAnno.tableName();// 以本类的注释为准
			}
			// 然后分析各属性注解信息，若继承层次上有覆盖，则以覆盖为准
			fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				poAnno = field.getAnnotation(Orm.class);
				if (poAnno != null) {
					base = clazz;
					break;
				}
			}
			clazz = clazz.getSuperclass();
		}

		if (tableName == null || base == null)
			throw new IllegalArgumentException("传入po错误");
		else
			sa.setTableName(tableName);

		fields = base.getDeclaredFields();
		AccessibleObject.setAccessible(fields, true);
		for (Field field : fields) {
			poAnno = field.getAnnotation(Orm.class);
			if (poAnno == null)
				continue;
			try {// 属性分析
				String columnLabel = poAnno.columnLabel().trim();
				boolean isKey = poAnno.isKey();
				boolean isMarkDeleted = poAnno.isMarkDeleted();
				String fieldName = field.getName();
				Object fieldValue = field.get(po);
				Class<?> fieldClass = field.getType();
				// 分析column和Property对应关系
				// 若导出类覆盖了基类的注解，则以基类为准
				if (columnLabel.trim().length() != 0) {
					PropertyBean prop = new PropertyBean();
					prop.setPropertyName(fieldName);
					prop.setPropertyValue(fieldValue);
					prop.setPropertyClazz(fieldClass);
					columnPropertyMap.put(columnLabel, prop);

					// 判断此属性是否主键或标记删除
					if (isKey && keyColumnLabel == null) {
						keyColumnLabel = columnLabel.trim();
						sa.setKeyColumnLabel(keyColumnLabel);
						// 此处写成else if实际上可以防止既注解了isKey，又注解了isMarkDeleted的情况
					} else if (isMarkDeleted && markDeletedColumnLabel == null) {
						markDeletedColumnLabel = columnLabel;
						sa.setMarkDeletedColumnLabel(markDeletedColumnLabel);
					}
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return sa;
	}
}
