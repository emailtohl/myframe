/**
 * 
 */
app.define('supplier/service', [], function() {
	return {
		getPage : function() {
			var url, urlArgs, form;
			form = app.byid('supplier-query');
			urlArgs = util.serialize(form);
			url = 'supplier/page?' + urlArgs;
			return util.get(url);
		},
		add : function(data) {
			var url = 'supplier/add';
			return util.post(url, data);
		},
		update : function(data) {
			var requestBody = util.encodeUrlParams(data);
			return util.ajax({
				url : 'supplier/update',
				method : 'PUT',
				requestBody : requestBody
			});
		},
		'delete' : function(id) {
			return util.ajax({
				url : 'supplier/delete' + '?supplierId=' + id,
				method : 'DELETE',
			});
		},
	};
});