/**
 * 
 */
app.define('goods/add', ['goods/service', 'common/validate'], function(service, validation) {
	return function() {
		var addForm, submitButton, goodsNameInput, priceInput, amountInput, supplierSelect;
		addForm = app.selectOne('form#goods-add');
		submitButton = addForm.querySelector('button[type="submit"]');
		goodsNameInput = addForm.querySelector('input[name="goodsName"]');
		priceInput = addForm.querySelector('input[name="price"]');
		amountInput = addForm.querySelector('input[name="amount"]');
		supplierSelect = addForm.querySelector('select[name="supplierId"]');
		submitButton.disabled = true;
		/**
		 * 校验方法
		 */
		function validateListener() {
			var goodsName = goodsNameInput.value;
			var price = priceInput.value;
			var amount = amountInput.value;
			if ((validation.float(price) || validation.integer(price))
				&& validation.integer(amount)
				&& validation.text(goodsName))
			{
				submitButton.disabled = false;
				app.byid('goods-add-page_cue').innerHTML = '输入有效!';
			}
			else {
				submitButton.disabled = true;
				app.byid('goods-add-page_cue').innerHTML = '输入不完整或有误!';
			}
		}
		goodsNameInput.onblur = validateListener;
		priceInput.onblur = validateListener;
		amountInput.onblur = validateListener;
		supplierSelect.onchange = validateListener;
		service.addTab();
		
		addForm.onsubmit = function(event) {
			event.preventDefault();
			service.add().success(function(xhr) {
				if (xhr.status === 201) {
					app.selectOne('form#goods-query input[type="submit"]').click();
				} else {
					alert('添加失败!');
				}
			}).error(function(xhr) {
				console.log(xhr);
				alert('添加失败，检查提交数据是否符合要求，查看是否有相应权限或session失效');
			});
		};
	};
});