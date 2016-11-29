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
import java.util.logging.Level;
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
		SSLSocketFactory ssf = null;
		try {
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(keyManagers, trustManagers, new SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			ssf = sslContext.getSocketFactory();
		} catch (KeyManagementException | NoSuchAlgorithmException | NoSuchProviderException e) {
			logger.log(Level.SEVERE, "不会出现该异常", e);
		}
		return ssf;
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
	
	/**
	 * 有效的证书需要由权威机构CA签名，CA会用自己的私钥来生成数字签名。这个权威机构CA是可以被客户端完全信任的，客户端浏览器会安装CA的根证书，
	 * 由CA签名的证书是被CA所信任的，这就构成了信任链，所以客户端可以信任该服务器的证书。
	 * 
	 * 客户端与服务器建立ssl连接时，服务器将自身的证书传输给客户端，客户端在验证证书的时候，先看CA的根证书是否在自己的信任根证书列表中。
	 * 再用CA的根证书提供的公钥来验证服务器证书中的数字签名，如果公钥可以解开签名，证明该证书确实被CA所信任。再看证书是否过期，
	 * 访问的网站域名与证书绑定的域名是否一致。这些都通过，说明证书可以信任。
	 * 
	 * 接下来使用服务器证书里面的公钥进行服务器身份的验证。 客户端生成一个随机数给到服务器。 服务器对随机数进行签名，并回传给到客户端。
	 * 客户端用服务器证书的公钥对随机数的签名进行验证，若验证通过，则说明对应的服务器确实拥有对应服务器证书的私钥，因此判断服务器的身份正常。否则，
	 * 则任务服务器身份被伪造。这些都没问题才说明服务器是可信的。
	 * 
	 * 接下来客户端会生成会话密钥，使用服务器公钥加密。服务器用自己的私钥解密后，用会话密钥加密数据进行传输。ssl连接就建立了。
	 * 
	 */
	private class AcceptsUntrustedCerts implements X509TrustManager {
		/**
		 * 检查客户端证书
		 */
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			logger.severe(Arrays.deepToString(chain));
			logger.severe("认证类型：" + authType);
			logger.severe("客户端证书未做校验");
		}

		/**
		 * 检查服务器端证书，若是tomcat容器，则对应server.xml <Connector SSLEnabled="true" keystoreFile>标签中keystoreFile属性
		 */
		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			logger.severe(Arrays.deepToString(chain));
			logger.severe("认证类型：" + authType);
			logger.severe("服务器端证书未做校验");
		}

		/**
		 * 返回受信任的X509证书数组，若是tomcat容器，则对应server.xml <Connector SSLEnabled="true" truststoreFile>标签中truststoreFile属性
		 */
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			logger.severe("返回受信任的X509证书数组是null");
			return null;
		}
	}
}