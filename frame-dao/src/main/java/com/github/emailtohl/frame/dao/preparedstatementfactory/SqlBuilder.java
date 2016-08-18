package com.github.emailtohl.frame.dao.preparedstatementfactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *************************************************
 * 此类为PreparedStatement服务，分析被Orm注解的po，创建出SQL语句和对应的参数
 * SELECT语句往往非常复杂，通常需手写SQL，但可通过本类的getSelectCondition()简化条件语句的拼写
 * 
 * @author helei 2015.07.22
 *************************************************
 */
public class SqlBuilder {
	// OrmAnalyzer可保证返回的SqlAndArgs，以及SqlAndArgs的tableName、columnPropertyMap不为null，否则会抛出异常
	private OrmAnalyzer analyzer;

	public SqlBuilder() {
		analyzer = new PoAnalyzer();
	}

	@Deprecated
	public SqlBuilder(boolean onlyParsingBaseClass) {
		if (onlyParsingBaseClass)
			analyzer = new PoBaseAnalyzer();
		else
			analyzer = new PoAnalyzer();
	}

	public SqlBuilder(OrmAnalyzer analyzer) {
		this.analyzer = analyzer;
	}

	// 为外界提供切换orm解析器的机会
	public void setAnalyzer(OrmAnalyzer analyzer) {
		this.analyzer = analyzer;
	}

	/**
	 * 调用程序可能会获取po中的keyColumnLabel或markDeletedColumnLabel，
	 * 通过OrmAnalyzer解析出po的基本信息
	 * 
	 * @return OrmAnalyzer 用它来解析出po的基本信息
	 */
	public OrmAnalyzer getAnalyzer() {
		return analyzer;
	}

	/**
	 * 返回简单的SELECT查询语句，此语句仅用于单表查询
	 */
	public SqlAndArgs getSimpleQueryStatement(Object po) {
		SqlAndArgs sa = getSelectCondition(po);
		String tableName = sa.getTableName();
		String condition = sa.getPreparedSQL();
		StringBuilder preSql = new StringBuilder();
		preSql.append("SELECT ").append(tableName).append(".* FROM ").append(tableName).append(condition);

		String keyColumnLabel = sa.getKeyColumnLabel();
		if (keyColumnLabel != null && keyColumnLabel.trim().length() != 0) {
			preSql.append(" ORDER BY ").append(tableName).append('.').append(keyColumnLabel).append(" DESC ");
		}
		sa.setPreparedSQL(preSql.toString());
		return sa;
	}

	/**
	 * 返回INSERT语句 第二个参数：ignoreNull，如果为true，则不管po中被注解的字段是否为空，都会拼接出来，主要用于批量执行SQL中
	 */
	public SqlAndArgs getInsertStatement(Object po, boolean ignoreNull) {
		SqlAndArgs sa = analyzer.parse(po);
		String tableName = sa.getTableName();
		Map<String, PropertyBean> columnPropertyMap = sa.getColumnPropertyMap();
		List<PropertyBean> argsList = new ArrayList<PropertyBean>();
		StringBuilder insertItems = new StringBuilder();
		StringBuilder parameters = new StringBuilder();
		StringBuilder preSql = new StringBuilder();
		for (Map.Entry<String, PropertyBean> entry : columnPropertyMap.entrySet()) {
			String columnLabel = entry.getKey();
			PropertyBean prop = entry.getValue();// prop在PoAnalyzer中添加进Map，不可能为null
			if (ignoreNull) {
				if (parameters.length() == 0) {
					parameters.append('?');
					insertItems.append(columnLabel);
				} else {
					parameters.append(',').append('?');
					insertItems.append(',').append(columnLabel);
				}
				argsList.add(prop);
			} else {
				if (prop.getPropertyValue() == null)
					continue;
				if (parameters.length() == 0) {
					parameters.append('?');
					insertItems.append(columnLabel);
				} else {
					parameters.append(',').append('?');
					insertItems.append(',').append(columnLabel);
				}
				argsList.add(prop);
			}
		}
		if (parameters.length() != 0 && insertItems.length() != 0) {
			preSql.append("INSERT INTO ").append(tableName).append(" (").append(insertItems).append(')')
					.append(" VALUES (").append(parameters).append(')');
		}
		sa.setPreparedSQL(preSql.toString());
		sa.setArgsList(argsList);
		return sa;
	}

