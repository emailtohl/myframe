package com.github.emailtohl.frame.site;

import javax.servlet.FilterRegistration;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import com.github.emailtohl.frame.dao.BaseDao;
import com.github.emailtohl.frame.ioc.Context;
import com.github.emailtohl.frame.mvc.DispatcherServlet;
import com.github.emailtohl.frame.site.filter.CompressionFilter;

/**
 * Application Lifecycle Listener implementation class Boot
 *
 */
@WebListener
public class Boot implements ServletContextListener {

	/**
	 * Default constructor.
	 */
	public Boot() {}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		// 创建应用层的容器
		Context ctx = new Context();
		servletContext.setAttribute("context", ctx);
		// 注册数据源到应用层容器中
		String configFilePath = Thread.currentThread().getContextClassLoader()
				.getResource("database.properties").getPath().substring(1);
		DataSource local = BaseDao.getDataSourceByPropertyFile(configFilePath);
		configFilePath = Thread.currentThread().getContextClassLoader()
				.getResource("remoteDatabase.properties").getPath().substring(1);
		DataSource remote = BaseDao.getDataSourceByPropertyFile(configFilePath);
		ctx.register("local", local);
		ctx.register("remote", remote);
		ctx.register("com.github.emailtohl.frame.site");
		
		servletContext.getServletRegistration("default").addMapping("/resource/*", "*.css", "*.js", "*.png", "*.gif", "*.jpg");
		DispatcherServlet dispatcherServlet = new DispatcherServlet("com.github.emailtohl.frame.site.controller", "/WEB-INF/jsp/");
		dispatcherServlet.setContext(ctx);
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcherServlet", dispatcherServlet);
		dispatcher.setLoadOnStartup(1);
		/* 可以上传文件 */
		dispatcher.setMultipartConfig(new MultipartConfigElement(null, 20_971_5200L, 41_943_0400L, 512_000));
		dispatcher.addMapping("/");
		
		FilterRegistration.Dynamic registration = servletContext.addFilter("compressionFilter", new CompressionFilter());
		registration.setAsyncSupported(true);
		registration.addMappingForUrlPatterns(null, false, "/*");
	}
	
	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {}

}
