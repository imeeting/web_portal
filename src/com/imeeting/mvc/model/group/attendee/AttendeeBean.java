package com.imeeting.mvc.model.group.attendee;

import org.json.JSONException;
import org.json.JSONObject;

public class AttendeeBean {

	public enum OnlineStatus {
		online, offline
	}

	public enum TelephoneStatus {
		initil, calling, muting, unmuting, hangingup, intalking, muted, hangedup, callfailed
	}
	
	public enum PhoneCallStatus {
		CallWait, Established, TermWait, Failed, Terminated
	}

	public enum VideoStatus {
		on, off
	}

	private String username;
	private OnlineStatus onlineStatus;
	private VideoStatus videoStatus;
	private PhoneCallStatus phoneCallStatus;

	public AttendeeBean(String name) {
		this(name, OnlineStatus.offline);
	}
	
	public AttendeeBean(String userName, OnlineStatus status) {
		this.username = userName;
		this.onlineStatus = status;
		this.videoStatus = VideoStatus.off;
		this.phoneCallStatus = PhoneCallStatus.Terminated;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String name) {
		this.username = name;
	}

	public OnlineStatus getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(OnlineStatus onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	public VideoStatus getVideoStatus() {
		return videoStatus;
	}

	public void setVideoStatus(VideoStatus videoStatus) {
		this.videoStatus = videoStatus;
	}
	
	public PhoneCallStatus getPhoneCallStatus(){
		return phoneCallStatus;
	}
	
	public boolean statusCall(){
		synchronized (phoneCallStatus) {
			if (PhoneCallStatus.Terminated.equals(phoneCallStatus) ||
				PhoneCallStatus.Failed.equals(phoneCallStatus)){
				phoneCallStatus = PhoneCallStatus.CallWait;
				return true;
			} else {
				return false;
			}
		}
	}
	
	public boolean statusHangup() {
		synchronized (phoneCallStatus) {
			if (PhoneCallStatus.CallWait.equals(phoneCallStatus) ||
				PhoneCallStatus.Established.equals(phoneCallStatus)){
				phoneCallStatus = PhoneCallStatus.TermWait;
				return true;
			} else {
				return false;
			}
		}
	}
	
	public boolean statusCallEstablished(){
		synchronized (phoneCallStatus) {
			if (PhoneCallStatus.CallWait.equals(phoneCallStatus)){
				phoneCallStatus = PhoneCallStatus.Established;
				return true;
			} else {
				return false;
			}
		}
	}
	
	public boolean statusCallFailed(){
		synchronized (phoneCallStatus) {
			if (PhoneCallStatus.CallWait.equals(phoneCallStatus)){
				phoneCallStatus = PhoneCallStatus.Failed;
				return true;
			} else {
				return false;
			}
		}
	}
	
	public boolean statusCallTerminated(){
		synchronized (phoneCallStatus) {
			if (PhoneCallStatus.CallWait.equals(phoneCallStatus) ||
				PhoneCallStatus.TermWait.equals(phoneCallStatus) ||
				PhoneCallStatus.Established.equals(phoneCallStatus) ||
				PhoneCallStatus.Failed.equals(phoneCallStatus)	){
				phoneCallStatus = PhoneCallStatus.Terminated;
				return true;
			} else {
				return false;
			}
		}
	}

	public JSONObject toJson() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("username", username);
			obj.put("online_status", onlineStatus.name());
			obj.put("video_status", videoStatus.name());
			obj.put("telephone_status", phoneCallStatus.name());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
}
