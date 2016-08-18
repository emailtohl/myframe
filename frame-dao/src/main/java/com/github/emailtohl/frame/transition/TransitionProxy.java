package com.github.emailtohl.frame.transition;

import java.lang.reflect.Proxy;
/**
 * 可在controller中获取支持事务的service的代理
 * 
 * @author helei
 *
 */
public final class TransitionProxy {
	private TransitionProxy() {
	}

	@SuppressWarnings("unchecked")
	public static <T> T getProxy(T service) {
		Class<? extends T> clz = (Class<? extends T>) service.getClass();
		return (T) Proxy.newProxyInstance(clz.getClassLoader(), clz.getInterfaces(), new TransitionHandler(service));
	}
}
