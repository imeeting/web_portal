package com.richitec.donkey.client;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

public class DonkeyHttpResponse {
	
	private HttpResponse httpResponse;
	private byte [] entityBytes;
	
	public DonkeyHttpResponse(HttpResponse response){
		this.httpResponse = response;
		
		HttpEntity entity = httpResponse.getEntity();
		int length = (int)entity.getContentLength();
		entityBytes = new byte[length];
		try {
			InputStream is = entity.getContent();
			is.read(entityBytes, 0, length);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Boolean isAccepted(){
		return getStatusCode() == 202;
	}
	
	public int getStatusCode(){
		return httpResponse.getStatusLine().getStatusCode();
	}
	
	public byte [] getEntityAsBytes(){
		return entityBytes;
	}
	
	public String getEntityAsString(){
		return new String(entityBytes);
	}
	
	public HttpResponse getHttpResponse(){
		return httpResponse;
	}
}
