package com.imeeting.mvc.model.conference.attendee;

public class AttendeeBean {

	private String confId;
	private String userName;
	
	public void setConferenceId(String confId){
		this.confId = confId;
	}
	
	public String getConfId(){
		return confId;
	}
	
	public void setUserName(String userName){
		this.userName = userName;
	}
	
	public String getUserName(){
		return userName;
	}
}
