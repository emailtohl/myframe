/**
 * 
 */
app.define('supplier/controller', ['supplier/service'], function(service) {
	return function() {
		var callee = arguments.callee;
		
		util.makeSortable(app.selectOne('table'));
		
		// 查询按钮
		app.byid('supplier-query').onsubmit = function(event) {
			event.preventDefault();
			service.getPage().success(function(xhr) {
				app.byid("body").innerHTML = xhr.responseText;
				callee();
			});
		};
		
		// 分页按钮
		(function pageable() {
			var a = app.byid('supplier-page').getElementsByClassName('paging-navigation')[0].getElementsByClassName('page-num');
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
		
		// 保存
		(function() {
			var saveBtns, i;
			saveBtns = app.selectAll('button.save-supplier-button');
			for (i = 0; i < saveBtns.length; i++) {
				saveBtns[i].onclick = saveListener;
			}
		})();
		
		function saveListener(event) {
			var j, inputs, data = {};
			event.preventDefault();
			inputs = this.parentNode.parentNode.querySelectorAll('input.edit');
			for (j = 0; j < inputs.length; j++) {
				if(inputs[j].value != null) {// 空字符串不会被忽略
					data[inputs[j].name] = inputs[j].value;
				}
			}
			if (data.supplierId) {// 如果有ID，则进行修改
				service.update(data).success(function(xhr) {
					app.selectOne('form#supplier-query input[type="submit"]').click();
				}).error(function(xhr) {
					console.log(xhr);
					alert('修改异常，检查提交数据是否符合要求，查看是否有相应权限或session失效');
				});
			} else {// 否则就添加
				service.add(data).success(function(xhr) {
					app.selectOne('form#supplier-query input[type="submit"]').click();
				}).error(function(xhr) {
					console.log(xhr);
					alert('添加异常，检查提交数据是否符合要求，查看是否有相应权限或session失效');
				});
			}
		}
		
		// 删除
		(function() {
			var deleteButtons, i;
			var deleteButtons = app.selectAll('button.delete-supplier-button');
			for(i = 0; i < deleteButtons.length; i++) {
				deleteButtons[i].onclick = deleteListener;
			}
		})();
		
		function deleteListener(event) {
			var j, idInput, tr;
			event.preventDefault();
			idInput = this.parentNode.parentNode.querySelector('input[name="supplierId"]');
			if (idInput && idInput.value) {
				util.shake('table');
				setTimeout(function() {
					if (confirm("你确定要删除此项吗？")) {
						service['delete'](idInput.value).success(function(xhr) {
							app.selectOne('form#supplier-query input[type="submit"]').click();
						}).error(function(xhr) {
							console.log(xhr);
							alert('删除失败，查看是否有相应权限或session失效');
						});
					}
				}, 500);
			} else {
				tr = this.parentNode.parentNode;
				tr.parentNode.removeChild(tr);
			}
		}
		
		// 添加tr
		(function() {
			app.byid('add-supplier-button').onclick = function(event) {
				event.preventDefault();
				var tr = document.createElement('tr');
				var html = 
					'<td><input name="supplierId" type="hidden" class="edit form-control" readonly="readonly"></td>'+
					'<td><input name="supplierName" type="text" class="edit form-control"></td>'+
					'<td><input name="address" type="text" class="edit form-control"></td>'+
					'<td><input name="tel" type="text" class="edit form-control"></td>'+
					'<td><input name="description" type="text" class="edit form-control"></td>'+
					'<td><input name="email" type="email" class="edit form-control"></td>'+
					'<td><input name="rank" type="text" class="edit form-control"></td>'+
					'<td><button class="save-supplier-button btn btn-success">保存</button></td>'+
					'<td><button class="delete-supplier-button btn btn-warning">删除</button></td>';
				tr.innerHTML = html;
				tr.querySelector('.save-supplier-button').onclick = saveListener;
				tr.querySelector('.delete-supplier-button').onclick = deleteListener;
				app.selectOne('tbody').appendChild(tr);
			};
		})();
		
	};
});