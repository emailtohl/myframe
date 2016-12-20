package com.github.emailtohl.frame.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.github.emailtohl.frame.dao.datasource.DataSourceImpl;
import com.github.emailtohl.frame.dao.myjdbctemplate.BeanAnnotationRowMapper;
import com.github.emailtohl.frame.dao.myjdbctemplate.RowMapper;
import com.github.emailtohl.frame.dao.myjdbctemplate.SimpleJdbcTemplate;
import com.github.emailtohl.frame.dao.preparedstatementfactory.SqlAndArgs;
import com.github.emailtohl.frame.dao.preparedstatementfactory.SqlBuilder;
import com.github.emailtohl.frame.util.BeanUtil;

/**
 * 
 * 基于Orm注解，提供dao的基础功能，可被业务dao继承 
 * （1）简单查询功能 
 * （2）分页查询功能 
 * （3）增、删、改功能 
 * （4）批量增、删、改功能
 * （5）数据同步功能
 * 
 * @author helei \
 * 2015.11.03
 */
public class BaseDao {
	private static final Logger logger = Logger.getLogger(BaseDao.class.getName());
	protected DataSource dataSource;
	protected SimpleJdbcTemplate simpleJdbcTemplate;
	protected SqlBuilder sqlBuilder = new SqlBuilder();// 解析po时，仅对其基类进行分析

	public BaseDao(DataSource dataSource) {
		this.dataSource = dataSource;
		simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}

	/**
	 * 向数据库插入一条数据
	 * 
	 * @param po
	 *            携带着要插入的值
	 * @return 若po中注解了主键，且数据库自动创建了主键值，则以long类型返回主键值，否则返回修改行数
	 */
	public long insert(Object po) {
		if (po == null)
			return 0L;
		SqlAndArgs sqlAndArgs = sqlBuilder.getInsertStatement(po, false);
		return simpleJdbcTemplate.insert(sqlAndArgs.getPreparedSQL(), sqlAndArgs.getParamValues(),
				sqlAndArgs.getKeyColumnLabel());
	}

	/**
	 * 使用Spring的jdbcTemplate使用的插入方法
	 * @param po
	 * @return
	 */
/*	public long insert(Object po) {
		if (po == null)
			return 0L;
		SqlAndArgs sqlAndArgs = sqlBuilder.getInsertStatement(po, false);
		final String preparedSQL = sqlAndArgs.getPreparedSQL();
		final Object[] args = sqlAndArgs.getParamValues();
		KeyHolder keyHolder = new GeneratedKeyHolder();
		PreparedStatementCreator psc = new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(preparedSQL,
						PreparedStatement.RETURN_GENERATED_KEYS);
				for (int i = 0; i < args.length; i++)
					ps.setObject(i + 1, args[i]);
				return ps;
			}
		};
		jdbcTemplate.update(psc, keyHolder);
		Object keyValue = null;
		try {
			keyValue = keyHolder.getKey();
		} catch (InvalidDataAccessApiUsageException e) {
			// e.printStackTrace();
			keyValue = keyHolder.getKeys().get(sqlAndArgs.getKeyColumnLabel());// 尝试通过主键名查找
		}
		if (keyValue instanceof Number) {
			return ((Number) keyValue).longValue();
		} else {
			return -1L;// 如果获取的是非整数主键，或者本表没有主键，那么返回-1
		}
	}*/

	/**
	 * 分析po的属性值，组成查询的sql语句，用list容器返回结果
	 * 
	 * @param po
	 *            携带查询数据的bean对象
	 * @param <T>
	 *            po的类型
	 * @return 用list容器返回结果
	 */
	public <T> List<T> query(T po) {
		if (po == null) {
			return new ArrayList<T>();
		}
		SqlAndArgs sqlAndArgs = sqlBuilder.getSimpleQueryStatement(po);
		@SuppressWarnings("unchecked")
		RowMapper<T> rowMapper = new BeanAnnotationRowMapper<T>((Class<T>) po.getClass());
		return (List<T>) simpleJdbcTemplate.query(sqlAndArgs.getPreparedSQL(), sqlAndArgs.getParamValues(), rowMapper);
	}

	/**
	 * 分析po属性，将查询结果封装到List中，其中每个元素是一个Object数组，存放每列的值
	 * 
	 * @param po
	 *            携带查询数据的bean对象
	 * @param <T>
	 *            po的类型
	 * @return 用list容器返回结果
	 */
	public <T> List<Object[]> queryArray(T po) {
		if (po == null) {
			return new ArrayList<Object[]>();
		}
		SqlAndArgs sqlAndArgs = sqlBuilder.getSimpleQueryStatement(po);
		return simpleJdbcTemplate.queryArray(sqlAndArgs.getPreparedSQL(), sqlAndArgs.getParamValues());
	}

