<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" errorPage="../common/error.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="myTagDemo" prefix="mine"%>
<%@ page import="java.util.Random" %>

<html lang="zh-CN">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<!-- 强制让文档的宽度与设备的宽度保持1:1，并且文档最大的宽度比例是1.0，且不允许用户点击屏幕放大浏览
<meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0;" name="viewport" />
iphone设备中的safari私有meta标签，它表示：允许全屏模式浏览
<meta content="yes" name="apple-mobile-web-app-capable" />
iphone的私有标签，它指定的iphone中safari顶端的状态条的样式
<meta content="black" name="apple-mobile-web-app-status-bar-style" />
告诉设备忽略将页面中的数字识别为电话号码
<meta content="telephone=no" name="format-detection" />
 -->

<title>
<mine:showGreeting randomNum="<%= new Random().nextInt(21) %>">
${greeting}
</mine:showGreeting></title>
<link rel="stylesheet" href="resource/lib/bootstrap/css/bootstrap.css" />
<link rel="stylesheet" href="resource/lib/bootstrap/css/bootstrap-theme.css" />
<link rel="stylesheet" href="resource/css/common/main.css" />
<link rel="stylesheet" href="resource/css/common/header.css" />
<link rel="stylesheet" href="resource/css/common/footer.css" />
<link rel='stylesheet' href='resource/css/goods/goods.css' />
<link rel='stylesheet' href='resource/css/supplier/supplier.css' />

<script type="text/javascript" src="resource/lib/util.js"></script>
<script type="text/javascript" src="resource/lib/context.js"></script>
<script type="text/javascript" src="resource/scripts/common/main.js"></script>
<script type="text/javascript" src="resource/lib/jquery/jquery-3.1.0.js"></script>
</head>
<body>
<div class="container-fluid">
	<div id="header"><jsp:include page="header.jsp"></jsp:include></div>
	<div id="body"></div>
	<jsp:include page="footer.jsp"></jsp:include>
</div>
</body>
</html>