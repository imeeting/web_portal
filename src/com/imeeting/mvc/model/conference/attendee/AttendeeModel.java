package com.imeeting.mvc.model.conference.attendee;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.imeeting.constants.AttendeeConstants;

public class AttendeeModel {
	private static Log log = LogFactory.getLog(AttendeeModel.class);

	public enum OnlineStatus {
		online, offline
	}

//	public enum PhoneCallStatus {
//		CallWait, Established, TermWait, Failed, Terminated
//	}
//
//	public enum VideoStatus {
//		on, off
//	}
	
	private String username;
	private String phone;
	private String nickname;
	private OnlineStatus onlineStatus;
//	private Integer joinCount = 0;
//	private Boolean isKickout = false;
//	private Long lastestHBTimeMillis;

	public AttendeeModel(String userName) {
		this(userName, OnlineStatus.offline);
	}

	public AttendeeModel(String userName, OnlineStatus status) {
		this.username = userName;
		this.nickname = "";
		this.phone = "";
		this.onlineStatus = status;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String name) {
		this.username = name;
	}
	
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDisplayName(){
		if (nickname != null && nickname.length()>0){
			return nickname;
		} else {
			return username;
		}
	}

//	public boolean isJoined(){
//		return joinCount > 0;
//	}
//	
//	public int join(){
//		onlineStatus = OnlineStatus.online;
//		return ++joinCount;
//	}
//	
//	public int unjoin(){
//		joinCount -= 1;
//		if (joinCount <= 0){
//			onlineStatus = OnlineStatus.offline;
//		}
//		return joinCount;
//	}
	
	public boolean isOnline(){
		return onlineStatus.equals(OnlineStatus.online);
	}

	public OnlineStatus getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(OnlineStatus onlineStatus) {
		this.onlineStatus = onlineStatus;
	}
	
//	public void heartBeat(){
//		this.lastestHBTimeMillis = System.currentTimeMillis();
//	}
//	
//	public Long getLastHBTimeMillis(){	
//		return lastestHBTimeMillis;
//	}

	public void statusHangup() {
		log.info("statusHangup");
		setOnlineStatus(OnlineStatus.offline);
	}

	public void statusCallEstablished() {
		log.info("statusCallEstablished");
		setOnlineStatus(OnlineStatus.online);
	}

	public void statusCallFailed() {
		log.info("statusCallFailed");
		setOnlineStatus(OnlineStatus.online);
	}

	public void statusCallTerminated() {
		log.info("statusCallTerminated");
		setOnlineStatus(OnlineStatus.online);
	}

	public JSONObject toJson() {
		JSONObject obj = new JSONObject();
		try {
			obj.put(AttendeeConstants.nickname.name(), nickname);
			obj.put(AttendeeConstants.phone.name(), phone);
			obj.put(AttendeeConstants.online_status.name(), getOnlineStatus().name());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}

}
