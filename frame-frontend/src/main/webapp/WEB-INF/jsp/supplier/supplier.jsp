<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" errorPage="../common/error.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div id="supplier-page" class="page">

	<div class="box-header with-border">
		<h3 class="box-title">组合查询供应商</h3>
	</div>
	<form id="supplier-query" class="form-inline">
		<span class="form-group">
		<label>供应商名</label>
		<input class="form-control" type="text" name="supplierName" value="${queryParam.supplierName}">
		</span>
		<span class="form-group">
		<label>地址</label>
		<input class="form-control" type="text" name="address" value="${queryParam.address}">
		</span>
		<span class="form-group">
		<label>描述</label>
		<input class="form-control" type="text" name="description" value="${queryParam.description}">
		</span>
		<span class="form-group">
		<label>等级</label>
		<input class="form-control" type="text" name="rank" value="${queryParam.rank}">
		</span>
		<input type="submit" class="btn btn-default" value="查询">
		
		<div style="margin-top: 30px">
		<c:import url="../common/page.jsp">
			<c:param name="pageNum" value="${supplierPage.pageNum}" />
			<c:param name="totalPage" value="${supplierPage.totalPage}" />
		</c:import>
		</div>
		
	</form>
	
	
	<h3>供应商列表</h3>
	<button type="button" id="add-supplier-button" class="btn btn-default">新增供应商</button>
	<table class="table table-striped table-hover">
		<c:choose>
			<c:when test="${empty requestScope.supplierPage.dataList}">
				<div class="errormsg">
					<c:out value="${requestScope.IllegalInput}" default="未查找到结果"/>
				</div>
			</c:when>
			
			<c:otherwise>
				<thead>
				<tr>
					<th></th>
					<th>供应商名</th>
					<th>地址</th>
					<th>电话</th>
					<th>描述</th>
					<th>电邮</th>
					<th>等级</th>
					<th>操作</th>
				</tr>
				</thead>
				<tbody class="form-inline">
				<c:forEach items="${requestScope.supplierPage.dataList}" var="ls">
					<tr>
						<td><input class="edit form-control" type="hidden" readonly="readonly" name="supplierId" value="${ls.supplierId}"></td>
						<td><input class="edit form-control" type="text" name="supplierName" value="${ls.supplierName}"></td>
						<td><input class="edit form-control" type="text" name="address" value="${ls.address}"></td>
						<td><input class="edit form-control" type="text" name="tel" value="${ls.tel}"></td>
						<td><input class="edit form-control" type="text" name="description" value="${ls.description}"></td>
						<td><input class="edit form-control" type="email" name="email" value="${ls.email}"></td>
						<td><input class="edit form-control" type="text" name="rank" value="${ls.rank}"></td>
						<td><button type="button" class="save-supplier-button btn btn-success btn-xs margin">保存</button></td>
						<c:if test="${sessionScope.user.roleId eq 1}">
							<td><button type="button" class="delete-supplier-button btn btn-danger btn-xs margin">删除</button></td>
						</c:if>
					</tr>
				</c:forEach>
				</tbody>
			</c:otherwise>
		</c:choose>
	</table>

</div>


