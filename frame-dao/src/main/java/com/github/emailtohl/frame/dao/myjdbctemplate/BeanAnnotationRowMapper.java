package com.github.emailtohl.frame.dao.myjdbctemplate;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.github.emailtohl.frame.dao.preparedstatementfactory.Orm;
import com.github.emailtohl.frame.util.BeanTools;

/**
 *************************************************
 * 实现了Spring的RowMapper接口
 * 以对象注解与表列名对应的方式
 * 对本类和其超类进行扫描
 * @param <T>
 * @author  helei
 * @version 1.0
 * 2015年4月30日 创建文件                             
 * 
 *************************************************
 */
public class BeanAnnotationRowMapper<T> implements RowMapper<T> {
	private Class<T> poClass;
	private volatile Map<String, Field> columnPropertMapper;

	public BeanAnnotationRowMapper(Class<T> poClass) {
		super();
		this.poClass = poClass;
	}

	@Override
	public T mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		if (poClass == null)
			throw new NullPointerException();
		setColumnPropertMap(resultSet);
		T po = null;
		try {
			po = (T) poClass.newInstance();
			Iterator<Entry<String, Field>> iterator = columnPropertMapper.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, Field> entry = iterator.next();
				String columnLabel = entry.getKey();
				Field field = entry.getValue();
				Object obj = resultSet.getObject(columnLabel);
				BeanTools.injectField(field, po, obj);// 此工具方法可以对基本类型的属性注入值
			}
		} catch (IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}
		return po;
	}

	/**
	 * 建立行集与po的对应关系——行集的columnLabel对应po的注解，对应关系如下： 行集的columnLabel → po注解 → po属性
	 * 所以，映射关系以行集为准，也就是说先确保数据库里有的columnLabel，然后再寻找是否有po属性对应，这样不会因注解的错误导致异常
	 * 
	 * @param resultSet： 行集
	 * @throws SQLException
	 */
	private void setColumnPropertMap(ResultSet resultSet) throws SQLException {
		if (columnPropertMapper != null)// 若多个线程访问它时，此时还是null，需要用volatile来做保护
			return;
		synchronized (this) {
			columnPropertMapper = new HashMap<String, Field>();
			ResultSetMetaData meta = resultSet.getMetaData();
			Class<? super T> clazz = poClass;
			while (clazz != null && clazz != Object.class) {
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					Orm anno = field.getAnnotation(Orm.class);
					if (anno != null) {
						field.setAccessible(true);
						String fieldAnnotation = anno.columnLabel();
						if (fieldAnnotation.length() != 0) {
							for (int i = 1; i <= meta.getColumnCount(); i++) {
								String columnLabel = meta.getColumnLabel(i);
								if (columnLabel != null && fieldAnnotation.equalsIgnoreCase(columnLabel)) {
									columnPropertMapper.put(columnLabel, field);
								}
							}
						}
					}
				}
				clazz = clazz.getSuperclass();
			}
		}
	}
}
