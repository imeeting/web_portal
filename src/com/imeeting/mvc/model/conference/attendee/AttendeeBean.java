package com.imeeting.mvc.model.conference.attendee;

public class AttendeeBean {

	private String confId;
	private String userName;
	private String nickName;
	
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
	
	public void setNickName(String nickName){
		this.nickName = nickName;
	}
	
	public String getNickName(){
		return nickName;
	}
	
	public String getDisplayName(){
		if (nickName != null && nickName.length()>0){
			return nickName;
		} else {
			return userName;
		}
	}
}
