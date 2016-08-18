<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="paging-navigation" style="margin-top: 10px">
	<c:if test="${param.pageNum > 1}">
		<a href = "#" class="page-num" data-page="1">首页</a>
	</c:if>
	<c:if test="${param.pageNum > 1}">
		<a href = "#" class="page-num" data-page="${param.pageNum - 1}" >上一页</a>
	</c:if>
	
	<%
		// ${param.name} 等价于 request.getParamter("name")
		Long pageNum = Long.parseLong(request.getParameter("pageNum"));
		Long totalPage = Long.parseLong(request.getParameter("totalPage"));
		for (int i = 1; i <= totalPage; i++) {
			out.println("<a href = \"#\" class=\"page-num\" data-page=\"" + i + "\" >" + i + "</a>");
		}
	%>
	
	<c:if test="${param.pageNum < param.totalPage}">
		<a href = "#" class="page-num" data-page="${param.pageNum + 1}" >下一页</a>
	</c:if>
	<c:if test="${param.pageNum < param.totalPage}">
		<a href = "#" class="page-num" data-page="${param.totalPage}" >尾页</a>
	</c:if>
	导航	<input type="text" name="pageNum" style="width : 22px" value="${param.pageNum}"> 页
	<div>
		当前页 <c:out value="${param.pageNum}" default="1"/>
		总页数 <c:out value="${param.totalPage}" default="1"/>
	</div>
</div>