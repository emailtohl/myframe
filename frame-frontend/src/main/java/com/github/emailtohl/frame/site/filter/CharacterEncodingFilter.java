package com.github.emailtohl.frame.site.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;

/**
 * Servlet Filter implementation class CharacterEncodingFilter
 */
@WebFilter(urlPatterns = { "/*" }, initParams = { @WebInitParam(name = "characterEncoding", value = "UTF-8"),
		@WebInitParam(name = "enabled", value = "true") })
public class CharacterEncodingFilter implements Filter {
	private static final Logger logger = Logger.getLogger(CharacterEncodingFilter.class.getName());
	private String characterEncoding;
	private boolean enabled;

	/**
	 * Default constructor.
	 */
	public CharacterEncodingFilter() {
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		characterEncoding = fConfig.getInitParameter("characterEncoding");
		enabled = "true".equalsIgnoreCase(fConfig.getInitParameter("enabled").trim());
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (enabled && characterEncoding != null) {// 如果启动了该Filter
			request.setCharacterEncoding(characterEncoding);
			response.setCharacterEncoding(characterEncoding);

			HttpServletRequest req = (HttpServletRequest) request;
			String httpMethod = req.getMethod();
			if ("get".equalsIgnoreCase(httpMethod)) {
				Enumeration<String> parameterNames = request.getParameterNames();
				while (parameterNames.hasMoreElements()) {
					String parameterName = parameterNames.nextElement();
					String parameterValue = request.getParameter(parameterName);
					if (parameterValue != null && parameterValue.trim().length() != 0) {
						logger.finest("模拟过滤数据");
					}
				}
			}
		}
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		characterEncoding = null;// 销毁时清空资源
	}
}
