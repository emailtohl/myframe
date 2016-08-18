package com.github.emailtohl.frame.util;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * 对URLConnection进行简单封装，可用于简单的HTTP的GET和POST请求
 * 
 * @author helei
 */
public class WebAccess {
	private static Logger logger = Logger.getLogger(WebAccess.class.getName());
	/**
	 * 代理，可为null
	 */
	private Proxy proxy;
	/**
	 * 用户名，可为null
	 */
	private String username;
	/**
	 * 密码，可为null
	 */
	private String password;

	public WebAccess() {
		super();
	}

	public WebAccess(Proxy proxy) {
		super();
		this.proxy = proxy;
	}

	public static class Response {
		/**
		 * 状态码
		 */
		private int status;
		/**
		 * 响应头信息
		 */
		private String head;
		/**
		 * 响应体信息
		 */
		private String body;

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public String getHead() {
			return head;
		}

		public void setHead(String head) {
			this.head = head;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}
	}

	/**
	 * URL对象中含有协议（http，https，ftp，file，jar:file:……）
	 * 当执行url.openConnection()时，将返回对应协议的URLConnection：
	 * 如果是http，就返回HttpURLConnection的实现，如果是https，就返回HttpsURLConnection的实现
	 * 
	 * 获取指定地址的一个URLConnection，如果不要求用户名和密码，则username和password可为null
	 * 
	 * @param urlName
	 *            资源地址
	 * @return URLConnection，通过URLConnection可以获取head信息和InputStream流
	 * @throws IOException
	 */
	public URLConnection getURLConnection(String urlName) throws IOException {
		URL url = new URL(urlName);
		URLConnection connection;
		if (proxy == null) {
			connection = url.openConnection();
		} else {
			connection = url.openConnection(proxy);
		}
		// set username, password
		if (username != null && password != null) {
			String input = username + ":" + password;
			String encoding = base64Encode(input);
			connection.setRequestProperty("Authorization", "Basic " + encoding);
		}
		return connection;
	}

	/**
	 * get请求不需做过多设置，故访问web站点的默认行为就是get
	 * 
	 * @param urlName
	 *            资源地址，若有参数，则以“?name1=value1&name2=value2”的形式拼接到url地址后面
	 * @return Response 包含响应头和响应体，如果是http请求，还包含响应状态码，若未获取响应状态码，则该值为0
	 * @throws IOException
	 *             通信出现异常，需要客户端程序处理
	 */
	public Response get(String urlName) throws IOException {
		URLConnection connection = getURLConnection(urlName);
		return get(connection);
	}

