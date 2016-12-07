package com.github.emailtohl.frame.site.controller;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.emailtohl.frame.site.dao.po.User;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = -7663722950556702442L;
	private Map<String, User> users = new ConcurrentHashMap<String, User>();
	
	/**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        User foo = new User(), bar = new User(), emailtohl = new User();
        foo.setId(1L);
        foo.setName("foo");
        foo.setEmail("foo@test.com");
        foo.setPassword("123456");
        foo.setRoleId(1);
        foo.setIcon("resource/images/icon-head-foo.jpg");
        
        bar.setId(2L);
        bar.setName("bar");
        bar.setEmail("bar@test.com");
        bar.setPassword("123456");
        bar.setRoleId(2);
        bar.setIcon("resource/images/icon-head-bar.jpg");
        
        emailtohl.setId(3L);
        emailtohl.setName("emailtohl");
        emailtohl.setEmail("emailtohl@163.com");
        emailtohl.setPassword("123456");
        emailtohl.setRoleId(1);
        emailtohl.setIcon("resource/images/20160204104250.png");
        
        users.put("foo@test.com", foo);
        users.put("bar@test.com", bar);
        users.put("emailtohl@163.com", emailtohl);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if (action != null && "logout".equals(action)) {// 如果是注销进入此方法
			request.getSession().invalidate();
			response.sendRedirect("login");
		} else {// 如果是跳转进来
			request.getRequestDispatcher("WEB-INF/jsp/common/login.jsp").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		User localUser = users.get(email);
		if (localUser != null && password.equals(localUser.getPassword())) {
			request.getSession().setAttribute("user", localUser);
			response.sendRedirect(request.getContextPath());
		} else {
			User reqUser = new User();
			reqUser.setEmail(email);
			reqUser.setPassword(password);
			request.setAttribute("reqUser", reqUser);
			request.setAttribute("failure", true);
			request.getRequestDispatcher("WEB-INF/jsp/common/login.jsp").forward(request, response);
		}
	}

}
