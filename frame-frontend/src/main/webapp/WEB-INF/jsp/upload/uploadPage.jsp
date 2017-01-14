<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" errorPage="../common/error.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div id="upload-page" class="page">
	<div class="box-header with-border">
		<h3 class="box-title">上传文件</h3>
	</div>
	<div class="row" id="upload-div">
		<div class="col-xs-6">
			<div class="form-group">
				<!-- 测试multipart/form-data -->
				<form id="multiUpload" action="file/multiUpload" method="post" enctype="multipart/form-data">
					<div class="form-group">
						<label for="">参数1</label>
						<input type="text" name="foo" value="foo" class="form-control"><br>
					</div>
					<div class="form-group">
						<label for="">参数2</label>
						<input type="text" name="bar" value="bar" class="form-control"><br>
					</div>
					<div class="form-group">
						<label for="">文件1</label>
						<input name="file1" type="file" multiple="multiple" value="abc" class="btn btn-primary btn-xs">
					</div>
					<div class="form-group">
						<label for="">文件2</label>
						<input name="file2" type="file" class="btn btn-default btn-xs">
					</div>
					<div class="form-group">
						<input type="submit" value="上传" class="btn btn-xs btn-info">
						<input type="button" id="cancel" class="btn btn-xs btn-danger" value="取消">
					</div>
				</form>
			</div>
		</div>
		<div class="col-xs-6 placeholders">
			<div class="row">
				<a id="download">
					<img class="img-circle" width="160" height="100" alt="头像" src="resource/images/img.png" data-rollover="resource/images/rollover_img.png">
				</a>
			</div>
			<div class="row" style="margin-top:26px;">
				<progress id="progress" max="100" value="0" class="" style="width: 500px;"></progress>
			</div>
		</div>
	</div>
</div>