	protected Response get(URLConnection connection) throws IOException {
		connection.connect();
		StringBuilder head = new StringBuilder(), body = new StringBuilder();
		int status = 0;
		Map<String, List<String>> headers = connection.getHeaderFields();
		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			String key = entry.getKey();
			for (String value : entry.getValue()) {
				head.append(key + ": " + value + "\n");
			}
		}
		if (connection instanceof HttpURLConnection)
			status = ((HttpURLConnection) connection).getResponseCode();
		logger.fine("----------");
		logger.fine("getContentType: " + connection.getContentType());
		logger.fine("getContentLength: " + connection.getContentLength());
		logger.fine("getContentEncoding: " + connection.getContentEncoding());
		logger.fine("getDate: " + connection.getDate());
		logger.fine("getExpiration: " + connection.getExpiration());
		logger.fine("getLastModifed: " + connection.getLastModified());
		logger.fine("----------");
		try (Scanner in = new Scanner(connection.getInputStream())) {
			while (in.hasNextLine())
				body.append(in.nextLine()).append("\n");
		} catch (IOException e) {
			if (!(connection instanceof HttpURLConnection))
				throw e;
			InputStream err = ((HttpURLConnection) connection).getErrorStream();
			if (err == null)
				throw e;
			try (Scanner in = new Scanner(err)) {
				body.append(in.nextLine());
				body.append("\n");
			}
		}
		Response response = new Response();
		response.setStatus(status);
		response.setHead(head.toString());
		response.setBody(body.toString());
		return response;
	}

	/**
	 * 用字符串形式返回响应体内容
	 * 
	 * @param urlName
	 *            资源地址，若有参数，则以“?name1=value1&name2=value2”的形式拼接到url地址后面
	 * @return 以字符串格式返回响应的内容
	 * @throws IOException
	 *             通信出现异常，需要客户端程序处理
	 */
	public String doGet(String urlName) throws IOException {
		Response response = get(urlName);
		return response.getBody();
	}

	/**
	 * post请求，主要是向web站点发送数据，然后接收响应
	 * 
	 * @param urlString
	 *            资源地址
	 * @param requestBody
	 *            请求体
	 * @return 包含响应头和响应体，如果是http请求，还包含响应状态码，若未获取响应状态码，则该值为0
	 * @throws IOException
	 *             通信出现异常，需要客户端程序处理
	 */
	public Response post(String urlString, String requestBody) throws IOException {
		URLConnection connection = getURLConnection(urlString);
		return post(connection, requestBody);
	}

	protected Response post(URLConnection connection, String requestBody) throws IOException {
		StringBuilder head = new StringBuilder(), body = new StringBuilder();
		int status = 0;
		connection.setDoOutput(true);
		// 首先建立连接，将url中的参数发送到web站点上
		connection.connect();
		// 然后再发送请求体数据
		try (PrintWriter out = new PrintWriter(connection.getOutputStream())) {
			out.print(requestBody);
			out.flush();
		}
		Map<String, List<String>> headers = connection.getHeaderFields();
		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			String key = entry.getKey();
			for (String value : entry.getValue()) {
				head.append(key + ": " + value + "\n");
			}
		}
		if (connection instanceof HttpURLConnection)
			status = ((HttpURLConnection) connection).getResponseCode();
		logger.fine("----------");
		logger.fine("getContentType: " + connection.getContentType());
		logger.fine("getContentLength: " + connection.getContentLength());
		logger.fine("getContentEncoding: " + connection.getContentEncoding());
		logger.fine("getDate: " + connection.getDate());
		logger.fine("getExpiration: " + connection.getExpiration());
		logger.fine("getLastModifed: " + connection.getLastModified());
		logger.fine("----------");
		try (Scanner in = new Scanner(connection.getInputStream())) {
			while (in.hasNextLine()) {
				body.append(in.nextLine());
				body.append("\n");
			}
		} catch (IOException e) {
			if (!(connection instanceof HttpURLConnection))
				throw e;
			InputStream err = ((HttpURLConnection) connection).getErrorStream();
			if (err == null)
				throw e;
			try (Scanner in = new Scanner(err)) {
				body.append(in.nextLine());
				body.append("\n");
			}
		}
		Response response = new Response();
		response.setStatus(status);
		response.setHead(head.toString());
		response.setBody(body.toString());
		return response;
	}

	/**
	 * 通过post方法获取指定资源，并将http body作为字符串返回，如果不要求用户名和密码，则username和password可为null
	 * 
	 * @param urlString
	 *            资源地址，若有参数，则以“?name1=value1&name2=value2”的形式拼接到url地址后面
	 * @param nameValuePairs
	 *            Map形式的键值对
	 * @return 包含响应头和响应体，如果是http请求，还包含响应状态码，若未获取响应状态码，则该值为0
	 * @throws IOException
	 *             通信出现异常，需要客户端程序处理
	 */
	public String doPost(String urlString, Map<String, String> nameValuePairs) throws IOException {
		String requestBody = getUrlParams(nameValuePairs);
		return post(urlString, requestBody).getBody();
	}

	protected String getUrlParams(Map<String, String> nameValuePairs) {
		StringBuilder requestBody = new StringBuilder();
		boolean first = true;
		for (Map.Entry<String, String> pair : nameValuePairs.entrySet()) {
			if (first)
				first = false;
			else
				requestBody.append('&');
			String name = pair.getKey();
			String value = pair.getValue();
			requestBody.append(name);
			requestBody.append('=');
			try {
				requestBody.append(URLEncoder.encode(value, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return requestBody.toString();
	}

	public void setUsernameAndPassword(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public Proxy getProxy() {
		return proxy;
	}

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	/**
	 * Computes the Base64 encoding of a string.
	 * 
	 * @param s
	 *            a string
	 * @return the Base64 encoding of s
	 */
	public String base64Encode(String s) {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		try (Base64OutputStream out = new Base64OutputStream(bOut)) {
			out.write(s.getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bOut.toString();
	}

	/**
	 * This stream filter converts a stream of bytes to their Base64 encoding.
	 * 
	 * Base64 encoding encodes 3 bytes into 4 characters.
	 * |11111122|22223333|33444444| Each set of 6 bits is encoded according to
	 * the toBase64 map. If the number of input bytes is not a multiple of 3,
	 * then the last group of 4 characters is padded with one or two = signs.
	 * Each output line is at most 76 characters.
	 */
	static class Base64OutputStream extends FilterOutputStream {
		private static char[] toBase64 = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
				'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
				'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4',
				'5', '6', '7', '8', '9', '+', '/' };

		private int col = 0;
		private int i = 0;
		private int[] inbuf = new int[3];

		/**
		 * Constructs the stream filter.
		 * 
		 * @param out
		 *            the stream to filter
		 */
		public Base64OutputStream(OutputStream out) {
			super(out);
		}

		public void write(int c) throws IOException {
			inbuf[i] = c;
			i++;
			if (i == 3) {
				if (col >= 76) {
					super.write('\n');
					col = 0;
				}
				super.write(toBase64[(inbuf[0] & 0xFC) >> 2]);
				super.write(toBase64[((inbuf[0] & 0x03) << 4) | ((inbuf[1] & 0xF0) >> 4)]);
				super.write(toBase64[((inbuf[1] & 0x0F) << 2) | ((inbuf[2] & 0xC0) >> 6)]);
				super.write(toBase64[inbuf[2] & 0x3F]);
				col += 4;
				i = 0;
			}
		}

		public void flush() throws IOException {
			if (i > 0 && col >= 76) {
				super.write('\n');
				col = 0;
			}
			if (i == 1) {
				super.write(toBase64[(inbuf[0] & 0xFC) >> 2]);
				super.write(toBase64[(inbuf[0] & 0x03) << 4]);
				super.write('=');
				super.write('=');
			} else if (i == 2) {
				super.write(toBase64[(inbuf[0] & 0xFC) >> 2]);
				super.write(toBase64[((inbuf[0] & 0x03) << 4) | ((inbuf[1] & 0xF0) >> 4)]);
				super.write(toBase64[(inbuf[1] & 0x0F) << 2]);
				super.write('=');
			}
		}
	}
}
