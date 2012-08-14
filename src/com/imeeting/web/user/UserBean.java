package com.imeeting.web.user;

public class UserBean {
	public static final String SESSION_BEAN = "userbean";
	
	private String name;
	private String password;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getPassword(){
		return this.password;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
}
