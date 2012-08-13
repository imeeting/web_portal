package com.richitec.donkey.client;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.richitec.util.TextUtility;

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
	
	public static String generateSipUriFromPhone(String phone){
//		return "sip:0" + phone + "@donkey.com";
		return phone;
	}
	
	public static String getPhoneNumberFromSipUri(String sipUri){
//		String[] s1 = TextUtility.splitText(sipUri, "sip:0", "@donkey.com");
//		String phoneNumber = sipUri;
//		if (s1 != null && s1.length > 0) {
//			phoneNumber = s1[0];
//		}
//		return phoneNumber;
		return sipUri;
	}
	
	private String getParamsString(List<String> paramList){
		StringBuffer sb = new StringBuffer();
		for (String s : paramList){
			if (sb.length() > 0){
				sb.append("&");
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
		HttpGet get = new HttpGet(url + "?" + paramString);
		
		DonkeyHttpResponse donkeyResponse = execute(get);
		return donkeyResponse;
	}
	
	private DonkeyHttpResponse executePost(String url, List<NameValuePair> params, String requestId) {
		params.add(new BasicNameValuePair("appid", appId));
		params.add(new BasicNameValuePair("reqid", requestId));
		HttpEntity entity = new UrlEncodedFormEntity(params, Consts.UTF_8);
		
		HttpPost post = new HttpPost(url);
		post.setEntity(entity);
		
		DonkeyHttpResponse donkeyResponse = execute(post);
		return donkeyResponse;
	}
	
	private String getSIPUriJSONArray(Collection<String> attendeeList){
		if (null != attendeeList && attendeeList.size()>0){
			String attendeeArray = "[";
			for(String attendee : attendeeList){
//				attendeeArray += "\"sip:0" + attendee + "@donkey.com\","; 
				attendeeArray += "\"" + attendee + "\",";
			}
			attendeeArray += "]";
			return attendeeArray;
		}
		return null;
	}
	
	public DonkeyHttpResponse createNoControlConference(
			String confId, String caller, Collection<String> phoneList, String requestId){
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("conference", confId));
		params.add(new BasicNameValuePair("deleteWhen", "nocontrol"));
		String sipuriJSONArray = getSIPUriJSONArray(phoneList);
		if (null != sipuriJSONArray){
			params.add(new BasicNameValuePair("sipuriList", sipuriJSONArray));
		}
		return executePost(baseUri+"/create", params, requestId);
	}
	
	public DonkeyHttpResponse createNoMediaConference(
			String confId, String caller, Collection<String> attendeeList, String requestId){
		log.info("createNoMediaConference - confId:" + confId);
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("conference", confId));
		params.add(new BasicNameValuePair("deleteWhen", "nomedia"));
		String attendeeJSONArray = getSIPUriJSONArray(attendeeList);
		log.info("createNoMediaConference - attendeeJSONArray: " + attendeeJSONArray);
		if (null != attendeeJSONArray){
			params.add(new BasicNameValuePair("sipuriList", attendeeJSONArray));
		}		
		return executePost(baseUri+"/create", params, requestId);
	}	
	
	public DonkeyHttpResponse destroyConference(String confId, String requestId){
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("conference", confId));
		return executePost(baseUri+"/destroy", params, requestId);
	}
	
	public DonkeyHttpResponse addAttendee(String confId, String sipUri, String requestId){
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("conference", confId));
		params.add(new BasicNameValuePair("sipuri", sipUri));
		return executePost(baseUri+"/add", params, requestId);
	}
	
	public DonkeyHttpResponse addMoreAttendee(
			String confId, Collection<String> phoneList, String requestId){
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("conference", confId));
		String sipuriJSONArray = getSIPUriJSONArray(phoneList);
		if (null != sipuriJSONArray){
			params.add(new BasicNameValuePair("sipuriList", sipuriJSONArray));
		}
		return executePost(baseUri+"/addmore", params, requestId);		
	}
	
	public DonkeyHttpResponse callAttendee(String confId, String sipUri, String requestId){
		log.info("callAttendee - confId: " + confId + " sipUri: " + sipUri);
		
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("conference", confId));
		params.add(new BasicNameValuePair("sipuri", sipUri));
		return executePost(baseUri+"/call", params, requestId);
	}	
	
	public DonkeyHttpResponse hangupAttendee(String confId, String sipUri, String requestId){
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("conference", confId));
		params.add(new BasicNameValuePair("sipuri", sipUri));
		return executePost(baseUri+"/hangup", params, requestId);		
	}	
	
	public DonkeyHttpResponse joinConference(String confId, String sipUri, String requestId){
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("conference", confId));
		params.add(new BasicNameValuePair("sipuri", sipUri));
		return executePost(baseUri+"/join", params, requestId);	
	}
	
	public DonkeyHttpResponse unjoinConference(String confId, String sipUri, String requestId){
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("conference", confId));
		params.add(new BasicNameValuePair("sipuri", sipUri));
		return executePost(baseUri+"/unjoin", params, requestId);	
	}
	
	public DonkeyHttpResponse muteAttendee(String confId, String sipUri, String requestId){
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("conference", confId));
		params.add(new BasicNameValuePair("sipuri", sipUri));
		return executePost(baseUri+"/mute", params, requestId);	
	}
	
	public DonkeyHttpResponse unmuteAttendee(String confId, String sipUri, String requestId){
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("conference", confId));
		params.add(new BasicNameValuePair("sipuri", sipUri));
		return executePost(baseUri+"/unmute", params, requestId);	
	}	
	
	
}
