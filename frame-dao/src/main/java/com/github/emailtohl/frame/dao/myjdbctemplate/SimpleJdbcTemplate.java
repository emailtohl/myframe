package com.github.emailtohl.frame.dao.myjdbctemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

/**
 *************************************************
 * 本类实现了类似Spring的JdbcTemplate的方法
 * 主要封装Connection、Statement、PreparedStatement、ResultSet模板代码以简化开发
 * 
 * @author helei
 * @version 1.5 由于Connection动态代理的解耦，本业务程序则无需关心是否在事务管理中，执行其close方法即可
 * 2015.11.03
 *************************************************
 */
public class SimpleJdbcTemplate {

	protected DataSource dataSource;

	public SimpleJdbcTemplate(DataSource dataSource) {
		super();
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * 查询操作，将每个元祖的数据存放在T的对象中，并以List形式返回所有的元祖
	 * @param prepareSQL 带参数？的sql语句
	 * @param args 参数数组
	 * @param rowMapper 行集映射接口
	 * @return List 封装的结果
	 */
	public <T> List<T> query(String prepareSQL, Object[] args, RowMapper<T> rowMapper) {
		if (prepareSQL == null || rowMapper == null)
			throw new IllegalArgumentException("prepareSQL或rowMapper是null");
		List<T> list = null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement(prepareSQL);
			if (args != null) {
				for (int i = 0; i < args.length; i++)
					preparedStatement.setObject(i + 1, args[i]);// 参数进入Object数组，肯定都是对象，直接使用setObject方法没问题
			}
			resultSet = preparedStatement.executeQuery();
			int rowNum = resultSet.getRow();
			list = new ArrayList<T>();
			while (resultSet.next()) {
				T obj = rowMapper.mapRow(resultSet, rowNum);
				list.add(obj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(resultSet, preparedStatement, null, connection);
		}
		return list;
	}
	
	/**
	 * 查询操作，将每个元祖的数据存放在Object数组中，并以List形式返回所有的元祖，本方法适合指定select项的查询，这种查询知道列的顺序，能快速获取结果
	 * @param prepareSQL 带参数？的sql语句
	 * @param args 参数数组
	 * @return List 封装的结果
	 */
	public List<Object[]> queryArray(String prepareSQL, Object[] args) {
		if (prepareSQL == null)
			throw new IllegalArgumentException("prepareSQL或rowMapper是null");
		List<Object[]> list = null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement(prepareSQL);
			if (args != null) {
				for (int i = 0; i < args.length; i++)
					preparedStatement.setObject(i + 1, args[i]);// 参数进入Object数组，肯定都是对象，直接使用setObject方法没问题
			}
			resultSet = preparedStatement.executeQuery();
			int length = resultSet.getMetaData().getColumnCount();
			list = new ArrayList<Object[]>();
			while (resultSet.next()) {
				Object[] objs = new Object[length];
				for (int i = 0; i < length; i++) {
					objs[i] = resultSet.getObject(i + 1);
				}
				list.add(objs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(resultSet, preparedStatement, null, connection);
		}
		return list;
	}

	/**
	 * 查询操作，将每个元祖的数据存放在Map中，key是列名，value是查询值
	 * @param prepareSQL 带参数？的sql语句
	 * @param args 参数数组
	 * @return List 封装的结果
	 */
	public List<Map<String, Object>> queryForList(String prepareSQL, Object[] args) {
		if (prepareSQL == null)
			throw new IllegalArgumentException("prepareSQL或rowMapper是null");
		List<Map<String, Object>> list = null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement(prepareSQL);
			if (args != null) {
				for (int i = 0; i < args.length; i++)
					preparedStatement.setObject(i + 1, args[i]);// 参数进入Object数组，肯定都是对象，直接使用setObject方法没问题
			}
			resultSet = preparedStatement.executeQuery();
			ResultSetMetaData meta = resultSet.getMetaData();
			int length = meta.getColumnCount();
			list = new ArrayList<Map<String, Object>>();
			while (resultSet.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 0; i < length; i++) {
					map.put(meta.getColumnLabel(i + 1), resultSet.getObject(i + 1));
				}
				list.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(resultSet, preparedStatement, null, connection);
		}
		return list;
	}
	
	/**
	 * 查询操作，将每个元祖的数据存放在T的对象中，并以List形式返回所有的元祖
	 * @param sql sql语句
	 * @param rowMapper 行集映射接口
	 * @return List 封装的结果
	 */
	public <T> List<T> query(String sql, RowMapper<T> rowMapper) {
		if (sql == null || rowMapper == null)
			throw new IllegalArgumentException("prepareSQL或rowMapper是null");
		List<T> list = null;
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			int rowNum = resultSet.getRow();
			list = new ArrayList<T>();
			while (resultSet.next()) {
				T obj = rowMapper.mapRow(resultSet, rowNum);
				list.add(obj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(resultSet, null, statement, connection);
		}
		return list;
	}

	/**
	 * 插入操作
	 * @param prepareSQL 带参数？的sql语句
	 * @param args 参数数组
	 * @param autoKeyName 数据库主键自动增长的列名
	 * @return 新插入数据在数据库中的主键值，如若获取不到，则返回-1
	 */
	public long insert(String prepareSQL, Object[] args, String autoKeyName) {
		if (prepareSQL == null)
			throw new IllegalArgumentException("prepareSQL是null");
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		long id = -1L;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement(prepareSQL, PreparedStatement.RETURN_GENERATED_KEYS);
			if (args != null) {
				for (int i = 0; i < args.length; i++)
					preparedStatement.setObject(i + 1, args[i]);
			}
			preparedStatement.executeUpdate();
			resultSet = preparedStatement.getGeneratedKeys();
			if (resultSet != null && autoKeyName != null && resultSet.next())
				id = resultSet.getLong(autoKeyName);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(resultSet, preparedStatement, null, connection);
		}
		return id;
	}

	/**
	 * 更新操作
	 * @param prepareSQL 带参数？的sql语句
	 * @param args 参数数组
	 * @return 更新的行数
	 */
	public int update(String prepareSQL, Object[] args) {
		if (prepareSQL == null)
			throw new IllegalArgumentException("prepareSQL是null");
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		int row = 0;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement(prepareSQL);
			if (args != null) {
				for (int i = 0; i < args.length; i++)
					preparedStatement.setObject(i + 1, args[i]);
			}
			row = preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(null, preparedStatement, null, connection);
		}
		return row;
	}

	/**
	 * 直接执行sql语句
	 * @param sql sql语句
	 * @return 影响的行数
	 */
	public int executeUpdate(String sql) {
		if (sql == null)
			throw new IllegalArgumentException("sql是null");
		Connection connection = null;
		Statement statement = null;
		int row = 0;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			row = statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(null, null, statement, connection);
		}
		return row;
	}

