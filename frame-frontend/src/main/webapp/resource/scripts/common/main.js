util.whenReady(function() {
	// Cotext来自于执行的script标签，第二个参数是脚本的根目录
	Application = util.inherit(Context, 'resource/scripts/');
	Application.prototype.byid = function(id) {
		return document.getElementById(id);
	};
	Application.prototype.selectAll = function(selector) {
		return document.querySelectorAll(selector);
	};
	Application.prototype.selectOne = function(selector) {
		return document.querySelector(selector);
	};
	app = new Application();
	
	/**
	 * 加载商品页面
	 */
	function loadGoodsPage() {
		util.get('goods/page', function(xhr) {
			app.byid("body").innerHTML = xhr.responseText;
			app.require(['goods/controller'], function(ctrl) {
				ctrl();
			});
		});
	}
	/**
	 * 加载供应商页面
	 */
	function loadSupplierPage() {
		util.get('supplier/page', function(xhr) {
			app.byid("body").innerHTML = xhr.responseText;
			app.require(['supplier/service', 'supplier/controller'], function(service, ctrl) {
				ctrl();
			});
		});
	}
	/**
	 * 加载文件上传页面
	 * @returns
	 */
	function loadUploadPage() {
		util.get('file/page', function(xhr) {
			app.byid("body").innerHTML = xhr.responseText;
			app.require(['upload/service', 'upload/controller'], function(service, ctrl) {
				ctrl();
			});
		});
	}
	/**
	 * 为Tab页添加激活样式
	 */
	function active(activItem) {
		var lis = app.selectAll('.nav li');
		for (var i = 0; i < lis.length; i++) {
			lis[i].classList.remove('active');
		}
		activItem.classList.add('active');
	}
	/**
	 * 切换到商品模块
	 */
	app.byid("goodsLabel").onclick = function() {
		loadGoodsPage();
		active(this);
	};
	/**
	 * 切换到供应商模块
	 */
	app.byid("supplierLabel").onclick = function() {
		loadSupplierPage();
		active(this);
	};
	/**
	 * 切换上传模块
	 */
	app.byid("uploadLabel").onclick = function() {
		loadUploadPage();
		active(this);
	};
	
	/**
	 * 聊天功能
	 */
	app.require(['footer/chat'], function(ctrl) {
		ctrl();
	});
	
	loadGoodsPage();
	
});

