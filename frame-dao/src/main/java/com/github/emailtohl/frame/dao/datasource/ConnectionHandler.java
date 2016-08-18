package com.github.emailtohl.frame.dao.datasource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * 代理Connection和ConnectionStrategy接口
 * 其中，如果从线程模式中获取的Connection，则说明接受事务统一管理，当调用close方法时，则不做任何处理
 * 而ConnectionStrategy接口则告诉事务管理器和Dao，本Connection是从何处获取
 * 
 * @author helei
 * 2015.11.03
 */
public class ConnectionHandler implements InvocationHandler {
	/*
	 * 描述java.sql.Connection类中的“void close() throws SQLException”的Method
	 * 将其缓存起来，用于在切面(connectionHandler)中的比较
	 * 由于内部类不能再创建静态字段，故在外部类中声明，并初始化
	 */
	private static Method closeMethod, isThreadStrategyMethod, setThreadStrategyMethod;
	static {
		try {
			closeMethod = Connection.class.getMethod("close", new Class<?>[] {});
			isThreadStrategyMethod = ConnectionStrategy.class.getMethod("isThreadStrategy", new Class<?>[] {});
			setThreadStrategyMethod = ConnectionStrategy.class.getMethod("setThreadStrategy", new Class<?>[] {boolean.class});
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	private boolean isThreadStrategy;
	private Connection conn;
	public ConnectionHandler(Connection conn) {
		this.conn = conn;
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// 如果是在线程中拿到的Connection，则关闭方法不做任何处理，以交给事务管理器处理
		Object result = null;
		if (closeMethod.equals(method) && isThreadStrategy) {
		} else if (setThreadStrategyMethod.equals(method)) {
			isThreadStrategy = (boolean) args[0];
		} else if (isThreadStrategyMethod.equals(method)) {
			result = isThreadStrategy;
		} else {
			result = method.invoke(conn, args);
		}
		return result;
	}
}
