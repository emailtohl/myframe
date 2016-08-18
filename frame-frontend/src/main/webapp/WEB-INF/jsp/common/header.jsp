<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" errorPage="../common/error.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<nav class="panel">
	<ul class="nav nav-pills">
	  <li role="presentation" class="active" id="goodsLabel"><a href="javascript:void(0)">商品管理</a></li>
	  <li role="presentation" id="supplierLabel"><a href="javascript:void(0)">供应商管理</a></li>
	  <li role="presentation"><a href="login?action=logout"><img width="39" height="50" alt="${sessionScope.user.name}" src="${sessionScope.user.icon}"></a></li>
	</ul>
	
	<div id="upload-div">
		<a id="download"><img width="85" height="50" alt="头像" src="resource/images/img.png" data-rollover="resource/images/rollover_img.png"></a>
		<progress id="progress" max="100" value="0" class=""></progress>
		
		<!-- 测试multipart/form-data -->
		
		<div style="margin-top:20px" class="form-group">
			<form id="multiUpload" action="file/multiUpload" method="post" enctype="multipart/form-data">
				<input type="text" name="foo" value="foo" class="form-control"><br>
				<input type="text" name="bar" value="bar" class="form-control"><br>
				<input name = "file1" type="file" multiple="multiple" value="abc" class="btn btn-primary btn-xs"><br>
				<input name = "file2" type="file" class="btn btn-default btn-xs"><br>
				<input type="submit" value="上传" class="btn btn-xs btn-info">
				<input type="button" id="cancel" class="btn btn-xs btn-danger" value="取消">
			</form>
		</div>
	</div>
</nav>