<%@ page language="java" contentType="text/html;charset=UTF-8"
	pageEncoding="UTF-8" errorPage="../common/error.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div id="goods-page" class="page">
	<div id="goods-list-page" class="sub-page">
		<div class="box box-primary">
			<div class="box-header with-border">
				<h3 class="box-title">组合查询商品</h3>
			</div>
			<form class="box box-primary" role="form" id="goods-query" method="GET" action="goods/list.do" class="form-inline">
				<div class="col-xs-4">
					<div class="form-group">
						<label>商品名</label>
						<input class="form-control" type="text" name="goodsName" placeholder="商品名" class="form-control" value="${queryParam.goodsName}">
					</div>
					<div class="form-group">
						<label>商品价格</label>
						<input class="form-control" type="text" name="price" placeholder="价格,必须有小数点" class="form-control"  value="${queryParam.price}">
					</div>
				</div>
				<div class="col-xs-4">
					<div class="form-group">
						<label>数量</label>
						<input class="form-control" type="number" name="amount" placeholder="数量" class="form-control"  value="${queryParam.amount}">
					</div>
					<div class="form-group">
						<label>供应商</label>
						<input class="form-control" type="text" name="supplierName" placeholder="供应商" class="form-control"  value="${queryParam.supplierName}">
					</div>
				</div>
				<div class="col-xs-4">
					<div class="form-group">
						<label>起始时间</label>
						<input class="form-control" name="startDate" type="date" class="" value="${queryParam.startDate}">
					</div>
					<div class="form-group">
						<label>结束时间</label>
						<input class="form-control" name="endDate" type="date" class="" value="${queryParam.endDate}">
					</div>
				</div>
				
				<div class="col-xs-6">
					<c:import url="../common/page.jsp">
						<c:param name="pageNum" value="${goodsPager.pageNum}" />
						<c:param name="totalPage" value="${goodsPager.totalPage}" />
					</c:import>
				</div>
				<div class="col-xs-6">
					<nav class="page-item pull-right" data-pagenum="${goodsPager.pageNum}" data-totalpage="${goodsPager.totalPage}"></nav>
				</div>
				
				<div class="box-footer">
					<div class="col-xs-6">
				   		<span class="tip">输入有误</span>
					</div>
					<div class="col-xs-6">
				   		<input type="submit" value="查询" class="btn btn-default pull-right">
					</div>
				</div>
		   	</form>
		</div>
		<div class="panel" id="goods-list">
			<h3>显示所有商品:</h3>
			<br>
			<table class="table table-striped table-hover">
				<c:choose>
					<c:when test="${empty requestScope.goodsPager.dataList}">
						<div class="errormsg">
							<c:out value="${requestScope.IllegalInput}" default="未查找到结果" />
						</div>
					</c:when>
			
					<c:otherwise>
						<thead>
							<tr>
								<th>商品名</th>
								<th>价格</th>
								<th>描述</th>
								<th>数量</th>
								<th>类型</th>
								<th>供应商</th>
								<th>创建时间</th>
								<th>操作</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${requestScope.goodsPager.dataList}" var="item">
								<tr>
									<td class="">${item.goodsName}</td>
									<td class="">${item.price}</td>
									<td class="">${item.description}</td>
									<td class="">${item.amount}</td>
									<td class="">${item.type}</td>
									<td data-supplierid="${item.supplierId}" class="">${item.supplierName}</td>
									<td><fmt:formatDate value="${item.createTime}" pattern="yyyy年MM月dd日 E"/></td>
									<td>
										<input type="button" value="编辑" class="edit btn btn-default" data-id="${item.goodsId}">
									</td>
									<c:if test="${sessionScope.user.roleId eq 1}">
										<td>
											<input type="button" value="删除" class="delete btn btn-default" data-id="${item.goodsId}">
										</td>
									</c:if>
								</tr>
							</c:forEach>
						</tbody>
					</c:otherwise>
			
				</c:choose>
			</table>
		</div>
		<br/>
		<input type="button" id="open-add-page-button" value="添加商品" class="btn btn-default"></input>
		
	</div>
	
	<div id="goods-add-page" class="sub-page">
		<div class="box box-primary">
			<div class="box-header with-border">
				<h3 class="box-title">添加商品</h3>
			</div>
			<form id="goods-add" action="goods/add" method="POST" class="box box-primary" role="form">
				<div class="col-xs-4">
					<div class="form-group">
						<label>商品名</label>
						<input class="form-control" type="text" name="goodsName" placeholder="商品名">
					</div>
				</div>
				<div class="col-xs-4">
					<div class="form-group">
						<label>商品价格</label>
						<input class="form-control" type="text" name="price" placeholder="价格,必须有小数点">
					</div>
				</div>
				<div class="col-xs-4">
					<div class="form-group">
						<label>数量</label>
						<input class="form-control" type="number" name="amount" placeholder="数量">
					</div>
				</div>
				<div class="col-xs-6">
					<div class="form-group">
						<label>供应商</label>
						<select name="supplierId" class="form-control">
							<c:forEach items="${requestScope.supplierList}" var="ls">
								<option value="${ls.supplierId}">${ls.supplierName}</option>
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="col-xs-6">
					<div class="form-group">
						<label>类型</label>
						<div>
							<label class="checkbox-inline">
							  <input name="types" type="checkbox" value="食品"> 食品
							</label>
							<label class="checkbox-inline">
							  <input name="types" type="checkbox" value="用品"> 用品
							</label>
							<label class="checkbox-inline">
							  <input name="types" type="checkbox" value="其他"> 其他
							</label>
						</div>
					</div>
				</div>
				<div class="col-xs-12">
					<div class="form-group">
						<textarea row="3" class="form-control" name="description"></textarea>
					</div>
				</div>
				<div class="box-footer">
					<div class="col-xs-6">
						<label>校验提示</label>
				   		<div id="goods-add-page_cue" class="validate-font-color"></div>
					</div>
					<div class="col-xs-6">
						<button type="button" class="back btn btn-default">返回</button>
				   		<button class="btn btn-default pull-right" type="submit">提交</button>
					</div>
				</div>
				
			</form>
		</div>
	</div>
	
	<div id="goods-edit-page" class="sub-page">
		<div class="box box-primary">
			<div class="box-header with-border">
				<h3 class="box-title">编辑商品</h3>
			</div>
			<form id="goods-edit" action="goods/update" method="POST" class="box box-primary" role="form">
				<div class="col-xs-4">
					<div class="form-group">
						<label>商品名</label>
						<input class="form-control" type="text" name="goodsName" placeholder="商品名">
					</div>
				</div>
				<div class="col-xs-4">
					<div class="form-group">
						<label>商品价格</label>
						<input class="form-control" type="text" name="price" placeholder="价格,必须有小数点">
					</div>
				</div>
				<div class="col-xs-4">
					<div class="form-group">
						<label>数量</label>
						<input class="form-control" type="number" name="amount" placeholder="数量">
					</div>
				</div>
				<div class="col-xs-6">
					<div class="form-group">
						<label>供应商</label>
						<select name="supplierId" class="form-control">
							<c:forEach items="${requestScope.supplierList}" var="ls">
								<option value="${ls.supplierId}">${ls.supplierName}</option>
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="col-xs-6">
					<div class="form-group">
						<label>类型</label>
						<div>
							<label class="checkbox-inline">
							  <input name="types" type="checkbox" value="食品"> 食品
							</label>
							<label class="checkbox-inline">
							  <input name="types" type="checkbox" value="用品"> 用品
							</label>
							<label class="checkbox-inline">
							  <input name="types" type="checkbox" value="其他"> 其他
							</label>
						</div>
					</div>
				</div>
				<div class="col-xs-12">
					<div class="form-group">
						<textarea row="3" class="form-control" name="description"></textarea>
					</div>
				</div>
				<div class="box-footer">
					<div class="col-xs-6">
						<label>校验提示</label>
				   		<div id="goods-edit-page_cue" class="validate-font-color"></div>
					</div>
					<div class="col-xs-6">
						<button type="button" class="back btn btn-default">返回</button>
				   		<button class="btn btn-default pull-right" type="submit">提交</button>
					</div>
				</div>
			</form>
		</div>

	</div>
</div>