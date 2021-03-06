package com.github.emailtohl.frame.site.controller;

import static com.github.emailtohl.frame.mvc.RequestMethod.DELETE;
import static com.github.emailtohl.frame.mvc.RequestMethod.GET;
import static com.github.emailtohl.frame.mvc.RequestMethod.POST;
import static com.github.emailtohl.frame.mvc.RequestMethod.PUT;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.emailtohl.frame.dao.Page;
import com.github.emailtohl.frame.mvc.DispatcherServlet.JspUrl;
import com.github.emailtohl.frame.mvc.Mvc;
import com.github.emailtohl.frame.site.dto.SupplierDto;
import com.github.emailtohl.frame.site.service.SupplierService;
import com.github.emailtohl.frame.util.ServletUtil;

@Mvc(action = "supplier")
public class SupplierController {
	@Inject
	private SupplierService supplierService;

	/*public SupplierController() {
		super();
		supplierService = TransitionProxy.getProxy(new SupplierServiceImpl());
	}*/

	@Mvc(action = "page", method = GET)
	public JspUrl get(HttpServletRequest request, SupplierDto supplierDto) {
		Page<SupplierDto> supplierPage = supplierService.queryPage(supplierDto);
		request.setAttribute("supplierPage", supplierPage);
		request.setAttribute("queryParam", supplierDto);
		request.getSession().setAttribute("supplierList", supplierPage.getDataList());
		return new JspUrl("supplier/supplier.jsp");
	}
	
	@Mvc(action = "add", method = POST)
	public void add(HttpServletRequest request, HttpServletResponse response, SupplierDto supplierDto) {
		long id = supplierService.addSupplier(supplierDto);
		if (id > 0) {
			response.setStatus(201);// Created
			response.setHeader("Location", "page?supplierId=" + id);
		}
	}
	
	@Mvc(action = "update", method = PUT)
	public void update(HttpServletRequest request) {
		SupplierDto supplierDto = ServletUtil.parseForm(request, SupplierDto.class);
		supplierService.updateSupplier(supplierDto);
	}

	@Mvc(action = "delete", method = DELETE)
	public void delete(HttpServletRequest request, SupplierDto supplierDto) {
		supplierService.deleteSupplier(supplierDto);
	}
	
}
