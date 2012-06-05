package com.richitec.sms.client;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SMSHttpResponse {
	
	private HttpResponse httpResponse;
	private Integer code = null;
	
	public SMSHttpResponse(HttpResponse response){
		this.httpResponse = response;
		
		HttpEntity entity = httpResponse.getEntity();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(entity.getContent());
			NodeList list = document.getElementsByTagName("code");
			this.code = Integer.parseInt(list.item(0).getFirstChild().getNodeValue());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getStatusCode(){
		return httpResponse.getStatusLine().getStatusCode();
	}
	
	public int getCode(){
		return this.code;
	}
	
	public Boolean isSuccess(){
		return this.code == 3;
	}
}
