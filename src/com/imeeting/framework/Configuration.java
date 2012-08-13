package com.imeeting.framework;

/**
 * Manage the configuration of phone conference server, including IP info
 * 
 * @author sk
 * 
 */
public class Configuration {
	
	private String uploadDir;
	private String suite0Id;
	private String suite5Id;
	private String suite10Id;
	
	public void setUploadDir(String uploadDir){
		this.uploadDir = uploadDir;
	}
	
	public String getUploadDir(){
		return this.uploadDir;
	}
	
	public String getSuite0Id(){
		return this.suite0Id;
	}
	
	public void setSuite0Id(String id){
		this.suite0Id = id;
	}
	
	public String getSuite5Id(){
		return this.suite5Id;
	}
	
	public void setSuite5Id(String id){
		this.suite5Id = id;
	}
	
	public String getSuite10Id(){
		return this.suite10Id;
	}
	
	public void setSuite10Id(String id){
		this.suite10Id = id;
	}
	
}
