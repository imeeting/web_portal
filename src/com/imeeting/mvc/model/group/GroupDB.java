package com.imeeting.mvc.model.group;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.imeeting.constants.AttendeeConstants;
import com.imeeting.constants.GroupConstants;
import com.imeeting.mvc.model.group.attendee.AttendeeBean;
import com.richitec.db.DBHelper;

public class GroupDB {
	private static Log log = LogFactory.getLog(GroupDB.class);
	
	public enum GroupStatus {
		OPEN, CLOSE
	};

	public enum UserGroupStatus {
		VISIABLE, HIDDEN
	};

	public static int insert(String groupId, String owner) throws SQLException {
		String sql = "INSERT INTO im_group(groupId, owner) VALUES (?, ?)";
		Object[] params = new Object[] { groupId, owner };
		return DBHelper.getInstance().update(sql, params);
	}

	public static int close(String groupId) throws SQLException {
		return setStatus(groupId, GroupStatus.CLOSE);
	}
	
	@Deprecated
	public static int open(String groupId) throws SQLException {
		return setStatus(groupId, GroupStatus.OPEN);
	}
	
	private static int setStatus(String groupId, GroupStatus status) throws SQLException {
		String sql = "UPDATE im_group set status = ? WHERE groupId = ?";
		Object[] params = new Object[] { status.name(), groupId };
		return DBHelper.getInstance().update(sql, params);
	}

	public static int getGroupTotalCount(String username) throws SQLException {
		// query the total count of conference list related to username
		String sql = "SELECT count(c.groupId) "
				+ "FROM im_group AS c INNER JOIN im_attendee AS a "
				+ "ON c.groupId = a.groupId AND a.username = ? AND a.status = ? "
				+ "ORDER BY c.created DESC";
		Object[] params = new Object[] { username, UserGroupStatus.VISIABLE.name() };
		return DBHelper.getInstance().count(sql, params);
	}

	public static JSONArray geGroupList(String userName, int offset,
			int pageSize) throws SQLException {
		// query conference list related to username
		String sql = "SELECT c.groupId AS id, c.owner, UNIX_TIMESTAMP(c.created) AS created, c.status, c.title "
				+ "FROM im_group AS c INNER JOIN im_attendee AS a "
				+ "ON c.groupId = a.groupId AND a.username = ? AND a.status = ? "
				+ "ORDER BY c.created DESC";
		Object[] params = new Object[] { userName, UserGroupStatus.VISIABLE.name() };

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
			String title = (String) groupMap.get("title");
			log.info("groupId: " + groupId);
			log.info("owner: " + owner);
			log.info("created time: " + createdTime.longValue());
			log.info("status: " + status);
			log.info("title: " + title);
			
			JSONObject group = new JSONObject();
			try {
				group.put(GroupConstants.groupId.name(), groupId);
				group.put(GroupConstants.owner.name(), owner);
				group.put(GroupConstants.created_time.name(), createdTime);
				group.put(GroupConstants.status.name(), status);
				group.put(GroupConstants.title.name(), title);
				JSONArray attendees = new JSONArray();
				group.put(GroupConstants.attendees.name(), attendees);
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
			String attendee = (String) attendeeMap.get(AttendeeConstants.username.name());

			log.info("groupId: " + groupId);
			log.info("attendee: " + attendee);
			
			JSONObject group = groupInfoMap.get(groupId);
			try {
				JSONArray attendees = group.getJSONArray(GroupConstants.attendees.name());
				attendees.put(attendee);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		// convert group info map to json array
		JSONArray groupJSONArray = new JSONArray();
		for (Map<String, Object> groupInfo : groupResultList) {
			String groupId = (String) groupInfo.get("id");
			JSONObject group = groupInfoMap.get(groupId);
			groupJSONArray.put(group);
		}
		return groupJSONArray;
	}

	public static int hideGroup(String groupId, String userName)
			throws SQLException {
		String sql = "UPDATE im_attendee set status = ? WHERE groupId = ? AND username = ?";
		Object[] params = new Object[] { UserGroupStatus.HIDDEN.name(), groupId,
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
	
	/**
	 * get attendees from group
	 * @param groupId
	 * @return List<Map<String, Object>>
	 * @throws SQLException 
	 */
	public static List<Map<String, Object>> getGroupAttendees(String groupId) throws SQLException {
		String sql = "SELECT username FROM im_attendee WHERE groupId = ?";
		Object[] params = new Object[] {groupId};
		List<Map<String, Object>> result = DBHelper.getInstance().query(sql, params);
		return result;
	}
	
	public static void editGroupTitle(String groupId, String title) throws SQLException {
		String sql = "UPDATE im_group SET title = ? WHERE groupId = ?";
		Object[] params = new Object[] {title, groupId};
		DBHelper.getInstance().update(sql, params);
	}
	
	/**
	 * check if the group exists in the db
	 * @param groupId
	 * @return
	 * @throws SQLException
	 */
	@Deprecated
	public static boolean isGroupExisted(String groupId) throws SQLException {
		String sql = "SELECT count(groupId) FROM im_group WHERE groupId = ?";
		Object[] params = new Object[] {groupId};
		int count = DBHelper.getInstance().count(sql, params);
		boolean ret = false;
		if (count > 0) {
			ret = true;
		}
		return ret;
	}
	
	/**
	 * update the owner and status of specified group
	 * @param groupId
	 * @param owner
	 * @param status
	 * @return rows
	 * @throws SQLException 
	 */
	public static int updateOwnerAndStatus(String groupId, String owner, GroupStatus status) throws SQLException {
		String sql = "UPDATE im_group SET owner = ? AND status = ? WHERE groupId = ?";
		Object[] params = new Object[] {owner, status.name(), groupId};
		int rows = DBHelper.getInstance().update(sql, params);
		return rows;
	}
}
