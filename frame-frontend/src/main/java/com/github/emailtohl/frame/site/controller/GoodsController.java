package com.github.emailtohl.frame.site.controller;

import static com.github.emailtohl.frame.mvc.RequestMethod.DELETE;
import static com.github.emailtohl.frame.mvc.RequestMethod.GET;
import static com.github.emailtohl.frame.mvc.RequestMethod.POST;
import static com.github.emailtohl.frame.mvc.RequestMethod.PUT;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.emailtohl.frame.dao.Pager;
import com.github.emailtohl.frame.mvc.DispatcherServlet.JspUrl;
import com.github.emailtohl.frame.mvc.Mvc;
import com.github.emailtohl.frame.site.dto.GoodsDto;
import com.github.emailtohl.frame.site.dto.SupplierDto;
import com.github.emailtohl.frame.site.service.GoodsService;
import com.github.emailtohl.frame.site.service.SupplierService;
import com.github.emailtohl.frame.site.service.impl.GoodsServiceImpl;
import com.github.emailtohl.frame.site.service.impl.SupplierServiceImpl;
import com.github.emailtohl.frame.transition.TransitionProxy;
import com.github.emailtohl.frame.util.ServletUtils;

@Mvc
public class GoodsController {
	private static Logger logger = Logger.getLogger(GoodsController.class.getName());
	private GoodsService goodsService;// 使用代理可以让业务逻辑层实现对底层dao的事务管理
	private SupplierService supplierService;

	public GoodsController() {
		super();
		goodsService = TransitionProxy.getProxy(new GoodsServiceImpl());
		supplierService = TransitionProxy.getProxy(new SupplierServiceImpl());
	}

	/**
	 * 根目录进来，首先调用本方法返回一个main.jsp页面，该页面中加载js和css，然后由前端调用各种子页面的加载
	 * 
	 * @return 主页面
	 */
	@Mvc(action = "")
	public JspUrl mainPage() {
		return new JspUrl("common/main.jsp");// 直接返回JspUrl对象可内部跳转
	}
	
	/**
	 * 查询商品的列表页面
	 * 
	 * @param goodsDto 查询条件
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	@Mvc(action = "goods/page", method = GET)
	public void get(GoodsDto goodsDto, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.finer(ServletUtils.getFirstParamsMap(request).toString());
		Pager<GoodsDto> goodsPager = goodsService.queryPage(goodsDto);
		List<SupplierDto> supplierList = supplierService.querySupplier(new SupplierDto());
		request.setAttribute("goodsPager", goodsPager);
		request.setAttribute("queryParam", goodsDto);
		request.setAttribute("supplierList", supplierList);
		request.getRequestDispatcher("/WEB-INF/jsp/goods/goods.jsp").forward(request, response);
	}
	
	/**
	 * 只获取数据模型
	 * 
	 * @param goodsDto
	 * @return
	 */
	@Mvc(action = "goods/model", method = GET)
	public List<GoodsDto> model(GoodsDto goodsDto) {
		return goodsService.queryGoods(goodsDto);
	}

	/**
	 * 增加一个商品
	 * 
	 * @param goodsDto
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@Mvc(action = "goods/add", method = POST)
	public void add(GoodsDto goodsDto, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		long id = goodsService.addGoods(goodsDto);
		response.setStatus(201);// Created
		response.setHeader("Location", "goods/model?goodsId=" + id);
	}

	/**
	 * 编辑商品
	 * 
	 * @param goodsDto
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@Mvc(action = "goods/update", method = PUT)
	public void update(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		// Servlet不能直接获取参数，不管是Content-Disposition还是application/x-www-form-urlencoded
		// 所以只能手写获取代码
		GoodsDto goodsDto = ServletUtils.parseForm(request, GoodsDto.class);
		goodsService.updateGoods(goodsDto);
		response.setStatus(204);// No Content
	}

	/**
	 * 删除商品
	 * 
	 * @param goodsDto
	 */
	@Mvc(action = "goods/delete", method = DELETE)
	public void delete(GoodsDto goodsDto)
			throws IOException {
		Long deleteId = goodsDto.getGoodsId();
		if (deleteId == null)
			return;
		goodsService.deleteGoods(goodsDto);
	}

}
