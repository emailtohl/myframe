/**
 * 用于前端校验
 */
app.define('common/validate', [], function() {
	
	return {
			text : function(string) {
				var reg = /^[0-9a-zA-Z\u4e00-\u9fa5]*$/;
				if(reg.exec(string))
					return true;
				else
					return false;
			},
			
			integer : function(digit) {
				var reg = /^[0-9]*[1-9][0-9]*$/;
				if(reg.exec(digit))
					return true;
				else
					return false;
			},
			
			float : function(digit) {
				var reg = /^\d+\.{1}\d+?$/;
				if(reg.exec(digit))
					return true;
				else
					return false;
			},
			
	};
	
});