	/**
	 * 返回UPDATE语句 为了安全起见，不允许条件为空的情况，若出现无条件的情况，则抛出异常
	 * 第二个参数：ignoreNull，如果为true，则不管po中被注解的字段是否为空，都会拼接出来，主要用于批量执行SQL中
	 */
	public SqlAndArgs getUpdateStatement(Object po, Object condition, boolean ignoreNull) {
		SqlAndArgs conditionSa = getCondition(condition);
		List<PropertyBean> conditionArgs = conditionSa.getArgsList();
		if (conditionArgs.isEmpty()) // 若无条件
			throw new IllegalArgumentException("此更改由于无条件，故终止操作");
		SqlAndArgs sa = getUpdateSetItem(po, ignoreNull);// 若无更新项，则返回getUpdateSetItem()会抛异常，程序终止
		List<PropertyBean> argsList = sa.getArgsList();
		argsList.addAll(conditionArgs);
		// 不用担心此处的tableName是null，否则在get SqlAndArgs时已抛出异常
		String tableName = conditionSa.getTableName();
		StringBuilder preSql = new StringBuilder();
		preSql.append("UPDATE ").append(tableName).append(sa.getPreparedSQL()).append(conditionSa.getPreparedSQL());
		sa.setPreparedSQL(preSql.toString());
		sa.setArgsList(argsList);
		return sa;
	}

	/**
	 * 返回DELETE语句 为了安全起见，不允许条件为空的情况，若出现无条件的情况，则抛出异常
	 */
	public SqlAndArgs getDeleteStatement(Object po) {
		SqlAndArgs sa = getCondition(po);
		if (sa.getArgsList().isEmpty()) // 若无条件
			throw new IllegalArgumentException("此删除由于无条件，故终止操作");
		String tableName = sa.getTableName();
		String condition = sa.getPreparedSQL();
		String markDeletedColumnLabel = sa.getMarkDeletedColumnLabel();// 标记删除的columnLabel
		PropertyBean markDeletedBean = null;
		StringBuilder preSql = new StringBuilder();
		// 先判断是否标记删除注释
		if (markDeletedColumnLabel != null && markDeletedColumnLabel.length() != 0) {
			markDeletedBean = sa.getColumnPropertyMap().get(markDeletedColumnLabel);
		}
		// 如果markDeleted是null，则说明markDeletedColumnLabel也不符合条件
		// 另外，markDeletedBean在PoAnalyzer中添加进Map，不可能为null，且其propertyValue的值已被注入
		if (markDeletedBean == null) {
			preSql.append("DELETE FROM ").append(tableName).append(condition);
		} else {
			preSql.append("UPDATE ").append(tableName).append(" SET ").append(markDeletedColumnLabel).append(" = 1")
					.append(condition);
		}
		sa.setPreparedSQL(preSql.toString());
		return sa;
	}

	/**
	 * 返回SQL语句的一个片段，此片段用于SELECT语句中，例如： WHERE 1=1 AND t_supplier.name LIKE ?
	 * 而参数类似于'%XXX%'
	 */
	public SqlAndArgs getSelectCondition(Object po) {
		SqlAndArgs sa = analyzer.parse(po);
		String tableName = sa.getTableName();
		String markDeletedColumnLabel = sa.getMarkDeletedColumnLabel();// 标记删除的columnLabel
		Map<String, PropertyBean> columnPropertyMap = sa.getColumnPropertyMap();

		List<PropertyBean> argsList = new ArrayList<PropertyBean>();
		StringBuilder condition = new StringBuilder();
		condition.append(" WHERE 1=1");
		for (Map.Entry<String, PropertyBean> entry : columnPropertyMap.entrySet()) {
			String columnLabel = entry.getKey();
			PropertyBean prop = entry.getValue();// prop在PoAnalyzer中添加进Map，不可能为null
			Object propValue = prop.getPropertyValue();
			if (propValue == null)
				continue;
			if (prop.getPropertyClazz() == String.class) {
				propValue = "%" + propValue + "%";
				condition.append(" AND ").append(tableName).append('.').append(columnLabel).append(" LIKE ?");
				prop.setPropertyValue(propValue);
			} else {
				condition.append(" AND ").append(tableName).append('.').append(columnLabel).append(" = ?");
			}
			argsList.add(prop);
		}
		// 如果此po中有标记删除的注解，但是并没有将此删除位作为查询条件，则不要将被标记删除的记录查询出来
		if (markDeletedColumnLabel != null && condition.indexOf(markDeletedColumnLabel) == -1) {
			condition.append(" AND ").append('(').append(tableName).append('.').append(markDeletedColumnLabel)
					.append(" IS NULL OR ").append(tableName).append('.').append(markDeletedColumnLabel).append(" <> 1")
					.append(')');
		}
		sa.setPreparedSQL(condition.toString());
		sa.setArgsList(argsList);
		return sa;
	}

