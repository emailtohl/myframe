package com.github.emailtohl.frame.site.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.github.emailtohl.frame.site.dao.po.User;

/**
 * Servlet Filter implementation class AuthenticationFilter
 */
@WebFilter({ "/", "/goods/*", "/supplier/*", "/file/*" })
public class AuthenticationFilter implements Filter {
	// key是角色ID，value是可访问的地址集合
	private Map<Integer, Set<String>> roleAccessMap = new ConcurrentHashMap<Integer, Set<String>>();
	public static final ThreadLocal<User> CURRENT_USER = new ThreadLocal<User>();
    /**
     * Default constructor. 
     */
    public AuthenticationFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		HttpSession session = req.getSession(false);
		if (session == null) {
			resp.sendRedirect("login");
		} else {
			User user = (User) session.getAttribute("user");
			if (user == null) {
				resp.sendRedirect("login");
			} else {
				if (checkAuthorization((HttpServletRequest) request, user)) {
					CURRENT_USER.set(user);
					chain.doFilter(request, response);
					CURRENT_USER.remove();
				} else {
					resp.sendError(403, "Forbidden" );
				}
			}
		}
	}
	
	private boolean checkAuthorization(HttpServletRequest request, User user) {
		boolean result = false;
		String access = request.getServletPath();
		if (access != null) {
			Integer roleId = user.getRoleId();
			Set<String> accessSet = roleAccessMap.get(roleId);
			result = accessSet.contains(access.substring(1));
		}
		return result;
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		Set<String> accesses2 = new HashSet<String>();
		accesses2.add("");
		accesses2.add("goods/page");
		accesses2.add("goods/model");
		accesses2.add("goods/add");
		accesses2.add("goods/update");
		accesses2.add("supplier/page");
		accesses2.add("supplier/add");
		accesses2.add("supplier/update");
		accesses2.add("file/download");
		roleAccessMap.put(2, accesses2);
		
		Set<String> accesses1 = new HashSet<String>(accesses2);
		// accesses1不仅含有accesses2所有值，还包括以下
		accesses1.add("goods/delete");
		accesses1.add("supplier/delete");
		accesses1.add("file/multiUpload");
		accesses1.add("file/page");
		accesses1.add("file/upload");
		roleAccessMap.put(1, accesses1);
	}

}
