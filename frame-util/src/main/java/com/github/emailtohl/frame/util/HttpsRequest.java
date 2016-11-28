package com.github.emailtohl.frame.util;

import java.io.IOException;
import java.net.Proxy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
/**
 * 继承于WebAccess，实现HTTPS的提交
 * 不过本类并未对远程服务器进行验证，如需验证还需要checkServerTrusted进行判断
 * 
 * @author helei
 */
public class HttpsRequest extends WebAccess {
	private static Logger logger = Logger.getLogger(HttpsRequest.class.getName());
	/**
	 * 负责管理用于验证到同位体的本地 SSLSocket 的密钥内容
	 */
	private KeyManager[] keyManagers = null;
	/**
	 * 默认的KeyManager[]不做任何检查，不符合安全要求
	 */
	private TrustManager[] trustManagers;
	/**
	 * host校验
	 */
	private HostnameVerifier NoHostnameVerifier = new NoHostnameVerifier();

	public HttpsRequest() {
		super();
		trustManagers = new TrustManager[] { new AcceptsUntrustedCerts() };
	}

	public HttpsRequest(Proxy proxy) {
		super(proxy);
		trustManagers = new TrustManager[] { new AcceptsUntrustedCerts() };
	}

	/**
	 * @param httpsUrl 传入应该是一个https协议的地址，执行GET请求
	 * @return Response 包含响应头和响应体以及响应状态码，若未获取响应状态码，则该值为0
	 * @throws IOException 通信出现异常，需要客户端程序处理
	 */
	@Override
	public Response get(String httpsUrl) throws IOException {
		validHttps(httpsUrl);
		SSLSocketFactory ssf = getSSLSocketFactory();
		HttpsURLConnection conn = (HttpsURLConnection) getURLConnection(httpsUrl);
		conn.setHostnameVerifier(NoHostnameVerifier);
		conn.setSSLSocketFactory(ssf);
		conn.setRequestMethod("GET");
		return super.get(conn);
	}

	/**
	 * @param httpsUrl 传入应该是一个https协议的地址，执行GET请求
	 * @return httpsUrl 响应体
	 * @throws IOException 通信出现异常，需要客户端程序处理
	 */
	@Override
	public String doGet(String httpsUrl) throws IOException {
		return this.get(httpsUrl).getBody();
	}

	/**
	 * @param httpsUrl 传入应该是一个https协议的地址，执行POST请求
	 * @param requestBody post的请求体
	 * @return Response 包含响应头和响应体以及响应状态码，若未获取响应状态码，则该值为0
	 * @throws IOException 通信出现异常，需要客户端程序处理
	 */
	@Override
	public Response post(String httpsUrl, String requestBody) throws IOException {
		validHttps(httpsUrl);
		SSLSocketFactory ssf = getSSLSocketFactory();
		HttpsURLConnection conn = (HttpsURLConnection) getURLConnection(httpsUrl);
		conn.setHostnameVerifier(NoHostnameVerifier);
		conn.setSSLSocketFactory(ssf);
		conn.setRequestMethod("POST");
		return super.post(conn, requestBody);
	}

	/**
	 * @param httpsUrl 传入应该是一个https协议的地址，执行GET请求
	 * @param requestBody post的请求体
	 * @return Response 包含响应头和响应体以及响应状态码，若未获取响应状态码，则该值为0
	 * @throws IOException 通信出现异常，需要客户端程序处理
	 */
	public String doPost(String httpsUrl, String requestBody) throws IOException {
		return this.post(httpsUrl, requestBody).getBody();
	}

	/**
	 * @param httpsUrl 传入应该是一个https协议的地址，执行GET请求
	 * @param nameValuePairs 以map形式传入的请求参数
	 * @return Response 包含响应头和响应体以及响应状态码，若未获取响应状态码，则该值为0
	 * @throws IOException 通信出现异常，需要客户端程序处理
	 */
	@Override
	public String doPost(String httpsUrl, Map<String, String> nameValuePairs) throws IOException {
		String requestBody = getUrlParams(nameValuePairs);
		return this.doPost(httpsUrl, requestBody);
	}

	public KeyManager[] getKeyManagers() {
		return keyManagers;
	}

	public void setKeyManagers(KeyManager[] keyManagers) {
		this.keyManagers = keyManagers;
	}

	public TrustManager[] getTrustManagers() {
		return trustManagers;
	}

	public void setTrustManagers(TrustManager[] trustManagers) {
		this.trustManagers = trustManagers;
	}

	private SSLSocketFactory getSSLSocketFactory() {
		try {
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(keyManagers, trustManagers, new SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			return ssf;
		} catch (KeyManagementException | NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void validHttps(String url) {
		String protocol = url.split(":")[0].trim();
		if (!"https".equalsIgnoreCase(protocol)) {
			throw new IllegalArgumentException("不是https协议地址");
		}
	}

	private class NoHostnameVerifier implements HostnameVerifier {
		/**
		 * 验证host
		 * @param hostname
		 * @param session
		 * @return
		 */
		@Override
		public boolean verify(String hostname, SSLSession session) {
			logger.severe("不验证host：");
			logger.severe("hostname: " + hostname);
			logger.severe("SSLSession: " + session.toString());
			return true;
		}
		
	}
	
	private class AcceptsUntrustedCerts implements X509TrustManager {
		/**
		 * 检查客户端证书
		 */
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			logger.severe("客户端证书未做校验：");
			logger.severe(Arrays.deepToString(chain));
			logger.severe("认证类型：" + authType);
		}

		/**
		 * 检查服务器端证书
		 */
		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			logger.severe("服务器端证书未做校验：");
			logger.severe(Arrays.deepToString(chain));
			logger.severe("认证类型：" + authType);
		}

		/**
		 * 返回受信任的X509证书数组
		 */
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			logger.severe("返回受信任的X509证书数组是null");
			return null;
		}
	}
}