package com.richitec.vos.client;

public class AccountInfo {
	
	private String accountID;
	private String accountName;
	private Double balance;
	private String expireTime;
	private Double overdraft;

	public AccountInfo(String vosResponseInfo){
		String [] list = vosResponseInfo.split(";");
		if (null == list) return;
		if (list.length > 0) accountID = list[0];
		if (list.length > 1) accountName = list[1];
		if (list.length > 2) balance = new Double(list[2]);
		if (list.length > 3) expireTime = list[3];
		if (list.length > 4) overdraft = new Double(list[4]);
	}
	
	public String getAccountID(){
		return accountID;
	}
	
	public String getAccountName(){
		return accountName;
	}
	
	public Double getBalance(){
		return balance;
	}
	
	public String getExpireTime(){
		return expireTime;
	}
	
	public Double getOverdraft(){
		return overdraft;
	}
}
