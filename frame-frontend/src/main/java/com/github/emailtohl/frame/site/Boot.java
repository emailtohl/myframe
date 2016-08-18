package com.github.emailtohl.frame.site;

import javax.servlet.FilterRegistration;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;

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
		
		servletContext.getServletRegistration("default").addMapping("/resource/*", "*.css", "*.js", "*.png", "*.gif", "*.jpg");
		DispatcherServlet dispatcherServlet = new DispatcherServlet("com.github.emailtohl.frame.site.controller", "/WEB-INF/jsp/");
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
