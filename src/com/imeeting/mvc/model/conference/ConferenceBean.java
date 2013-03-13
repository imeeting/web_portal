package com.imeeting.mvc.model.conference;

import java.util.ArrayList;
import java.util.List;

import com.imeeting.mvc.model.conference.attendee.AttendeeBean;


public class ConferenceBean {
	private String id;
	private String title;
	private Long createdTimeStamp;
	private Long scheduledTimeStamp;
	private List<AttendeeBean> attendeeList;
	
	public ConferenceBean(){
		attendeeList = new ArrayList<AttendeeBean>();
	}

	public void setId(String id){
		this.id = id;
	}
	
	public String getId(){
		return id;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getTitle(){
		return title;
	}
	
	public void setCreatedTimeStamp(Long timestamp){
		createdTimeStamp = timestamp;
	}
	
	public Long getCreatedTimeStamp(){
		return createdTimeStamp;
	}
	
	public void setScheduledTimeStamp(Long timestamp){
		scheduledTimeStamp = timestamp;
	}
	
	public Long getScheduledTimeStamp(){
		return scheduledTimeStamp;
	}
	
	public void addAttendee(AttendeeBean a){
		attendeeList.add(a);
	}
	
	public List<AttendeeBean> getAttendeeList(){
		return attendeeList;
	}
}