	/**
	 * 返回SQL语句的一个片段，此片段用于UPDATE和DELETE语句中，例如： WHERE t_supplier.name = 'XXX'
	 */
	public SqlAndArgs getCondition(Object po) {
		SqlAndArgs sa = analyzer.parse(po);
		Map<String, PropertyBean> columnPropertyMap = sa.getColumnPropertyMap();

		List<PropertyBean> argsList = new ArrayList<PropertyBean>();
		StringBuilder condition = new StringBuilder();
		condition.append(" WHERE 1=1");
		for (Map.Entry<String, PropertyBean> entry : columnPropertyMap.entrySet()) {
			String columnLabel = entry.getKey();
			PropertyBean prop = entry.getValue();// prop在PoAnalyzer中添加进Map，不可能为null
			Object propValue = prop.getPropertyValue();
			if (propValue != null && propValue.toString().trim().length() != 0) {
				condition.append(" AND ").append(columnLabel).append(" = ?");
				argsList.add(prop);
			}
		}
		sa.setPreparedSQL(condition.toString());
		sa.setArgsList(argsList);
		return sa;
	}

	/**
	 * 返回SQL语句的一个片段，此片段用于UPDATE的SET项中，例如： SET tel = '12345678', rank = 2
	 * 如果ignoreNull==true，则，不论该属性是否为null，均拼接到SQL语句中
	 */
	public SqlAndArgs getUpdateSetItem(Object po, boolean ignoreNull) {
		SqlAndArgs sa = analyzer.parse(po);
		Map<String, PropertyBean> columnPropertyMap = sa.getColumnPropertyMap();

		List<PropertyBean> argsList = new ArrayList<PropertyBean>();
		StringBuilder updateItems = new StringBuilder();
		for (Map.Entry<String, PropertyBean> entry : columnPropertyMap.entrySet()) {
			String columnLabel = entry.getKey();
			PropertyBean prop = entry.getValue();// prop在PoAnalyzer中添加进Map，不可能为null
			if (ignoreNull) {
				if (updateItems.length() == 0) {
					updateItems.append(columnLabel).append(" = ?");
				} else {
					updateItems.append(" , ").append(columnLabel).append(" = ?");
				}
				argsList.add(prop);
			} else {
				if (prop.getPropertyValue() == null)
					continue;
				if (updateItems.length() == 0) {
					updateItems.append(columnLabel).append(" = ?");
				} else {
					updateItems.append(" , ").append(columnLabel).append(" = ?");
				}
				argsList.add(prop);
			}
		}
		if (updateItems.length() > 0)
			updateItems.insert(0, " SET ");
		else
			throw new IllegalArgumentException("UPDATE语句中SET无更新项");
		sa.setPreparedSQL(updateItems.toString());
		sa.setArgsList(argsList);
		return sa;
	}

	/**
	 * 返回SQL语句的一个片段，此片段仅仅只是SELECT语句具体的查询项，查询项以基类注解信息为准，例如： t_supplier.id,
	 * t_supplier.name
	 * 注意：
	 * （1）本方法不返回FROM t_supplier，因为FROM前后可能需要插入一些联表查询的语句
	 * （2）本方法处理后SqlAndArgs的preparedSQL仅如：t_supplier.id, t_supplier.name
	 * 如需联表查询，可将其他表的查询项添加到preparedSQL前后
	 */
	public SqlAndArgs getSelectItem(Object po) {
		SqlAndArgs sa = analyzer.parse(po);
		String tableName = sa.getTableName();
		Map<String, PropertyBean> columnPropertyMap = sa.getColumnPropertyMap();

		StringBuilder selectItems = new StringBuilder();
		for (Map.Entry<String, PropertyBean> entry : columnPropertyMap.entrySet()) {
			String columnLabel = entry.getKey();
			if (selectItems.length() == 0) {
				selectItems.append(' ').append(tableName).append('.').append(columnLabel);
			} else {
				selectItems.append(", ").append(tableName).append('.').append(columnLabel);
			}
		}
		if (selectItems.length() != 0) {
			sa.setPreparedSQL(selectItems.toString());
		} else {
			sa.setPreparedSQL(" * ");
		}
		sa.setArgsList(new ArrayList<PropertyBean>());// 为了保持一致性，此处不能让argsList字段为null
		return sa;
	}

