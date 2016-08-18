package com.github.emailtohl.frame.site.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;
import javax.websocket.EncodeException;
import javax.websocket.HandshakeResponse;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;

import com.github.emailtohl.frame.site.dao.po.User;
/**
 * websocket服务端，可通过ChatEndpoint.EndpointConfigurator获取到HttpSession
 * @author helei
 */
@SuppressWarnings("unused")
@ServerEndpoint(value = "/chat/{param}", configurator = ChatEndpoint.EndpointConfigurator.class)
public class ChatEndpoint {
	private static final Logger logger = Logger.getLogger(ChatEndpoint.class.getName());
	private static final Map<Session, HttpSession> seesionMap = new ConcurrentHashMap<Session, HttpSession>();
	private Map<String, Object> userProperties;
	private HttpSession httpSession;
	private User user;
	private String name;
	
	@OnOpen
	public void onOpen(Session session, @PathParam("param") String param) throws EncodeException {
		httpSession = (HttpSession) session.getUserProperties().get("HTTP_SESSION");
		logger.log(Level.FINEST, "用户参数： " + param);
		user = (User) httpSession.getAttribute("user");
		name = user == null ? param : user.getName();
		if (name == null) {
			name = "";
		}
		for (Session s : seesionMap.keySet()) {
			try {
				s.getBasicRemote().sendText("welcome new user：" + name + " sessionId： " + session.getId());
			} catch (IOException e) {
				logger.log(Level.FINEST, "发送消息失败，sessionId： " + s.getId(), e);
			}
		}
		seesionMap.put(session, httpSession);
	}

	@OnMessage
	public void onMessage(Session session, String message, @PathParam("param") String param) {
		for (Session s : seesionMap.keySet()) {
			try {
				s.getBasicRemote().sendText("sessionId： " + session.getId() + "  " + name + ":  " + message);
			} catch (IOException e) {
				logger.log(Level.FINEST, "发送消息失败，sessionId： " + s.getId(), e);
			}
		}
	}

	@OnClose
	public void onClose(Session session, @PathParam("param") String param) {
		seesionMap.remove(session);
		for (Session s : seesionMap.keySet()) {
			try {
				s.getBasicRemote().sendText("goodbye user：" + name + " sessionId： " + session.getId());
			} catch (IOException e) {
				logger.log(Level.FINEST, "发送消息失败，sessionId： " + s.getId(), e);
			}
		}
	}

	@OnError
	public void onError(Session session, Throwable e) {

	}

	/**
	 * 通过http来建立websoctet，握手时的配置，可在此获取到HttpSession
	 * 
	 * @author helei
	 */
	public static class EndpointConfigurator extends ServerEndpointConfig.Configurator {
		@Override
		public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
			super.modifyHandshake(config, request, response);
			config.getUserProperties().put("HTTP_SESSION", request.getHttpSession());
		}
	}
}
