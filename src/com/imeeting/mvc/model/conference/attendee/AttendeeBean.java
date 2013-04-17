package com.imeeting.mvc.model.conference.attendee;

import org.json.JSONException;
import org.json.JSONObject;

import com.imeeting.constants.AttendeeConstants;
import com.imeeting.mvc.model.conference.attendee.AttendeeModel.OnlineStatus;

public class AttendeeBean {

	private String confId;
	private String nickName;
	private String phone;
	private String email;
	
	public void setConferenceId(String confId){
		this.confId = confId;
	}
	
	public String getConfId(){
		return confId;
	}
	
	public void setNickName(String nickName){
		this.nickName = nickName;
	}
	
	public String getNickName(){
		return nickName;
	}
	
	public void setPhone(String phone){
		this.phone = phone;
	}
	
	public String getPhone(){
		return phone;
	}
	
	public void setEmail(String email){
		this.email = email;
	}
	
	public String getEmail(){
		return email;
	}
	
	public String getDisplayName(){
		if (nickName != null && nickName.length()>0){
			return nickName;
		} else 
		if (email != null && email.length()>0){
			return email;
		} else {
			return phone;
		}
	}	
	
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put(AttendeeConstants.nickname.name(), nickName);
			obj.put(AttendeeConstants.phone.name(), phone);
			obj.put(AttendeeConstants.online_status.name(), OnlineStatus.offline.name());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return obj;
	}
}
