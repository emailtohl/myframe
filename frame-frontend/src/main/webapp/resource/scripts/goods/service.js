/**
 * 
 */
app.define('goods/service', [], function() {
	return {
		listTab : function() {
			app.byid('goods-page').style.display = 'block';
			var subPages = document.getElementsByClassName('sub-page');
			for (var i = 0; i < subPages.length; i++) {
				subPages[i].style.display = 'none';
			}
			app.byid('goods-list-page').style.display = 'block';
		},
		addTab : function() {
			var subPages = document.getElementsByClassName('sub-page');
			for (var i = 0; i < subPages.length; i++) {
				subPages[i].style.display = 'none';
			}
			app.byid('goods-add-page').style.display = 'inline-table';
		},
		editTab : function() {
			var subPages = document.getElementsByClassName('sub-page');
			for (var i = 0; i < subPages.length; i++) {
				subPages[i].style.display = 'none';
			}
			app.byid('goods-edit-page').style.display = 'inline-table';
			app.selectOne('#goods-edit-page button[type="submit"]').disabled = false;
		},
		/**
		 * 处理相应
		 */
		response : function(xhr) {
			var msg = xhr.responseText;
			if (msg && msg.trim().indexOf("<!DOCTYPE html>") == -1) {//如果后台校验失败，会返回main页面，这时页面显示错误，需要重新加载
				app.byid("body").innerHTML = msg;
			} else {
				goodsPage().success(function(xhr) {
					app.byid("body").innerHTML = xhr.responseText;
				}).error(function(xhr) {
					alert(xhr.status)
				});
			}
		},
		getPageNum : function() {
			return app.selectOne('input[name="pageNum"]').value;
		},
		getPage : function() {
			var url, urlArgs, form;
			form = app.byid('goods-query');
			urlArgs = util.serialize(form);
			url = 'goods/page?' + urlArgs;
			return util.get(url);
		},
		getModel : function(data) {
			var urlArgs = util.encodeUrlParams(data);
			urlArgs = urlArgs ? ('?' + urlArgs) : '';
			return util.get('goods/model' + urlArgs);
		},
		add : function() {
			var url, data;
			url = 'goods/add';
			// 直接传入表单元素
			data = util.getFormData(app.byid('goods-add'));
			return util.post(url, data);
		},
		update : function(model) {
//			var url, data;
//			url = 'goods/update';
//			// 传入的是model
//			data = util.getFormData(app.byid('goods-edit'));
//			return util.post(url, data);
			var requestBody = util.encodeUrlParams(model);
			return util.ajax({
				url : 'goods/update',
				method : 'PUT',
				requestBody : requestBody
			});
		},
		'delete' : function(id) {
			return util.ajax({
				url : 'goods/delete' + '?goodsId=' + id,
				method : 'DELETE',
			});
		},
	};
});