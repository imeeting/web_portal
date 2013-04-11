package com.imeeting.mvc.model.contact;

public class ContactBean {
	
	private Integer id;
	private String nickName;
	private String phone;
	private String email;
	private Integer count;
	
	public void setId(Integer id){
		this.id = id;
	}
	
	public Integer getId(){
		return id;
	}
	
	public void setNickName(String nickName){
		this.nickName = nickName;
	}
	
	public String getNickName(){
		return nickName;
	}
	
	public void setPhone(String phone){
		this.phone = phone;
	}
	
	public String getPhone(){
		return phone;
	}
	
	public void setEmail(String email){
		this.email = email;
	}
	
	public String getEmail(){
		return email;
	}
	
	public void setCount(Integer count){
		this.count = count;
	}
	
	public Integer getCount(){
		return count;
	}
	
	public void incCount(){
		if (null == count){
			count = 1;
		} else {
			count += 1;
		}
	}
	
}
