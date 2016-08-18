package com.github.emailtohl.frame.site.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class SqlSensitive
 */
@WebFilter(dispatcherTypes = { DispatcherType.REQUEST }, description = "简单过滤sql注入使用的敏感字符", urlPatterns = { "/*" })
public class SqlSensitive implements Filter {
	private Pattern sqlPattern = Pattern.compile(
			"(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|"
					+ "(\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)",
			Pattern.CASE_INSENSITIVE);
	private Pattern emailPattern = Pattern.compile("^[a-z0-9`!#$%^&*'{}?/+=|_~-]+(\\.[a-z0-9`!#$%^&*'{}?/+="
			+ "|_~-]+)*@([a-z0-9]([a-z0-9-]*[a-z0-9])?)+(\\.[a-z0-9]" + "([a-z0-9-]*[a-z0-9])?)*$");

	/**
	 * Default constructor.
	 */
	public SqlSensitive() {
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		boolean flag = true;
		Enumeration<String> parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String parameterName = parameterNames.nextElement();
			String parameterValue = request.getParameter(parameterName);
			if (parameterValue != null && parameterValue.length() != 0) {
				flag = isValid(parameterValue);
				
				if ("email".equals(parameterName)) {
					flag = emailPattern.matcher(parameterValue).matches();
				}
				if (!flag)
					break;
			}
		}
		if (!flag) {
			HttpServletResponse hsr = (HttpServletResponse) response;
			hsr.setCharacterEncoding("UTF-8");
			hsr.sendError(HttpServletResponse.SC_BAD_REQUEST, "请求中的参数非法");
		} else
			chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
	}

	public boolean isValid(String str) {
		if (sqlPattern.matcher(str).find()) {
			return false;
		}
		return true;
	}
}
