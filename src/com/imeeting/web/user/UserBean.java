package com.imeeting.web.user;

public class UserBean {
	public static final String SESSION_BEAN = "userbean";
	private String userId;
	private String username;
	private String nickname;
	private String userKey;
	private String password;
	

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return username;
	}

	public void setUserName(String name) {
		this.username = name;
	}
	
	public String getNickName(){
		return nickname;
	}
	
	public void setNickName(String nickName){
		this.nickname = nickName;
	}
	
	public String getDisplayName(){
		if (nickname == null || nickname.length()<=0){
			return username; 
		} else {
			return nickname;
		}
	}
	
	public String getPassword(){
		return this.password;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	
	public String getUserKey(){
		return userKey;
	}
	
	public void setUserKey(String userKey){
		this.userKey = userKey;
	}
}
