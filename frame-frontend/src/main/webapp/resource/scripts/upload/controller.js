/**
 * 
 */
app.define('upload/controller', [], function() {
	return function() {
		var callee = arguments.callee;
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
	}
});