	/**
	 * 本方法返回一个完整的带总行数统计的SELECT语句，主要用于分页查询功能所需要的总页数信息，例如： SELECT t_supplier.name,
	 * (SELECT COUNT (*) AS _count_ FROM t_supplier) FROM t_supplier WHERE 1 = 1
	 * AND t_supplier. RANK = 2 AND ( t_supplier.mark_deleted IS NULL OR
	 * It_supplier.mark_deleted <> 'deleted' ) ORDER BY t_supplier. ID
	 * 
	 * 第二个参数theColumnLabelOftotalRow，需要用户提供总记录数的列名，若为null，则默认为"_count_"
	 * 
	 * 注意：基于性能考虑，本方法返回了完整的语句，若需联表查询，可根据需要在preparedSQL字符串中中插入其他查询项
	 */
	public SqlAndArgs getSelectStatementWithCountItem(Object po, String theColumnLabelOftotalRow) {
		SqlAndArgs sa = analyzer.parse(po);
		if (theColumnLabelOftotalRow == null || theColumnLabelOftotalRow.trim().length() == 0)
			theColumnLabelOftotalRow = "_count_";
		String tableName = sa.getTableName();
		if (tableName == null)
			throw new IllegalArgumentException("被注解的对象中没有表名");
		String keyColumnLabel = sa.getKeyColumnLabel();
		String markDeletedColumnLabel = sa.getMarkDeletedColumnLabel();// 标记删除的columnLabel
		Map<String, PropertyBean> columnPropertyMap = sa.getColumnPropertyMap();

		StringBuilder preSql = new StringBuilder();
		StringBuilder condition = new StringBuilder();
		List<PropertyBean> argsList = new ArrayList<PropertyBean>();
		for (Map.Entry<String, PropertyBean> entry : columnPropertyMap.entrySet()) {
			String columnLabel = entry.getKey();
			PropertyBean prop = entry.getValue();// prop在PoAnalyzer中添加进Map，不可能为null
			Object propValue = prop.getPropertyValue();
			// 首先计算查询项
			if (preSql.length() == 0) {
				preSql.append(tableName).append('.').append(columnLabel);
			} else {
				preSql.append(", ").append(tableName).append('.').append(columnLabel);
			}
			if (preSql.length() != 0) {
				preSql.insert(0, "SELECT ");
			} else {
				preSql.insert(0, "SELECT * ");
			}
			// 然后根据存在的属性值计算查询条件
			if (propValue == null)
				continue;
			if (prop.getPropertyClazz() == String.class) {
				propValue = "%" + propValue + "%";
				if (condition.length() == 0) {
					condition.append(tableName).append('.').append(columnLabel).append(" LIKE ?");
				} else {
					condition.append(" AND ").append(tableName).append('.').append(columnLabel).append(" LIKE ?");
				}
			} else {
				if (condition.length() == 0) {
					condition.append(tableName).append('.').append(columnLabel).append(" = ?");
				} else {
					condition.append(" AND ").append(tableName).append('.').append(columnLabel).append(" = ?");
				}
			}
			argsList.add(prop);
		}
		// 如果此po中有标记删除的注解，但是并没有将此删除位作为查询条件，则不要将被标记删除的记录查询出来
		if (markDeletedColumnLabel != null && condition.indexOf(markDeletedColumnLabel) == -1) {
			if (condition.length() == 0) {
				condition.append(tableName).append('.').append(markDeletedColumnLabel).append(" IS NULL OR ")
						.append(tableName).append('.').append(markDeletedColumnLabel).append(" <> 1");
			} else {
				condition.append(" AND ").append('(').append(tableName).append('.').append(markDeletedColumnLabel)
						.append(" IS NULL OR ").append(tableName).append('.').append(markDeletedColumnLabel)
						.append(" <> 1").append(')');
			}
		}

		// 此处预留WHERE，客户程序就不用判断condition是否为空了，可直接用" AND "拼接
		condition.insert(0, " WHERE 1=1 ");

		// 拼装SELECT COUNT(*)语句
		preSql.append(" (SELECT count(*) ").append(" AS ").append(theColumnLabelOftotalRow).append(" FROM ")
				.append(tableName).append(condition).append(')').append(" FROM ").append(tableName).append(condition);
		if (keyColumnLabel != null && keyColumnLabel.trim().length() != 0) {
			preSql.append(" ORDER BY ").append(keyColumnLabel).append(" DESC ");
		}
		sa.setPreparedSQL(preSql.toString());
		argsList.addAll(argsList);// 再添加一份相同的参数，一份用于SELECT COUNT(*)，一份用于条件查询
		sa.setArgsList(argsList);
		return sa;
	}

