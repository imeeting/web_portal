package com.imeeting.mvc.model.addressbook;

import com.mongodb.DB;

public class AddressBookDAO {
	private DB db;
	
	public AddressBookDAO() {
		
	}

	public DB getDb() {
		return db;
	}

	public void setDb(DB db) {
		this.db = db;
	}


}
