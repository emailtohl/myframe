util.whenReady(function() {
	document.querySelector('input[name="email"]').value = util.getCookie('email');
	document.querySelector('input[name="password"]').value = util.getCookie('password');
	document.getElementById('rememberMe').checked = util.getCookie('rememberMe');
	
	document.getElementById('rememberMe').onchange = function(e) {
		if (this.checked) {
			var email = document.querySelector('input[name="email"]').value;
			var password = document.querySelector('input[name="password"]').value;
			util.setCookie('email', email, 30);
			util.setCookie('password', password, 30);
			util.setCookie('rememberMe', true, 30);
		} else {
			util.setCookie('email', '', 0);
			util.setCookie('password', '', 0);
			util.setCookie('rememberMe', false, 0);
		}
	};
	
});