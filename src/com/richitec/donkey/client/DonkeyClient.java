package com.richitec.donkey.client;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

public class DonkeyClient {

	private static Log log = LogFactory.getLog(DonkeyClient.class);
	
	private HttpClient httpClient;
	
	private String baseUri;
	private String appId;
	private String appKey;
	
	public DonkeyClient(){
		this.httpClient = new DefaultHttpClient();
	}

	public DonkeyClient(String baseUri, String appId, String appKey) {
		this.baseUri = baseUri;
		this.appId = appId;
		this.appKey = appKey;
		this.httpClient = new DefaultHttpClient();
	}
	
	public void setBaseUri(String baseUri){
		this.baseUri = baseUri;
	}
	
	public void setAppId(String appId){
		this.appId = appId;
	}
	
	public void setAppKey(String appKey){
		this.appKey = appKey;
	}
	
	private String getParamsString(List<String> paramList){
		StringBuffer sb = new StringBuffer();
		for (String s : paramList){
			if (sb.length() > 0){
				sb.append("&");
			} else {
				sb.append("?");
			}
			sb.append(s);
		}
		return sb.toString();
	}
	
	private String getDigest(List<String> paramList){
		Collections.sort(paramList);

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < paramList.size(); i++) {
			sb.append(paramList.get(i));
		}
		// add appKey to StringBuffer sb2
		sb.append(this.appKey);

		String digest = new String();
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
			byte[] md5Value = md5.digest(sb.toString().getBytes());
			digest = Hex.encodeHexString(md5Value);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return digest;
	}
	
	private DonkeyHttpResponse execute(HttpUriRequest req){
		DonkeyHttpResponse donkeyResponse = null;
		try {
			donkeyResponse = this.httpClient.execute(req, new ResponseHandler<DonkeyHttpResponse>() {
				@Override
				public DonkeyHttpResponse handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
					return new DonkeyHttpResponse(response);
				}
			});
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return donkeyResponse;
	}
	
	private DonkeyHttpResponse executeGet(String url, List<String> paramList, String requestId){
		paramList.add("appid=" + this.appId);
		paramList.add("reqid=" + requestId);
		String digest = getDigest(paramList);
		paramList.add("sig=" + digest);
		
		String paramString = getParamsString(paramList);
		HttpGet get = new HttpGet(url + paramString);
		
		DonkeyHttpResponse donkeyResponse = execute(get);
		return donkeyResponse;
	}
	
	public DonkeyHttpResponse createNoControlConference(String confId, String requestId){
		LinkedList<String> paramList = new LinkedList<String>();
		paramList.add("conference=" + confId);
		paramList.add("deleteWhen=nocontrol");
		return executeGet(baseUri+"/create", paramList, requestId);
	}
	
	public DonkeyHttpResponse createNoMediaConference(String confId, String requestId){
		LinkedList<String> paramList = new LinkedList<String>();
		paramList.add("conference=" + confId);
		paramList.add("deleteWhen=nomedia");
		return executeGet(baseUri+"/create", paramList, requestId);
	}	
	
	public DonkeyHttpResponse destroyConference(String confId, String requestId){
		LinkedList<String> paramList = new LinkedList<String>();
		paramList.add("conference=" + confId);
		return executeGet(baseUri+"/destroy", paramList, requestId);
	}
	
	public DonkeyHttpResponse addAttendee(String confId, String sipUri, String requestId){
		LinkedList<String> paramList = new LinkedList<String>();
		paramList.add("conference=" + confId);
		paramList.add("sipuri=" + sipUri);
		return executeGet(baseUri+"/add", paramList, requestId);
	}
	
	public DonkeyHttpResponse callAttendee(String confId, String sipUri, String requestId){
		LinkedList<String> paramList = new LinkedList<String>();
		paramList.add("conference=" + confId);
		paramList.add("sipuri=" + sipUri);
		return executeGet(baseUri+"/call", paramList, requestId);
	}	
	
	public DonkeyHttpResponse hangupAttendee(String confId, String sipUri, String requestId){
		LinkedList<String> paramList = new LinkedList<String>();
		paramList.add("conference=" + confId);
		paramList.add("sipuri=" + sipUri);
		return executeGet(baseUri+"/hangup", paramList, requestId);
	}	
	
	public DonkeyHttpResponse joinConference(String confId, String sipUri, String requestId){
		LinkedList<String> paramList = new LinkedList<String>();
//		paramList.add("m=join");
		paramList.add("conference=" + confId);
		paramList.add("sipuri=" + sipUri);
		return executeGet(baseUri+"/join", paramList, requestId);
	}
	
	public DonkeyHttpResponse unjoinConference(String confId, String sipUri, String requestId){
		LinkedList<String> paramList = new LinkedList<String>();
//		paramList.add("m=unjoin");
		paramList.add("conference=" + confId);
		paramList.add("sipuri=" + sipUri);
		return executeGet(baseUri+ "/unjoin", paramList, requestId);
	}
	
	public DonkeyHttpResponse muteAttendee(String confId, String attendee, String requestId){
		LinkedList<String> paramList = new LinkedList<String>();
//		paramList.add("m=mute");
		paramList.add("conference=" + confId);
		paramList.add("sipuri=" + attendee);
		return executeGet(baseUri + "mute", paramList, requestId);
	}
	
	public DonkeyHttpResponse unmuteAttendee(String confId, String attendee, String requestId){
		LinkedList<String> paramList = new LinkedList<String>();
//		paramList.add("m=unmute");
		paramList.add("conference=" + confId);
		paramList.add("sipuri=" + attendee);
		return executeGet(baseUri+"unmute", paramList, requestId);
	}	
	
	
}