	/**
	 * 执行SELECT COUNT(*) FROM tableName 计算的出的行数
	 * @param countSql 计算行数的sql语句
	 * @return 计算出的行数
	 */
	public long getCount(String countSql) {
		if (countSql == null)
			throw new IllegalArgumentException("countSql或rowMapper是null");
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		long count = 0L;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(countSql);
			if (resultSet.next())
				count = resultSet.getLong(1);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(resultSet, null, statement, connection);
		}
		return count;
	}

	/**
	 * 执行SELECT COUNT(*) FROM tableName 计算的出的行数
	 * @param countSql 带参数？的sql语句
	 * @param args 参数数组
	 * @return 计算出的行数
	 */
	public long getCount(String countSql, Object[] args) {
		if (countSql == null)
			throw new IllegalArgumentException("countSql是null");
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		long count = 0L;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement(countSql);
			if (args != null) {
				for (int i = 0; i < args.length; i++)
					preparedStatement.setObject(i + 1, args[i]);
			}
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next())
				count = resultSet.getLong(1);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(resultSet, preparedStatement, null, connection);
		}
		return count;
	}

	/**
	 * 批量更新操作
	 * @param prepareSQL 带参数？的sql语句
	 * @param batchArgs 一条带参数？的sql语句对应着多种参数数组，此处用List容器存放多个参数数组
	 * @return int[] 每个参数数组配合prepareSQL执行后影响的行数
	 */
	public int[] batchUpdate(String prepareSQL, List<Object[]> batchArgs) {
		if (prepareSQL == null)
			throw new IllegalArgumentException("prepareSQL是null");
		int[] rows = null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		Statement statement = null;
		try {
			connection = dataSource.getConnection();
			boolean autoCommit = connection.getAutoCommit();// 保存原有设置
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			if (batchArgs == null) {
				statement.addBatch(prepareSQL);
			} else {
				for (Object[] args : batchArgs) {
					preparedStatement = connection.prepareStatement(prepareSQL);
					for (int i = 0; i < args.length; i++)
						preparedStatement.setObject(i + 1, args[i]);
					statement.addBatch(preparedStatement.toString());
					preparedStatement.close();
				}
			}
			rows = statement.executeBatch();
			connection.commit();
			connection.setAutoCommit(autoCommit);// 还原先前设置
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(null, preparedStatement, statement, connection);
		}
		return rows;
	}

	/**
	 * 批量执行各条sql语句
	 * @param sql 存放多条sql语句的数组
	 * @return int[] 每sql语句执行后影响的行数
	 */
	public int[] batchUpdate(String[] sql) {
		if (sql == null)
			throw new IllegalArgumentException("sql是null");
		int[] rows = null;
		Connection connection = null;
		Statement statement = null;
		try {
			connection = dataSource.getConnection();
			boolean autoCommit = connection.getAutoCommit();// 保存原有设置
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			for (int i = 0; i < sql.length; i++) {
				statement.addBatch(sql[i]);
			}
			rows = statement.executeBatch();
			connection.commit();
			connection.setAutoCommit(autoCommit);// 还原先前设置
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(null, null, statement, connection);
		}
		return rows;
	}

	/**
	 * 关闭资源的私有方法
	 * @param resultSet
	 * @param preparedStatement
	 * @param statement
	 * @param connection
	 */
	private void close(ResultSet resultSet, PreparedStatement preparedStatement, Statement statement,
			Connection connection) {
		try {
			if (resultSet != null && !resultSet.isClosed())
				resultSet.close();
			if (preparedStatement != null && !preparedStatement.isClosed())
				preparedStatement.close();
			if (statement != null && !statement.isClosed())
				statement.close();
			if (connection != null && !connection.isClosed())
				connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
