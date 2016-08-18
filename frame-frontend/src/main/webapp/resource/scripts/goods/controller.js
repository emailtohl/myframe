/**
 * 
 */
app.define('goods/controller', ['goods/service', 'common/validate', 'goods/add', 'goods/edit'],
		function(service, validation, addModule, editModule) {
	return function() {
		var callee = arguments.callee;
		service.listTab();
		util.makeSortable(app.selectOne('table'));
		
		// 查询功能
		app.byid('goods-query').onsubmit = function(event) {
			event.preventDefault();
			service.getPage().success(function(xhr) {
				app.byid("body").innerHTML = xhr.responseText;
				callee();
			}).error(function(xhr) {
				console.log(xhr);
				alert('查看是否有相应权限或session失效');
			});
		};
		
		// 分页按钮
		(function pageable() {// 因需要使用额外参数，故使用立即执行函数
			var a = app.byid('goods-page').getElementsByClassName('paging-navigation')[0].getElementsByClassName('page-num');
			for (var i = 0; i < a.length; i++) {
				a[i].onclick = function(event) {
					event.preventDefault();
					var pageNum = this.getAttribute('data-page');
					app.selectOne('input[name="pageNum"]').value = pageNum;
					service.getPage().success(function(xhr) {
						app.byid("body").innerHTML = xhr.responseText;
						callee();
					});
				}
			}
		})();
		
		// 为输入框添加校验监听
		(function inputValid() {
			var combinationQueryInputs = app.selectAll('form#goods-query input');
			for(var i in combinationQueryInputs) {
				combinationQueryInputs[i].onkeyup = function() {
					var price = app.selectOne('form#goods-query input[name="price"]').value;
					var amount = app.selectOne('form#goods-query input[name="amount"]').value;
					var goodsName = app.selectOne('form#goods-query input[name="goodsName"]').value;
					var supplierName = app.selectOne('form#goods-query input[name="supplierName"]').value;

					if (("" == price || validation.float(price))
						&& ("" == amount || validation.integer(amount))
						&& ("" == goodsName || validation.text(goodsName))
						&& ("" == supplierName || validation.text(supplierName)))
						app.selectOne('form#goods-query span.tip').style.display = 'none';
					else
						app.selectOne('form#goods-query span.tip').style.display = 'inline';
				};
			}
		})();
		
		// 为添加和编辑Tab页中的退回按钮添加监听
		(function() {
			var backButtons = document.getElementsByClassName('back');
			for(var i = 0; i < backButtons.length; i++) {
				backButtons[i].onclick = function(event) {
					event.preventDefault();
					service.listTab();
				};
			}
		})();
		
		// 添加商品功能
		app.selectOne('input#open-add-page-button').onclick = function(event) {
			event.preventDefault();
			addModule()
		};
		
		// 编辑商品功能
		(function() {
			var i, editBtn = app.selectAll('input.edit'), id;
			for (i = 0; i < editBtn.length; i++) {
				editBtn[i].onclick = function(event) {
					event.preventDefault();
					id = this.getAttribute('data-id');
					editModule(id);
				}; 
			}
		})();
		
		// 删除功能
		(function(){
			var i, deleteBtn = app.selectAll('input.delete');
			for (i = 0; i < deleteBtn.length; i++) {
				deleteBtn[i].onclick = function(event) {
					var id = this.getAttribute('data-id');
					event.preventDefault();
					util.shake('table');
					setTimeout(function() {
						if (confirm("你确定要删除此项吗？")) {
							service['delete'](id).success(function(xhr) {
								app.selectOne('form#goods-query input[type="submit"]').click();
							}).error(function(xhr) {
								console.log(xhr);
								alert('删除失败，查看是否有相应权限或session失效');
							});
						}
					}, 500);
				}; 
			}
		})();
		
		// 另一种分页方式，通过JS代码创建的分页按钮
		(function() {
			var nav, totalPage, pageNum;
			nav = app.selectOne('.page-item');
			totalPage = nav.getAttribute('data-totalpage');
			pageNum = nav.getAttribute('data-pagenum');
			util.createPageItems(nav, totalPage, pageNum).onclick(function(pageNum) {
				app.selectOne('input[name="pageNum"]').value = pageNum;
				app.selectOne('form#goods-query input[type="submit"]').click();
			});
		})();
		
	}
});