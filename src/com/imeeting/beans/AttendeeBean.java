package com.imeeting.beans;

import org.json.JSONException;
import org.json.JSONObject;

public class AttendeeBean {

	public enum OnlineStatus {
		online, offline
	}

	public enum TelephoneStatus {
		idle, calling, muting, unmuting, hangingup, intalking, muted, hangedup, callfailed
	}

	public enum VideoStatus {
		on, off
	}

	private String name;
	private OnlineStatus onlineStatus;
	private VideoStatus videoStatus;
	private TelephoneStatus telephoneStatus;

	public AttendeeBean(String name) {
		this(name, OnlineStatus.offline);
	}
	
	public AttendeeBean(String name, OnlineStatus status) {
		this.setName(name);
		this.setOnlineStatus(status);
		this.setVideoStatus(VideoStatus.off);
		this.setTelephoneStatus(TelephoneStatus.idle);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public TelephoneStatus getTelephoneStatus() {
		return telephoneStatus;
	}

	public void setTelephoneStatus(TelephoneStatus telephoneStatus) {
		this.telephoneStatus = telephoneStatus;
	}

	public JSONObject toJson() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("name", name);
			obj.put("online_status", onlineStatus.name());
			obj.put("video_status", videoStatus.name());
			obj.put("telephone_status", telephoneStatus.name());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
}
