package com.imeeting.framework;

/**
 * Manage the configuration of phone conference server, including IP info
 * 
 * @author sk
 * 
 */
public class Configuration {
	
	private String uploadDir;
	
	public void setUploadDir(String uploadDir){
		this.uploadDir = uploadDir;
	}
	
	public String getUploadDir(){
		return this.uploadDir;
	}
	
}
