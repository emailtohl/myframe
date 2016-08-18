/**
 * 全局环境中创建构造函数，组织上下文模块，本工具模仿requirejs，符合AMD风格
 * 为了让require、define方法有效，生成的实例最好是在全局空间中
 * 
 * version v1.0 author 何磊 date 2016.06.25
 */
function Context(baseUrl) {
	var self = this, module = {}/* 存储被加载的模块 */, callbackMap = {}/* 存储所有回调函数，便于在条件满足时执行它们 */;
	if (baseUrl) {
		this.base = baseUrl;
	} else {
		this.base = '/';
	}
	/**
	 * moduleName是去掉base和“.js”后缀的名字
	 */
	function getPath(moduleName) {
		return self.base + moduleName + '.js';
	}
	/**
	 * 异步加载js文件，若已被加载，则不再加载
	 */
	function loadasync(url, onload) {
		var head, exist, script;
		head = document.getElementsByTagName('head')[0];
		exist = head.querySelector('script[src="' + url + '"]');
		if (!exist) {
			script = document.createElement('script');
			script.src = url;
			if (onload) {
				script.onload = onload;
			}
			head.appendChild(script);
		}
	}
	/**
	 * 添加一个回调函数到callbackMap中，使用时间戳作为key值
	 */
	function addCallback(stringArray, callback, moduleName) {
		var key, value;
		key = new Date().getTime().toString();
		value = {
			'key' : key,
			'stringArray' : stringArray,
			'callback' : callback,
			'moduleName' : moduleName
		};
		callbackMap[key] = value;
	}
	/**
	 * 从callbackMap中返回一个优先级队列
	 */
	function getPriority() {
		var key, tempList = [];
		for (key in callbackMap) {// 遍历所有的回调函数，并存放于优先级队列中
			if (callbackMap.hasOwnProperty(key)) {
				tempList.push(callbackMap[key]);
			}
		}
		/*
		 * 比较函数接收两个参数，如果第一个参数应该位于第二个之前则返回一个负数，如果两个参数相等则返回0
		 * 如果第一个参数应该位于第二个之后则返回一个正数
		 */
		return tempList.sort(function(a, b) {
			var i;
			if (!a.moduleName) {// 直接使用this.require时，没有moduleName，它应该是最低优先级执行的
				return 1;
			}
			if (!b.moduleName) {// 直接使用this.require时，没有moduleName，它应该是最低优先级执行的
				return -1;
			}
			for (i = 0; i < b.stringArray.length; i++) {
				if (a.moduleName == b.stringArray[i]) {// 如果a在b的依赖表中
					return -1;
				}
			}
			for (i = 0; i < a.stringArray.length; i++) {
				if (b.moduleName == a.stringArray[i]) {// 如果b在a的依赖表中
					return 1;
				}
			}
			return 0;
		});
	}
	/**
	 * 启动所有准备就绪的回调函数
	 */
	function invokeAllCallback() {
		var priority, i, j, key, value, stringArray, callback, flag, m, objectArray = [];
		priority = getPriority();
		// console.log(callbackMap);
		// console.log(priority);
		for (i = 0; i < priority.length; i++) {
			value = priority[i];
			stringArray = value['stringArray'];
			callback = value['callback'];
			flag = true;
			for (j = 0; j < stringArray.length; j++) {// 遍历该回调函数的参数
				m = module[stringArray[j]];
				if (m) {
					objectArray[j] = m;
				} else {// 若某个参数还未就绪，则暂不回调
					flag = false;
					objectArray.length = 0;
					break;
				}
			}
			if (flag) {
				callback.apply(self, objectArray);
				key = value['key'];
				delete callbackMap[key];// 清理已执行过的回调函数
				// console.log(callbackMap);
			}
		}
	}
	this.require = function(stringArray/* 表示注入的moduleName数组 */,
			callback/* 加载完成后继续执行的回调函数 */, moduleName/* stringArray的执行一定要在moduleName前 */) {
		var i, m, objectArray = [], flag = true, path;
		// 获取模块需要的参数
		for (i = 0; i < stringArray.length; i++) {
			m = module[stringArray[i]];
			if (m) {
				objectArray[i] = m;
			} else {
				flag = false;
				path = getPath(stringArray[i]);
				loadasync(path, function() {
					/*
					 * 实际上，这里并不确定onload事件是发生在被加载的js文件执行完之前还是执行完之后，若是前者则会出现问题
					 * 经测试，的确是执行完加载的js文件才触发onload事件
					 * 所以，被加载的js文件在执行完this.define方法后，会将模块保存在module的空间中
					 */
					invokeAllCallback();
				});
			}
		}
		if (flag) {
			callback.apply(self, objectArray);
		} else {
			addCallback(stringArray, callback, moduleName);
		}
	}
	this.define = function(moduleName/* 模块名 */, stringArray/* 依赖的moduleName数组 */, func/* 定义的模块 */) {
		self.require(stringArray, function(/* 由于接收的是参数数组，这里的变量未知，故直接在函数体内使用arguments */) {
			module[moduleName] = func.apply(self, arguments);
		}, moduleName);
	};
}