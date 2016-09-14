package com.github.emailtohl.frame.mvc;

import static com.github.emailtohl.frame.mvc.RequestMethod.ANY;
import static com.github.emailtohl.frame.mvc.RequestMethod.DELETE;
import static com.github.emailtohl.frame.mvc.RequestMethod.GET;
import static com.github.emailtohl.frame.mvc.RequestMethod.POST;
import static com.github.emailtohl.frame.mvc.RequestMethod.PUT;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.github.emailtohl.frame.ioc.Context;
import com.github.emailtohl.frame.util.BeanTools;
import com.github.emailtohl.frame.util.Serializing;

/**
 *************************************************
 * 模仿SpringMVC的DispatcherServlet，为业务控制器提供调度分发功能，可转发JSP，也可直接将模型数据序列化到客户端
 * 
 * @author helei
 * @version 2.0 2015.11.05
 * 
 * @version 2.1 更新http方法 2016.7.2
 *************************************************
 */
/*
@WebServlet(description = "前端调度器，需要配置业务控制器和视图解析器的信息，所有项均需要配置，"
		+ "当用户程序返回结果是JspUrl包装的地址，则跳转到某JSP的相对路径，否则解析为json返回给客户端",
		loadOnStartup = 1, urlPatterns = { "/" }, initParams = {
		@WebInitParam(name = "controllerPack", value = "site.controller", description = "配置业务控制器所在的包"),
		@WebInitParam(name = "viewPrefix", value = "WEB-INF/jsp/", description = "配置视图资源的前缀") })
@MultipartConfig // 标识Servlet支持文件上传
*/
public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = -4212357389547896819L;
	private static Logger logger = Logger.getLogger(DispatcherServlet.class.getName());
	private Context context;
	private Serializing serializing = new Serializing();
	private String controllerPack;
	private Map<String, ClassAndMethodBean> actionHandleMap;
	private String viewPrefix;

	public DispatcherServlet() {
		super();
	}

	public DispatcherServlet(String controllerPack, String viewPrefix) {
		super();
		this.controllerPack = controllerPack;
		this.viewPrefix = viewPrefix;
	}
	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * init(ServletConfig config)是被容器调用的方法，执行顺序是在servlet被构造后
	 */
	public void init(ServletConfig config) throws ServletException {
		if (viewPrefix == null) {
			viewPrefix = config.getInitParameter("viewPrefix");
		}
		if (controllerPack == null) {
			controllerPack = config.getInitParameter("controllerPack");
		}
		try {
			actionHandleMap = new MvcParser().getActionHandleMap(controllerPack);
			if (context != null) {
				for (ClassAndMethodBean cb : actionHandleMap.values()) {
					String name = cb.getClazz().getName();
					Object instance = cb.getController();
					context.register(name, instance);
				}
			}
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ServletException("控制器不能实例化，检查是否有无参构造器");
		}
		Iterator<Entry<String, ClassAndMethodBean>> iterator = actionHandleMap.entrySet().iterator();
		logger.info("ActionController可处理以下请求：");
		while (iterator.hasNext()) {
			Map.Entry<String, ClassAndMethodBean> entry = iterator.next();
			String key = entry.getKey();
			RequestMethod httpMethod = entry.getValue().getHttpMethod();
			Class<?> clazz = entry.getValue().getClazz();
			Method method = entry.getValue().getMethod();
			logger.info("[" + key + "   " + httpMethod.name() + "]  处理类：" + clazz + "   处理方法：" + method);
		}
	}

	/**
	 * 实现请求分发，将请求委托给dispatch方法处理 若调用程序返回的不是字符，则将结果解析为json返回到客户端
	 * 
	 * @param request
	 * @param response
	 * @param httpMethod
	 * @throws ServletException
	 * @throws IOException
	 */
	private void dispatch(HttpServletRequest request, HttpServletResponse response, RequestMethod httpMethod)
			throws ServletException, IOException {
		Object result = null;
		String requestURI = request.getRequestURI();
		String requestAction = requestURI.substring(requestURI.indexOf("/", 1) + 1);// 取掉前面的项目名
		ClassAndMethodBean cam = actionHandleMap.get(requestAction);
		if (cam != null && (cam.getHttpMethod() == ANY || cam.getHttpMethod() == httpMethod)) {
			Object controller = cam.getController();
			Method handleMethod = cam.getMethod();
			Class<?>[] argsClass = handleMethod.getParameterTypes();
			Object[] args = new Object[argsClass.length];
			// 组装invoke方法时需要的参数
			for (int i = 0; i < argsClass.length; i++) {
				if (HttpServletRequest.class.isAssignableFrom(argsClass[i])) {
					args[i] = request;
				} else if (HttpServletResponse.class.isAssignableFrom(argsClass[i])) {
					args[i] = response;
				} else if (HttpSession.class.isAssignableFrom(argsClass[i])) {
					args[i] = request.getSession();
				} else {
					try {
						Object parameter = argsClass[i].newInstance();
						injectParamBean(request, parameter);
						args[i] = parameter;
					} catch (InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
						logger.warning("参数注入失败");
					}
				}
			}
			try {
				result = handleMethod.invoke(controller, args);
			} catch (Exception e) {// 捕获并处理一切异常
				logger.log(Level.SEVERE, "执行失败", e);
				if (!response.isCommitted()) {
					response.setStatus(500);
					response.sendError(500, "业务程序执行错误");
				}
			}
		}
		// 转到相关视图
		if (!response.isCommitted()) {
			if (result instanceof JspUrl) {
				String jspUrl = ((JspUrl) result).url;
				request.getRequestDispatcher(viewPrefix + jspUrl).forward(request, response);
			} else if (result != null) {
				response.setCharacterEncoding("UTF-8");
				String representation;
				String accept = request.getHeader("Accept");
				// 默认返回json
				if (accept != null && !accept.contains("application/json") && accept.contains("application/xml")) {
					response.setContentType("application/xml");
					representation = serializing.toXml(result, true);
				} else {
					response.setContentType("application/json");
					representation = serializing.toJson(result);
				}
				PrintWriter out = response.getWriter();
				out.print(representation);
				out.close();
			}
		}
	}

	/**
	 * 分析所有请求，然后注入到接收这些参数的bean中 注意：若参数是数组，则bean对象中接收它的属性必须是String[]
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param parameterBean
	 *            接收的Bean
	 */
	private void injectParamBean(HttpServletRequest request, Object parameterBean) {
		if (request == null || parameterBean == null)
			return;
		Map<String, String[]> paramMap = request.getParameterMap();
		Map<String, Field> beanMap = BeanTools.getFieldMap(parameterBean);
		for (Entry<String, String[]> paramEntry : paramMap.entrySet()) {
			String[] parameterValues = paramEntry.getValue();
			if (parameterValues == null)
				continue;
			String paramName = paramEntry.getKey();
			Field field = beanMap.get(paramName);
			if (field == null)
				continue;
			if (field.getType() == String[].class) {
				try {
					field.set(parameterBean, parameterValues);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
					logger.log(Level.SEVERE, "请求的参数注入失败", e);
				}
			} else if (parameterValues.length > 0) {
				BeanTools.injectFieldWithString(field, parameterBean, parameterValues[0]);
			}
		}
	}

	/**
	 * HttpServlet的service会分发到相应的处理方法中，也会做出正确的HTTP响应，事实上，service方法的实现非常复杂，
	 * 与web容器有关
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response) 从指定的URL中获取资源
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		dispatch(request, response, GET);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response) 通常用于处理Web表单提交，也处理二进制文件上传等
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		dispatch(request, response, POST);
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 *      存储URL中提供的实体
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		dispatch(request, response, PUT);
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 *      删除由URL标识的资源
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		dispatch(request, response, DELETE);
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 *      删除由URL标识的资源
	 */
	protected void doOptions(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setHeader("Allow", "OPTIONS,GET,POST,PUT,DELETE");
		response.setStatus(204);// NO_CONTENT
	}

	/**
	 * 业务控制器如果返回本类的对象，则跳转到相应JSP地址，否则解析为json返回给客户端
	 */
	public static class JspUrl {
		public final String url;

		public JspUrl(String url) {
			super();
			this.url = url;
		}
	}

	public String getControllerPack() {
		return controllerPack;
	}

	public void setControllerPack(String controllerPack) {
		this.controllerPack = controllerPack;
	}

	public String getViewPrefix() {
		return viewPrefix;
	}

	public void setViewPrefix(String viewPrefix) {
		this.viewPrefix = viewPrefix;
	}

}
