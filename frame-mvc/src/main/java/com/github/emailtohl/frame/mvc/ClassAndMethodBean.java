package com.github.emailtohl.frame.mvc;

import java.lang.reflect.Method;

/**
 * 此Bean在注解中使用
 * @author helei
 *
 */
public class ClassAndMethodBean {
	private Class<?> clazz;
	private Object controller;
	private Method method;
	private RequestMethod HttpMethod;
	public ClassAndMethodBean() {
		super();
	}
	public Class<?> getClazz() {
		return clazz;
	}
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
	public Object getController() {
		return controller;
	}
	public void setController(Object controller) {
		this.controller = controller;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public RequestMethod getHttpMethod() {
		return HttpMethod;
	}
	public void setHttpMethod(RequestMethod httpMethod) {
		HttpMethod = httpMethod;
	}
}