	/**
	 * 分析po属性，将查询结果封装到List中，其中每个元素是一个Map，key是列名，value是查询值
	 * 
	 * @param po
	 *            携带查询数据的bean对象
	 * @param <T>
	 *            po的类型
	 * @return 用list容器返回结果
	 */
	public <T> List<Map<String, Object>> queryForList(T po) {
		if (po == null) {
			return new ArrayList<Map<String, Object>>();
		}
		SqlAndArgs sqlAndArgs = sqlBuilder.getSimpleQueryStatement(po);
		return simpleJdbcTemplate.queryForList(sqlAndArgs.getPreparedSQL(), sqlAndArgs.getParamValues());
	}

	/**
	 * 本方法返回一个Pager对象，包含查询结果，最大行数，当前页码等信息
	 * 注意：本方法的分页查询语句是基于MySQL和Postgresql的，对于Oracle或SQLServer等数据库不适合
	 * 
	 * @param selectSql
	 *            查询的sql
	 * @param args
	 *            参数数组
	 * @param rowMapper
	 *            将查询结果与被注入对象的映射
	 * @param pageNum
	 *            需要查询的页码
	 * @param pageSize
	 *            每页最多行
	 * @param <T>
	 *            po的类型
	 * @return 封装到Pager中的查询结果，包括结果list，最大页数，当前页数等信息
	 */
	public <T> Pager<T> pageForMySqlOrPostgresql(String selectSql, Object[] args, RowMapper<T> rowMapper,
			Long pageNum, Integer pageSize) {
		if (selectSql == null || args == null) {
			Pager<T> p = new Pager<T>();
			p.setDataList(new ArrayList<T>());
			return p;
		}
		if (pageNum == null || pageNum < 1L)
			pageNum = 1L;
		if (pageSize == null || pageSize < 1)
			pageSize = 10;// 默认每页10条记录
		Long offset = (pageNum - 1) * pageSize;// offset在数据库中的序号是以0开始
		String pageSql = " LIMIT " + pageSize + " OFFSET " + offset;
		Long totalRowNum = null;
		Long totalPageNum = null;
		// 先计算总记录数
		StringBuilder countSql = new StringBuilder();
		countSql.append("SELECT COUNT(*) FROM (").append(selectSql).append(") AS count");
		totalRowNum = simpleJdbcTemplate.getCount(countSql.toString(), args);
		if (totalRowNum != null) {// 计算总页码数
			totalPageNum = (totalRowNum + pageSize - 1) / pageSize;
		}
		// 再查询结果
		List<T> dataList = simpleJdbcTemplate.query(selectSql + pageSql, args, rowMapper);
		Pager<T> pager = new Pager<T>();
		pager.setDataList(dataList);
		pager.setPageNum(pageNum);
		pager.setOffset(offset);
		pager.setPageSize(pageSize);
		pager.setTotalRow(totalRowNum);
		pager.setTotalPage(totalPageNum);
		return pager;
	}

	/**
	 * 根据条件，更新数据库某些记录 注意，若没有限制条件，为了安全起见，停止更新操作
	 * 
	 * @param po
	 *            携带着要更新的数据
	 * @param conditionPo
	 *            携带着限制条件的值
	 * @return 更新的行数
	 */
	public int update(Object po, Object conditionPo) {
		if (po == null || conditionPo == null)
			return 0;
		SqlAndArgs sqlAndArgs = sqlBuilder.getUpdateStatement(po, conditionPo, false);
		return simpleJdbcTemplate.update(sqlAndArgs.getPreparedSQL(), sqlAndArgs.getParamValues());
	}

	/**
	 * 根据条件，删除数据库某些记录
	 * 
	 * @param conditionPo
	 *            携带着限制条件的值
	 * @return 删除行数
	 */
	public int delete(Object conditionPo) {
		if (conditionPo == null)
			return 0;
		SqlAndArgs sqlAndArgs = sqlBuilder.getDeleteStatement(conditionPo);
		return simpleJdbcTemplate.update(sqlAndArgs.getPreparedSQL(), sqlAndArgs.getParamValues());
	}

