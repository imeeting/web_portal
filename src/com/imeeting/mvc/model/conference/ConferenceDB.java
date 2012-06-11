package com.imeeting.mvc.model.conference;

import java.sql.SQLException;

import com.richitec.db.DBHelper;

public class ConferenceDB {
	
	public enum ConfStatus {OPEN, CLOSE}; 
	public enum UserConfStatus {VISIABLE, HIDDEN};
	
	public static int insert(String confId, String owner) throws SQLException{
		String sql = "INSERT INTO im_conference(confId, owner) VALUES (?, ?)";
		Object [] params = new Object [] {confId, owner};
		return DBHelper.getInstance().update(sql, params);
	}
	
	public static int close(String confId) throws SQLException {
		String sql = "UPDATE im_conference set status = ? WHERE confId = ?";
		Object [] params = new Object [] {ConfStatus.CLOSE, confId};
		return DBHelper.getInstance().update(sql, params);
	}

	public static void geConferenceList(String userName){
		String sql = "SELECT confId, username from im_conference WHERE username = ? AND status = ?";
		Object [] params = new Object [] {userName, UserConfStatus.VISIABLE};
		//TODO:
	}
	
	public static int hiddenConference(String confId, String userName) throws SQLException{
		String sql = "UPDATE im_attendee set status = ? WHERE confId = ? AND username = ?";
		Object [] params = new Object [] {UserConfStatus.HIDDEN, confId, userName};
		return DBHelper.getInstance().update(sql, params);		
	}
}
