package com.github.emailtohl.frame.dao.datasource;

import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.github.emailtohl.frame.transition.TransitionHandler;

/**
 * 本类支持事务管理功能，实现了DataSource接口
 * 首先本类查询事务管理器的ThreadLocal中是否有DataSource，若有，则表示此时正处于事务管理中，
 * 返回ThreadLocal中的DataSource 若无，则返回一个被代理的Connection，该代理会根据isThreadStrategy状态决定行为
 * 例如，在事务管理过程中，调用Connection的close方法时，代理会根据isThreadStrategy为true而不真正执行close，
 * 直到事务管理器来执行
 * 
 * @author helei 2015.11.03
 */
public final class DataSourceImpl implements DataSource {
	public static final Map<String, DataSource> dataSourcePool = new HashMap<String, DataSource>();
	private String url, username, password;

	private DataSourceImpl(String url, String username, String password) {
		super();
		this.url = url;
		this.username = username;
		this.password = password;
	}

	/**
	 * 一个数据源对应一个数据库，创建一个数据源就将其添加进连接池中
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	public static synchronized DataSource newInstance(String url, String username, String password) {
		DataSource ds = dataSourcePool.get(url);
		if (ds == null) {
			ds = new DataSourceImpl(url, username, password);
			dataSourcePool.put(url, ds);
		}
		return ds;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		Map<String, Connection> map = TransitionHandler.threadBindConnection.get();
		if (map != null) {
			conn = map.get(url);
		}
		// 如果在ThreadLocal中没有拿到Connection，则直接从DriverManager中获取
		return conn == null ? getConnectionWrap(this.url, this.username, this.password) : conn;
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		Connection conn = null;
		Map<String, Connection> map = TransitionHandler.threadBindConnection.get();
		if (map != null) {
			conn = map.get(url);
		}
		// 如果在ThreadLocal中没有拿到Connection，则直接从DriverManager中获取
		return conn == null ? getConnectionWrap(this.url, username, password) : conn;
	}

	private Connection getConnectionWrap(String url, String username, String password) throws SQLException {
		Connection conn = DriverManager.getConnection(url, username, password);
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		return (Connection) Proxy.newProxyInstance(cl, new Class<?>[] { Connection.class, ConnectionStrategy.class },
				new ConnectionHandler(conn));
	}
}
