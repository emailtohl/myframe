package com.github.emailtohl.frame.transition;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.github.emailtohl.frame.dao.datasource.ConnectionStrategy;
import com.github.emailtohl.frame.dao.datasource.DataSourceImpl;

/**
 * 事务管理层 首先获取所有数据源的Connection，将这些Connection以Map形式绑定到ThreadLocal上，
 * 如此Dao层就可以在当前线程上拿到这些Connection
 * 为了不让底层Dao使用Connection.close()影响事务性，还需在这些Connection上设置一个标记：setThreadStrategy(
 * true) 如此在调用Connection.close()时，则并不做实质性的关闭操作
 * 待所有事务执行完成后，再调用setThreadStrategy(false)，然后再做统一关闭
 * 
 * @author helei
 * @version 2.0
 *          过去用策略模式实现了事务功能，不过由于该实现与业务程序耦合，且关系复杂。重构后，现在对Connection做动态代理实现了与业务程序结构
 *          2015.11.03
 */
public class TransitionHandler implements InvocationHandler {
	private static Logger logger = Logger.getLogger(TransitionHandler.class.getName());
	/*
	 * 静态值，便于一条线程上获取同一对象，在这里使业务逻辑层控制持久层的事务功能
	 * 理解它也很容易，它其实就是一个Map，key是线程，value即为绑定的对象
	 */
	public static final ThreadLocal<Map<String, Connection>> threadBindConnection = new ThreadLocal<Map<String, Connection>>();

	private Object target;

	public TransitionHandler(Object target) {
		this.target = target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) {
		logger.finest("进入动态代理，连接点方法名：" + method.getName());
		if (args != null)
			logger.finest("连接点参数：" + Arrays.toString(args));
		long startTime = System.currentTimeMillis();

		Object result = null;
		// 创建一个Connection的Map，分别对应每个数据库的Connection，key是url，value是Connection
		Map<String, Connection> connectionMap = new HashMap<String, Connection>();
		for (Entry<String, DataSource> entry : DataSourceImpl.dataSourcePool.entrySet()) {
			try {
				String url = entry.getKey();
				// 这里返回的connection是被动态代理的connection
				Connection connection = entry.getValue().getConnection();
				connection.setAutoCommit(false);
				// 设置为线程模式，如此调用此connection的close()方法时，会根据此属性决定是否真正关闭
				((ConnectionStrategy) connection).setThreadStrategy(true);
				connectionMap.put(url, connection);
			} catch (SQLException e) {
				e.printStackTrace();
				logger.log(Level.SEVERE, "获取Connection失败，或设置Connection事务属性失败", e);
				throw new RuntimeException("设置自动提交功能为false时出现SQLException");
			}
		}
		threadBindConnection.set(connectionMap);// 绑定到当前线程上
		try {
			result = method.invoke(target, args);// 进入Service层
			// 执行完后，若没出异常，则执行下面提交事务操作
			for (Connection connection : connectionMap.values()) {
				try {
					if (connection != null && !connection.isClosed())
						connection.commit();
				} catch (SQLException e) {
					logger.log(Level.SEVERE, "提交事务失败", e);
					throw e;// 提交时也有可能失败，故仍然做回滚处理
				}
			}
		} catch (Exception e) {// 在method.invoke中遇到任何异常均执行回滚操作
			e.printStackTrace();
			logger.log(Level.SEVERE, "执行事务失败", e);
			for (Connection connection : connectionMap.values()) {
				// 可能因其他异常进入处理块，先对Connection进行判断，再做处理
				try {
					if (connection != null && !connection.isClosed())
						connection.rollback();
				} catch (SQLException sqlException) {
					sqlException.printStackTrace();
					logger.log(Level.SEVERE, "事务回滚可能失败", sqlException);
				}
			}
		} finally {
			for (Connection connection : connectionMap.values()) {
				// 切换该线程策略，关闭此connection时，将真正关闭
				((ConnectionStrategy) connection).setThreadStrategy(false);
				try {
					if (!connection.isClosed() && !connection.isReadOnly())
						connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
					logger.log(Level.SEVERE, "关闭Connection异常", e);
				}
			}
			threadBindConnection.remove();
			long endTime = System.currentTimeMillis();
			int consumed = (int) ((double) (endTime - startTime));
			logger.info(method.getName() + " 执行用时: " + consumed + " 毫秒");
		}
		return result;
	}

}
