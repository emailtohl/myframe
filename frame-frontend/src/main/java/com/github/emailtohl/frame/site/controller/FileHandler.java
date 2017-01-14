package com.github.emailtohl.frame.site.controller;

import static com.github.emailtohl.frame.mvc.RequestMethod.GET;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.emailtohl.frame.mvc.Mvc;
import com.github.emailtohl.frame.mvc.RequestMethod;
import com.github.emailtohl.frame.mvc.DispatcherServlet.JspUrl;
import com.github.emailtohl.frame.util.ServletUtil;

@Mvc(action = "file/")
public class FileHandler {
	private static final Logger logger = Logger.getLogger(FileHandler.class.getName());
	private static final String uploadPath = "/resource/upload/";
	private static final String downloadPath = "/resource/download/";

	@Mvc(action = "page", method = GET)
	public JspUrl page(HttpServletRequest request, HttpServletResponse response) {
		return new JspUrl("upload/uploadPage.jsp");// 直接返回JspUrl对象可内部跳转
	}
	
	@Mvc(action = "upload")
	public void upload(HttpServletRequest request, HttpServletResponse response) {
		ServletUtil.upload(request, response, uploadPath, null);
	}

	@Mvc(action = "download", method = RequestMethod.GET)
	public void download(HttpServletRequest request, HttpServletResponse response) {
		ServletUtil.download(request, response, downloadPath);
	}

	@SuppressWarnings("unused")
	@Mvc(action = "multiUpload", method = RequestMethod.POST)
	public void multiUpload(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		Map<String, String[]> paramMap = request.getParameterMap();
		for (Entry<String, String[]> entry : paramMap.<String, String[]> entrySet()) {
			String name = entry.getKey();
			String[] values = entry.getValue();
			String str = "name: " + name + " value: " + values[0];
			logger.info(str);
		}
		String result = ServletUtil.multipartOnload(request, uploadPath);
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.println(result);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null)
				out.close();
		}
	}
}
