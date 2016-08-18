util.whenReady(function() {
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
	 * 下载功能
	 */
	app.byid("download").onclick = function() {
		window.open('file/download?filename=img.png');
	};
	/**
	 * 图片翻转功能
	 */
	util.rollover();
	/**
	 * multipart/form-data文件上传功能
	 */
	var promise;
	app.byid('multiUpload').onsubmit = function(event) {
		event.preventDefault();
		promise = util.post('file/multiUpload', this, null, progressListener);
		promise.success(function(xhr) {
			alert(xhr.responseText);
		}).error(function(xhr) {
			console.log(xhr);
			alert('上传失败，检查是否超出容量，有无权限');
		});
	};
	app.byid('cancel').onclick = function(event) {
		event.preventDefault();
		if (promise) {
			promise.abort = true;
		}
	};
	
	function progressListener(value) {
		app.byid('progress').value = value;
	}
	
	/**
	 * 聊天功能
	 */
	app.require(['footer/chat'], function(ctrl) {
		ctrl();
	});
	
	loadGoodsPage();
	
});