	/**
	 * 获取一条时间范围内的sql片段（例如“ AND timeColumnLabel > ? AND timeColumnLabel < ?”）和其参数
	 * 
	 * @param timeColumnLabel
	 *            在数据库中该时间的ColumnLabel名
	 * @param startTime
	 *            起始时间
	 * @param endTime
	 *            结束时间
	 * @return SqlAndArgs 包含sql片段及对应的参数
	 */
	public <T> SqlAndArgs getSelectTimeRangeCondition(String timeColumnLabel, T startTime, T endTime) {
		StringBuilder timeRange = new StringBuilder();
		List<PropertyBean> argsList = new ArrayList<PropertyBean>();
		PropertyBean prop;
		if (startTime != null && startTime.toString().length() > 0) {
			timeRange.append(" AND ").append(timeColumnLabel).append(" > ?");
			prop = new PropertyBean();
			Class<? extends Object> clz = startTime.getClass();
			prop.setPropertyClazz(clz);
			prop.setPropertyName(clz.getName());
			prop.setPropertyValue(startTime);
			argsList.add(prop);
		}
		if (endTime != null && endTime.toString().length() > 0) {
			timeRange.append(" AND ").append(timeColumnLabel).append(" < ?");
			prop = new PropertyBean();
			Class<? extends Object> clz = endTime.getClass();
			prop.setPropertyClazz(clz);
			prop.setPropertyName(clz.getName());
			prop.setPropertyValue(endTime);
			argsList.add(prop);
		}
		SqlAndArgs sa = new SqlAndArgs();
		sa.setPreparedSQL(timeRange.toString());
		sa.setArgsList(argsList);
		return sa;
	}

	/**
	 * 将批量执行的preSql和参数用Map管理起来，一条preSql语句对应多组参数，每组用List管理
	 * 
	 * @param sqlAndArgsList
	 *            List组成的多个SqlAndArgs 注意：sqlAndArgsList中不要有查询的sql，实际上查询在批量执行中无意义
	 * @return Map<String, List<Object[]>> Map管理的多条preSql，每条preSql可能对应多组参数
	 */
	public Map<String, List<Object[]>> getSqlAndBatchArgsMapper(List<SqlAndArgs> sqlAndArgsList) {
		if (sqlAndArgsList == null)
			throw new NullPointerException("sqlAndArgsList is null");
		Map<String, List<Object[]>> map = new LinkedHashMap<String, List<Object[]>>();
		String preSql = null;
		Object[] params = null;
		List<Object[]> argsList = null;
		for (SqlAndArgs sa : sqlAndArgsList) {
			preSql = sa.getPreparedSQL();
			params = sa.getParamValues();
			argsList = map.get(preSql);
			if (argsList == null) {
				argsList = new LinkedList<Object[]>();
				argsList.add(params);
				map.put(preSql, argsList);
			} else {
				argsList.add(params);
			}
		}
		return map;
	}

	/**
	 * 本方法分析一个po对象，根据该po的Orm注解获取到主键的名字和javaType，然后封装成PropertyBean对象返回
	 * 
	 * @param po
	 *            被@Orm注解的对象
	 * @return PropertyBean 这个对象描述了该po中对主键的描述
	 */
	public PropertyBean getKeyPropertyBean(Object po) {
		if (po == null)
			throw new NullPointerException("po是null，无法查询主键属性");
		String keyName = null;
		Object propertyValue = null;
		Class<?> propertyClazz = null;
		Class<?> clazz = po.getClass();
		while (clazz != Object.class && keyName == null) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				Orm fieldAnnotation = field.getAnnotation(Orm.class);
				if (fieldAnnotation != null && fieldAnnotation.isKey()) {
					field.setAccessible(true);
					keyName = field.getName();
					propertyClazz = field.getType();
					try {
						propertyValue = field.get(po);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
			clazz = clazz.getSuperclass();
		}
		PropertyBean pb = new PropertyBean();
		pb.setPropertyName(keyName);
		pb.setPropertyClazz(propertyClazz);
		pb.setPropertyValue(propertyValue);
		return pb;
	}
}
