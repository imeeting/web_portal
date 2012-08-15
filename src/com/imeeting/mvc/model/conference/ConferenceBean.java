package com.imeeting.mvc.model.conference;


public class ConferenceBean {
	private String id;
	private String title;
	private Long createdTimeStamp;

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
}
