package com.imeeting.mvc.model.conference;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.imeeting.constants.ConferenceConstants;
import com.imeeting.mvc.model.conference.ConferenceDB.ConferenceStatus;
import com.imeeting.mvc.model.conference.attendee.AttendeeBean;


public class ConferenceBean {
	private String id;
	private String title;
	private String status;
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
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
	
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		
		try {
			obj.put(ConferenceConstants.conferenceId.name(), id);
			obj.put(ConferenceConstants.created_time.name(), createdTimeStamp);
			obj.put(ConferenceConstants.schedule_time.name(), scheduledTimeStamp);
			obj.put(ConferenceConstants.title.name(), title);
			obj.put(ConferenceConstants.status.name(), status);
			JSONArray attendeeArray = new JSONArray();
			for (AttendeeBean attendee : attendeeList) {
				attendeeArray.put(attendee.toJSONObject());
			}
			obj.put(ConferenceConstants.attendees.name(), attendeeArray);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return obj;
	}
}
