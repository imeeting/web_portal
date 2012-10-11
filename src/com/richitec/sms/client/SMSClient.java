package com.richitec.sms.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class SMSClient {
	
	private static Log log = LogFactory.getLog(SMSClient.class);
	
	private HttpClient httpClient;

	private String baseUri;
	private String userName;
	private String password;
	private PoolingClientConnectionManager connManager;
	
	public SMSClient(){
		initHttpClient();
	}

	public SMSClient(String baseUri, String userName, String password) {
		this.baseUri = baseUri;
		this.userName = userName;
		this.password = password;
		initHttpClient();
	}
	
	private void initHttpClient(){
		connManager = new PoolingClientConnectionManager();
		connManager.setMaxTotal(10);
		connManager.setDefaultMaxPerRoute(10);
		
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setSoTimeout(httpParameters, 10000);
		HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
		
		httpClient = new DefaultHttpClient(connManager, httpParameters);
	}
	
	public void setBaseUri(String baseUri){
		this.baseUri = baseUri;
	}
	
	public void setUserName(String userName){
		this.userName = userName;
	}
	
	public void setPassword(String password){
		this.password = password;
	}

	public SMSHttpResponse sendTextMessage(String phone, String content) throws UnsupportedEncodingException{
		String contentStr = URLEncoder.encode(content, "GBK");
		StringBuffer stm = new StringBuffer();
		stm.append("?OperID=" + this.userName + 
				"&OperPass=" + this.password + 
				"&SendTime=&ValidTime=&AppendID=&DesMobile=" + phone + 
				"&Content=" + contentStr + "&ContentType=15");
		
		HttpGet get = new HttpGet(this.baseUri + stm.toString());
		HttpContext context = new BasicHttpContext();
		SMSHttpResponse smsResponse = null;
		try {
			smsResponse = this.httpClient.execute(get, new ResponseHandler<SMSHttpResponse>(){
				@Override
				public SMSHttpResponse handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
					return new SMSHttpResponse(response);
				}
			}, context);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (smsResponse != null && smsResponse.getHttpResponse() != null){
					EntityUtils.consume(smsResponse.getHttpResponse().getEntity());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return smsResponse;
	}
}
