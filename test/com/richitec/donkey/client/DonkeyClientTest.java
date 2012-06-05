package com.richitec.donkey.client;

import org.junit.BeforeClass;
import org.junit.Test;

public class DonkeyClientTest {
	
	private static DonkeyClient client;
	private static String baseUri = "http://192.168.1.234:8080/donkey/api";
	private static String appId = "26287092";
	private static String appKey = "ud4872uu";
	private static String reqId = "huuguanghui";
	
	@BeforeClass
	public static void setUp(){
		client = new DonkeyClient(baseUri, appId, appKey);
	}

	@Test
	public void createConference(){
		DonkeyHttpResponse response = client.createConference(reqId);
		System.out.println(response.getEntityAsString());
	}
}
