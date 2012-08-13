package com.richitec.sms.client;

import java.io.UnsupportedEncodingException;

import org.junit.BeforeClass;
import org.junit.Test;

public class SMSClientTest {
	
	private static SMSClient client;
	private static String baseUri = "http://221.179.180.158:9000/QxtSms/QxtFirewall";
	private static String userName = "njftwl";
	private static String password = "ft123456";
	private static String phone = "13382794516";
	
	@BeforeClass
	public static void setUp(){
		client = new SMSClient(baseUri, userName, password);
	}

	@Test
	public void sendMessage() throws UnsupportedEncodingException{
		String message = "1234567890";
		SMSHttpResponse response = client.sendTextMessage(phone, message);
		System.out.println(response.getCode());
	}
}
