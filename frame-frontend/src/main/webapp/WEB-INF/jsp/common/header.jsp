<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" errorPage="../common/error.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!-- Static navbar -->
<nav class="navbar navbar-default" role="navigation">
	<div class="container-fluid">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed"
				data-toggle="collapse" data-target="#navbar" aria-expanded="false"
				aria-controls="navbar">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="javascript:void(0)">我的框架</a>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<ul class="nav navbar-nav">
				<li id="goodsLabel" class="active"><a href="javascript:void(0)">商品管理</a></li>
				<li id="supplierLabel"><a href="javascript:void(0)">供应商管理</a></li>
				<li id="uploadLabel"><a href="javascript:void(0)">文件上传</a></li>
			</ul>
			<ul class="nav navbar-nav navbar-right">
				<li>
					<a class="btn" href="login?action=logout">
						<strong>退出</strong>
						<img width="19px" height="20px" alt="${sessionScope.user.name}"	src="${sessionScope.user.icon}">
					</a>
				</li>
			</ul>
		</div>
		<!--/.nav-collapse -->
	</div>
	<!--/.container-fluid -->
</nav>


<nav class="panel">


</nav>