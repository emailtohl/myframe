/**
 * 
 */
app.define('goods/edit', ['goods/service', 'common/validate'], function(service, validation) {
	return function(id) {
		var model, updateForm, submitButton, goodsNameInput, priceInput, amountInput, supplierSelect, descriptionTextarea;
		updateForm = app.selectOne('form#goods-edit');
		submitButton = updateForm.querySelector('button[type="submit"]');
		goodsNameInput = updateForm.querySelector('input[name="goodsName"]');
		priceInput = updateForm.querySelector('input[name="price"]');
		amountInput = updateForm.querySelector('input[name="amount"]');
		supplierSelect = updateForm.querySelector('select[name="supplierId"]');
		descriptionTextarea = updateForm.querySelector('textarea[name="description"]');
		
		/**
		 * 校验方法
		 */
		function validate() {
			var goodsName = goodsNameInput.value;
			var price = priceInput.value;
			var amount = amountInput.value;
			if ((validation.float(price) || validation.integer(price))
					&& validation.integer(amount)
					&& validation.text(goodsName))
			{
				submitButton.disabled = false;
				app.byid('goods-edit-page_cue').innerHTML = '输入有效!';
			}
			else {
				submitButton.disabled = true;
				app.byid('goods-edit-page_cue').innerHTML = '输入不完整或有误!';
			}
		}
		
		service.getModel({goodsId : id}).success(function(xhr) {
			var json = xhr.responseText, arr;
			arr = JSON.parse(json);
			if (arr.length > 0) {
				model = arr[0];
			}
			goodsNameInput.value = model.goodsName;
			goodsNameInput.onkeyup = function(event) {
				validate();
				model.goodsName = this.value;
			};
			
			priceInput.value = model.price;
			priceInput.onkeyup = function(event) {
				validate();
				model.price = this.value;
			};
			
			amountInput.value = model.amount;
			amountInput.onkeyup = function(event) {
				validate();
				model.amount = this.value;
			};
			
			supplierSelect.value = model.supplierId;
			supplierSelect.onchange = function(event) {
				validate();
				model.supplierId = this.value;
			};
			
			descriptionTextarea.value = model.description;
			descriptionTextarea.onkeyup = function(event) {
				validate();
				model.description = this.value;
			};
			
			// 设置checkbox
			(function setCheckBox(type) {
				var types = type.split(',');
				var checkboxes = updateForm.querySelectorAll('input[type="checkbox"]');
				var checkboxesMap = {};// key是checkbox值，value是该checkbox的dom元素
				for (var i = 0; i < checkboxes.length; i++) {
					checkboxesMap[checkboxes[i].value] = checkboxes[i];
				}
				for (i = 0; i < types.length; i++) {
					var checkboxEle = checkboxesMap[types[i].trim()];
					if (checkboxEle)
						checkboxEle.checked = true;
				}
				/**
				 * checkbox改变后维护到model的值
				 */
				function maintainCheckbox() {
					var s = '', first = true;
					for (var i = 0; i < checkboxes.length; i++) {
						if (checkboxes[i].checked) {
							if (first) {
								first = false;
							} else {
								s += ',';
							}
							s += checkboxes[i].value;
						}
					}
					model.type = s;
				}
				for (var i = 0; i < checkboxes.length; i++) {
					checkboxes[i].onchange = maintainCheckbox;
				}
			})(model.type);

			updateForm.onsubmit = function(event) {
				event.preventDefault();
				delete model.supplierName;
				service.update(model).success(function(xhr) {
					app.selectOne('form#goods-query input[type="submit"]').click();
				}).error(function(xhr) {
					console.log(xhr);
					alert('修改失败，检查提交数据是否符合要求，查看是否有相应权限或session失效');
				});
			};
			
			// 准备好后，打开页面
			service.editTab();
			
		}).error(function(xhr) {
			console.log(xhr);
			alert('获取失败，查看是否有相应权限或session失效');
		});
		
	};
});