	/**
	 * 批量执行sql
	 * 
	 * @param sqlAndBatchArgsMapper
	 *            一条sql对应多组参数
	 */
	public void batchUpdate(Map<String, List<Object[]>> sqlAndBatchArgsMapper) {
		if (sqlAndBatchArgsMapper == null)
			return;
		Iterator<Entry<String, List<Object[]>>> iterator = sqlAndBatchArgsMapper.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, List<Object[]>> entry = iterator.next();
			String preparedSQL = entry.getKey();
			List<Object[]> batchArgs = entry.getValue();
			simpleJdbcTemplate.batchUpdate(preparedSQL, batchArgs);
		}
	}

	/**
	 * 本方法可将源（远程）List数据同步到本地表中，使用时，需用对应本地数据表的po对象去接收远程数据 本方法会将数据同步到对应本地数据表中
	 * 
	 * 注意： （1）被比较的po对象需实现自己的equals()/hashCode()方法 （2）po对象必须是被@Orm注解的对象
	 * 
	 * @param srcList
	 *            作为比较基准的list，它持有的po对应着本地数据库表
	 * @param poClass
	 *            本地数据库表对应的po Class
	 * @param <T>
	 *            po的类型
	 */
	public <T> void syncData(List<T> srcList, Class<T> poClass) {
		if (srcList == null || poClass == null)
			throw new IllegalArgumentException("参数srcList或poClass为null");
		T po = null;
		SqlAndArgs sa = null;
		List<T> localList = null;
		String keyName = null;
		try {
			po = poClass.newInstance();
			sa = sqlBuilder.getAnalyzer().parse(po);
			// 首先检查是否有主键，没有则抛出异常程序终止
			String keyColumnLabel = sa.getKeyColumnLabel();
			if (keyColumnLabel == null || keyColumnLabel.length() == 0) {
				throw new IllegalArgumentException("没有主键信息，注意在po类中添加Orm的主键注解");
			}
			keyName = sa.getColumnPropertyMap().get(keyColumnLabel).getPropertyName();
			String markDeletedColumnLabel = sa.getMarkDeletedColumnLabel();
			// 考虑有标记删除的场景
			if (markDeletedColumnLabel != null && markDeletedColumnLabel.length() != 0) {
				String propertyName = sa.getColumnPropertyMap().get(markDeletedColumnLabel).getPropertyName();
				Field f = BeanUtil.getDeclaredField(po, propertyName);
				// 考虑到po标记删除位初始值未知，故显式地，分别地，查询已做标记删除和未做标记删除的记录
				BeanUtil.injectField(f, po, null);
				localList = query(po);
				BeanUtil.injectField(f, po, 1);
				localList.addAll(query(po));
			} else {
				localList = query(po);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("创建po实例异常，检查po对象是否有无参构造器");
		}
		// 将本地记录list转存到Map中，key是po的注解，值是po
		Map<Serializable, T> localMap = BeanUtil.saveListToMap(localList, keyName);
		Set<Serializable> existSet = new HashSet<Serializable>();// 使用一个Set来跟踪每一个远程记录的key
		List<SqlAndArgs> sal = new ArrayList<SqlAndArgs>();// 用于存储批量sql和参数
		try {
			Field keyField = poClass.getDeclaredField(keyName);
			keyField.setAccessible(true);
			T condition = poClass.newInstance();
			for (T srcPo : srcList) {
				Serializable key = Serializable.class.cast(keyField.get(srcPo));
				T localPo = localMap.get(key);
				if (localPo == null) {
					// 新增
					sa = sqlBuilder.getInsertStatement(srcPo, true);// 即便是null也写入insert语句中
					sal.add(sa);
				} else {
					// 比较操作
					if (!srcPo.equals(localPo)) {// 用户程序需实现po的equals()/hashCode()方法
						BeanUtil.injectField(keyField, condition, key);
						// 第三个参数是true表示即便是null也写入update语句中
						sa = sqlBuilder.getUpdateStatement(srcPo, condition, true);
						sal.add(sa);
					}
				}
				existSet.add(key);// 将远程数据统计进Set中
			}
			for (T localPo : localList) {
				Object key = keyField.get(localPo);
				if (!existSet.contains(key)) {// 说明远程数据源已经没有这条数据了
					// 删除工作
					BeanUtil.injectField(keyField, condition, key);
					sa = sqlBuilder.getDeleteStatement(condition);
					sal.add(sa);
				}
			}
			batchUpdate(sqlBuilder.getSqlAndBatchArgsMapper(sal));
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "同步有错误", e);
		}
		logger.info(new java.sql.Timestamp(new java.util.Date().getTime()) + "   数据同步完成");
	}

	/**
	 * 工具方法，配置文件路径获取一个DataSource
	 * 
	 * @param configFilePath
	 * @return
	 */
	public static DataSource getDataSourceByPropertyFile(String configFilePath) {
		DataSource dataSource = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(configFilePath);
			Properties props = new Properties();
			props.load(fis);
			String driverClassName = props.getProperty("driver");
			String url = props.getProperty("url");
			String username = props.getProperty("username");
			String password = props.getProperty("password");
			dataSource = getDataSource(driverClassName, url, username, password);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return dataSource;
	}

	/**
	 * 工具方法，通过jdbc驱动名，url、username以及password获取一个DataSource
	 * 
	 * @param driver
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	public static DataSource getDataSource(String driver, String url, String username, String password) {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return DataSourceImpl.newInstance(url, username, password);
	}
}
