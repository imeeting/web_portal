package com.richitec.vos.client;

public class CurrentSuiteInfo {
	
	private String suiteId;
	private String suiteName;
	private String orderId;
	private Double giftBalance = 0.0;

	public CurrentSuiteInfo(String vosResponseInfo){
		String [] list = vosResponseInfo.split(";");
		if (null == list) return;
		if (list.length > 0) orderId = list[0];
		if (list.length > 1) suiteName = list[1];
		if (list.length > 6) giftBalance = new Double(list[6]);
		if (list.length > 7) suiteId = list[7];
	}
	
	public String getSuiteId(){
		return suiteId;
	}
	
	public String getSuiteName(){
		return suiteName;
	}
	
	public Double getGiftBalance(){
		return giftBalance;
	}
	
	public String getOrderId(){
		return orderId;
	}
}
