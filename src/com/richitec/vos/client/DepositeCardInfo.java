package com.richitec.vos.client;

public class DepositeCardInfo {
	
	private Double value;
	private String account;
	private String balance;
	
	public DepositeCardInfo(String vosResponseInfo){
		String [] list = vosResponseInfo.split(";");
		if (null == list) return;
		if (list.length > 0) value = new Double(list[0]);
		if (list.length > 2) account = list[2];
		if (list.length > 3) balance = list[3];
	}
	
	public Double getValue(){
		return value;
	}
	
	public String getAccountName(){
		return account;
	}
	
	public String getBalance(){
		return balance;
	}
}
