package com.imeeting.mvc.model.group;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sun.util.logging.resources.logging;

import com.imeeting.mvc.model.group.message.CreateAudioConferenceMsg;
import com.richitec.db.DBHelper;

public class GroupDB {
	private static Log log = LogFactory.getLog(GroupDB.class);
	
	public enum ConfStatus {
		OPEN, CLOSE
	};

	public enum UserConfStatus {
		VISIABLE, HIDDEN
	};

	public static int insert(String groupId, String owner) throws SQLException {
		String sql = "INSERT INTO im_group(groupId, owner) VALUES (?, ?)";
		Object[] params = new Object[] { groupId, owner };
		return DBHelper.getInstance().update(sql, params);
	}

	public static int close(String groupId) throws SQLException {
		String sql = "UPDATE im_group set status = ? WHERE groupId = ?";
		Object[] params = new Object[] { ConfStatus.CLOSE, groupId };
		return DBHelper.getInstance().update(sql, params);
	}

	public static int getGroupTotalCount(String username) throws SQLException {
		// query the total count of conference list related to username
		String sql = "SELECT count(c.groupId) "
				+ "FROM im_group AS c INNER JOIN im_attendee AS a "
				+ "ON c.groupId = a.groupId AND a.username = ? AND a.status = ? "
				+ "ORDER BY c.created DESC";
		Object[] params = new Object[] { username, UserConfStatus.VISIABLE.name() };
		return DBHelper.getInstance().count(sql, params);
	}

	public static JSONArray geGroupList(String userName, int offset,
			int pageSize) throws SQLException {
		// query conference list related to username
		String sql = "SELECT c.groupId AS id, c.owner, UNIX_TIMESTAMP(c.created) AS created, c.status "
				+ "FROM im_group AS c INNER JOIN im_attendee AS a "
				+ "ON c.groupId = a.groupId AND a.username = ? AND a.status = ? "
				+ "ORDER BY c.created DESC";
		Object[] params = new Object[] { userName, UserConfStatus.VISIABLE.name() };

		List<Map<String, Object>> groupResultList = DBHelper.getInstance()
				.queryPager(sql, params, offset, pageSize);
		log.info("groupResultList size: " + groupResultList.size());
		
		if (groupResultList.size() <= 0) {
			return new JSONArray();
		}
		
		StringBuffer groupIds = new StringBuffer();
		groupIds.append('(');

		HashMap<String, JSONObject> groupInfoMap = new HashMap<String, JSONObject>();

		for (Map<String, Object> groupMap : groupResultList) {
			String groupId = (String) groupMap.get("id");
			String owner = (String) groupMap.get("owner");
			Long createdTime = (Long) groupMap.get("created");
			String status = (String) groupMap.get("status");

			log.info("groupId: " + groupId);
			log.info("owner: " + owner);
			log.info("created time: " + createdTime.longValue());
			log.info("status: " + status);
			
			
			JSONObject group = new JSONObject();
			try {
				group.put("groupId", groupId);
				group.put("owner", owner);
				group.put("created_time", createdTime);
				group.put("status", status);
				JSONArray attendees = new JSONArray();
				group.put("attendees", attendees);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			groupInfoMap.put(groupId, group);

			groupIds.append("'" + groupId + "'").append(',');
		}
		if (groupIds.lastIndexOf(",") == groupIds.length() - 1) {
			groupIds.deleteCharAt(groupIds.length() - 1);
		}
		groupIds.append(')');

		// query the attendees in each conference
		sql = "SELECT groupId AS id, username " + "FROM im_attendee "
				+ "WHERE groupId IN " + groupIds.toString();
		log.info(sql);
		List<Map<String, Object>> attendeeList = DBHelper.getInstance().query(
				sql);

		for (Map<String, Object> attendeeMap : attendeeList) {
			String groupId = (String) attendeeMap.get("id");
			String attendee = (String) attendeeMap.get("username");

			log.info("groupId: " + groupId);
			log.info("attendee: " + attendee);
			
			JSONObject group = groupInfoMap.get(groupId);
			try {
				JSONArray attendees = group.getJSONArray("attendees");
				attendees.put(attendee);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		// convert group info map to json array
		JSONArray groupJSONArray = new JSONArray();
		for (Map<String, Object> groupInfo : groupResultList) {
			String groupId = (String) groupInfo.get("id");
			JSONObject conf = groupInfoMap.get(groupId);
			groupJSONArray.put(conf);
		}
		return groupJSONArray;
	}

	public static int hideGroup(String groupId, String userName)
			throws SQLException {
		String sql = "UPDATE im_attendee set status = ? WHERE groupId = ? AND username = ?";
		Object[] params = new Object[] { UserConfStatus.HIDDEN, groupId,
				userName };
		return DBHelper.getInstance().update(sql, params);
	}

	public static void insertAttendees(String groupId, JSONArray attendees)
			throws SQLException {
		String sql = "INSERT INTO im_attendee(groupId, username) VALUES(?,?)";
		List<Object[]> params = new ArrayList<Object[]>();
		for (int i = 0; i < attendees.length(); i++) {
			try {
				String attendee = attendees.getString(i);
				params.add(new Object[] { groupId, attendee });
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		DBHelper.getInstance().batchUpdate(sql, params);
	}

	public static void insertAttendee(String groupId, String userName)
			throws SQLException {
		String sql = "INSERT INTO im_attendee(groupId, username) VALUES(?,?)";
		Object[] params = new Object[] { groupId, userName };
		DBHelper.getInstance().update(sql, params);
	}
}
