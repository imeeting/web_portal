package com.richitec.vos.client;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;


public class VOSHttpResponse {
	
	private HttpResponse httpResponse;
	private byte [] entityBytes;
	
	private Integer httpStatusCode;
	private Integer vosStatusCode;
	private String vosResponseInfo;
	
	public VOSHttpResponse(HttpResponse response){
		httpResponse = response;
		
		httpStatusCode = response.getStatusLine().getStatusCode();
		
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
		
		if (httpStatusCode == 200){
			parseVOSResponse();
		}
	}
	
	private void parseVOSResponse(){
		String str = new String(entityBytes).trim();
		String [] list = str.split("\\|");
		if (null == list || list.length == 0){
			return;
		}
		
		this.vosStatusCode = new Integer(list[0]);
		
		if (list.length > 1){
			this.vosResponseInfo = list[1];
		} else {
			this.vosResponseInfo = "";
		}
	}
	
	public int getHttpStatusCode(){
		return httpStatusCode;
	}
	
	public int getVOSStatusCode(){
		return this.vosStatusCode;
	}
	
	public boolean isOperationSuccess(){
		return 0 == this.vosStatusCode;
	}
	
	public String getVOSResponseInfo(){
		return this.vosResponseInfo;
	}

}
