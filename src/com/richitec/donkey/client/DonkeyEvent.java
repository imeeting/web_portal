package com.richitec.donkey.client;

import org.json.JSONException;
import org.json.JSONObject;

public class DonkeyEvent {
	
	private static final String appId = "appId";
	private static final String reqId = "reqId";
	private static final String confId = "conference";
	private static final String status = "status";
	private static final String state = "state";
	private static final String type = "type";
	private static final String sipUri = "sipUri";
	private static final String method = "method";
	
	private JSONObject json;
	
	public DonkeyEvent(String jsonString) throws JSONException{
		json = new JSONObject(jsonString);
	}
	
	private String getString(String name){
		String r = null;
		try {
			r = json.getString(name);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return r;
	}
	
	public String getAppId(){
		return getString(appId);
	}
	
	public String getRequestId(){
		return getString(reqId);
	}
	
	public Integer getStatusCode() {
		Integer i = null;
		try {
			i = json.getInt(status);
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return i;
	}
	
	public String getConferenceId() {
		return getString(confId);
	}
	
	public String getConferenceState() {
		return getString(state);
	}
	
	public String getAttendeeState() {
		return getString(state);
	}
	
	public String getSipUri(){
		return getString(sipUri);
	}
	
	public String getMethod() {
		return getString(method);
	}
	
	public String getEventType() {
		return getString(type);
	}
}
