/**
 * websocket支持的聊天应用
 */
app.define('footer/chat', [], function() {
    var input = document.getElementById('chat');		// 查找输入字段
    input.focus();										// 设置光标

    // 打开一个websocket，用于发送和接收聊天消息，协议由http://变为ws://
    var socket = new WebSocket('ws://' + location.host + '/frontend/chat/hello');

    // 下面展示如何通过websocket从服务器获取消息
    socket.onmessage = function(event) {			// 当收到一条消息
        var msg = event.data;						// 从事件对象中获取消息内容
        append(msg);
    }
    
/*    socket.onopen = function() {
		var intervalId = window.setInterval(function() {
			if (socket.readyState != WebSocket.OPEN) {
				window.clearInterval(intervalId);
				return;
			}
			if (socket.bufferedAmount == 0)
				socket.send(updatedModelData);
		}, 50);
	}*/
    
    socket.onclose = function(event) {
    	console.log('WebSocketClosed! ' + event.data);
    }

    socket.onerror = function(event) {
    	alert('WebSocketError! ' + event.data);
    }
    
    // 下面展示了如何通过websocket发送消息给服务器端
    input.onchange = function() {							// 当用户敲击回车键
    	if (socket.readyState != WebSocket.OPEN) {
			alert(socket.readyState);
			return;
		}
        socket.send(input.value);							// 通过套接字传递该内容
        input.value = '';									// 等待更多内容的输入
        append(msg);
    }
    
    function append(msg) {
    	document.getElementById('chatmsg').value += new Date().toLocaleString() + '  ' + msg + '\n';
        var d = document.getElementById("chatmsg").scrollHeight;// 及时显示最底部的信息
        document.getElementById("chatmsg").scrollTop = d;
    }

});
