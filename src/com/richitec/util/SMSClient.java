package com.richitec.util;

import java.io.IOException;
import java.net.URLEncoder;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import com.richitec.util.ConfigManager;

//TODO: need a HTTP connection pool
public class SMSClient {
	private static Log log = LogFactory.getLog(SMSClient.class);
	private static SMSClient _instance;

	private String baseURI;

	private SMSClient() {
		baseURI = "http://" + ConfigManager.getInstance().getTextMessageUrl();
	}

	public static SMSClient getInstance() {
		if (null == _instance) {
			_instance = new SMSClient();
		}
		return _instance;
	}

	private TextMessageResponse execute(HttpMethod method) throws IOException,
			ParserConfigurationException, SAXException {
		TextMessageResponse response = null;
		HttpClient httpClient = new HttpClient();
		httpClient.executeMethod(method);
		response = new TextMessageResponse(method.getStatusCode(),
				method.getResponseBodyAsStream());
		method.releaseConnection();
		return response;
	}

	public TextMessageResponse sendTextMessage(String phone, String content)
			throws IOException, ParserConfigurationException,
			SAXException {
		String contentStr = URLEncoder.encode(content, "GBK");
		GetMethod get = new GetMethod(baseURI + "/QxtFirewall");
		StringBuffer stm = new StringBuffer();
		String operID = ConfigManager.getInstance().getTextMessageUserName();
		String operPass = ConfigManager.getInstance().getTextMessagePwd();
		stm.append("&OperID=" + operID + "&OperPass=" + operPass
				+ "&SendTime=&ValidTime=&AppendID=&DesMobile=" + phone
				+ "&Content=" + contentStr + "&ContentType=15");
		get.setQueryString(stm.toString());

		return execute(get);
	}
	
	public void sendValidateCode(String phone, String validateCode) throws IOException, ParserConfigurationException, SAXException{
		String content = "验证码：" + validateCode + " [联�?飞影]";
		TextMessageResponse res = sendTextMessage(phone, content);
		log.info("sms gateway return: " + res.getCode());
	}
}
