<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html lang="zh">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <title>Log in</title>
  <!-- Tell the browser to be responsive to screen width -->
  <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
  <link rel="stylesheet" href="resource/lib/bootstrap/css/bootstrap.css" />
  <link rel="stylesheet" href="resource/lib/bootstrap/css/bootstrap-theme.css" />
</head>
<body>
<div class="login-box">
  <!-- /.login-logo -->
  <div style="width: 300px;margin: 100px auto;">
    <h2>登录</h2>
    <form action="login" method="post">
      <div class="form-group">
        <input type="email" class="form-control" placeholder="Email" name="email" value="${reqUser.email}">
      </div>
      <div class="form-group has-feedback">
        <input type="password" class="form-control" placeholder="Password" name="password" value="${reqUser.password}">
      </div>
      <div class="row">
        <div class="col-xs-8">
          <div class="checkbox icheck">
            <label>
              <input id="rememberMe" type="checkbox"> 记住密码
            </label>
          </div>
        </div>
        <!-- /.col -->
        <div class="col-xs-4">
          <input type="submit" class="btn btn-primary btn-block btn-flat" value="登录"></input>
        </div>
        <!-- /.col -->
      </div>
    </form>
    <a href="#">忘记密码</a><br>
    <a href="register.html" class="text-center">注册</a>
    <c:if test="${failure}">
	  <div class="alert alert-danger" role="alert">登录失败</div>
	</c:if>
  </div>
  <!-- /.login-box-body -->
</div>
<!-- /.login-box -->

<script type="text/javascript" src="resource/lib/util.js"></script>
<script type="text/javascript" src="resource/scripts/common/login.js"></script>

</body>
</html>