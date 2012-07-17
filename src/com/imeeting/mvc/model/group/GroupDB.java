package com.imeeting.mvc.model.group;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.imeeting.constants.AttendeeConstants;
import com.imeeting.constants.GroupConstants;
import com.imeeting.mvc.model.group.attendee.AttendeeBean;

public class GroupDB {
	private static Log log = LogFactory.getLog(GroupDB.class);
	
	private JdbcTemplate jdbc;
	
	public enum GroupStatus {
		OPEN, CLOSE
	};

	public enum UserGroupStatus {
		VISIABLE, HIDDEN
	};
	
	public void setDataSource(DataSource ds){
		jdbc = new JdbcTemplate(ds);
	}

	public void saveGroup(GroupModel group) throws DataAccessException {
		insert(group.getGroupId());
		editGroupTitle(group.getGroupId(), "ID: " + group.getGroupId()); // temporary use only
		Collection<AttendeeBean> attendeeCollection = group.getAllAttendees();
		saveAttendeeBeans(group.getGroupId(), attendeeCollection);
	}
	
	public void saveAttendeeBeans(String groupId, Collection<AttendeeBean> attendeeCollection) throws DataAccessException {
		String sql = "INSERT INTO im_attendee(groupId, username) VALUES(?,?)";
		List<Object[]> params = new ArrayList<Object[]>();
		for (AttendeeBean attendee : attendeeCollection){
			params.add(new Object[] { groupId, attendee.getUsername() });
		}
		jdbc.batchUpdate(sql, params);
	}
	
	public void saveAttendees(String groupId, Collection<String> attendeeNameCollection) throws DataAccessException {
		String sql = "INSERT INTO im_attendee(groupId, username) VALUES(?,?)";
		List<Object[]> params = new ArrayList<Object[]>();
		for (String attendeeName : attendeeNameCollection){
			params.add(new Object[] { groupId, attendeeName });
		}
		jdbc.batchUpdate(sql, params);
	}	

	public int insert(String groupId) throws DataAccessException {
		return jdbc.update("INSERT INTO im_group(groupId) VALUES (?)", groupId);
	}

	public int close(String groupId) throws DataAccessException {
		return setStatus(groupId, GroupStatus.CLOSE);
	}
	
	private int setStatus(String groupId, GroupStatus status) throws DataAccessException {
		return jdbc.update(
					"UPDATE im_group SET status=? WHERE groupId=?",
					status.name(), groupId);
	}

	public int getGroupTotalCount(String username) throws DataAccessException {
		// query the total count of conference list related to username
		String sql = "SELECT count(c.groupId) "
			+ "FROM im_group AS c INNER JOIN im_attendee AS a "
			+ "ON c.groupId = a.groupId AND a.username = ? AND a.status = ? "
			+ "ORDER BY c.created DESC";
		return jdbc.queryForInt(sql, username, UserGroupStatus.VISIABLE.name());
	}

	public JSONArray getGroupList(String userName, int offset,
			int pageSize) throws DataAccessException {
		// query conference list related to username
		String sql = "SELECT c.groupId AS id, UNIX_TIMESTAMP(c.created) AS created, c.status, c.title "
				+ "FROM im_group AS c INNER JOIN im_attendee AS a "
				+ "ON c.groupId = a.groupId AND a.username = ? AND a.status = ? "
				+ "ORDER BY c.created DESC LIMIT ?, ?";

		int startIndex = (offset - 1) * pageSize;
		List<Map<String, Object>> groupResultList = 
			jdbc.queryForList(sql, userName, UserGroupStatus.VISIABLE.name(), startIndex, pageSize);
		
		log.info("groupResultList size: " + groupResultList.size());
		
		if (groupResultList.size() <= 0) {
			return new JSONArray();
		}
		
		StringBuffer groupIds = new StringBuffer();
		groupIds.append('(');

		HashMap<String, JSONObject> groupInfoMap = new HashMap<String, JSONObject>();

		for (Map<String, Object> groupMap : groupResultList) {
			String groupId = (String) groupMap.get("id");
			Long createdTime = (Long) groupMap.get("created");
			String status = (String) groupMap.get("status");
			String title = (String) groupMap.get("title");
			log.info("groupId: " + groupId);
			log.info("created time: " + createdTime.longValue());
			log.info("status: " + status);
			log.info("title: " + title);
			
			JSONObject group = new JSONObject();
			try {
				group.put(GroupConstants.groupId.name(), groupId);
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
		List<Map<String, Object>> attendeeList = jdbc.queryForList(sql);

		for (Map<String, Object> attendeeMap : attendeeList) {
			String groupId = (String) attendeeMap.get("id");
			String attendee = (String) attendeeMap.get(AttendeeConstants.username.name());

//			log.info("groupId: " + groupId);
//			log.info("attendee: " + attendee);
			
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

	
	public int makeGroupVisibleForEachAttendee(String groupId) throws DataAccessException {
		String sql = "UPDATE im_attendee SET status = ? WHERE groupId = ? ";
		return jdbc.update(sql, UserGroupStatus.VISIABLE.name(), groupId );
	}

	public int hideGroup(String groupId, String userName) throws DataAccessException {
		String sql = "UPDATE im_attendee SET status = ? WHERE groupId = ? AND username = ?";
		return jdbc.update(sql, UserGroupStatus.HIDDEN.name(), groupId, userName);
	}

	
	/**
	 * get attendees from group
	 * @param groupId
	 * @return List<Map<String, Object>>
	 * @throws SQLException 
	 */
	public List<AttendeeBean> getGroupAttendees(String groupId) throws DataAccessException {
		return jdbc.query("SELECT username FROM im_attendee WHERE groupId = ?", 
				new Object[] {groupId}, 
				new RowMapper<AttendeeBean>(){
					@Override
					public AttendeeBean mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return new AttendeeBean(rs.getString("username"));
					}
				});
	}
	
	public int editGroupTitle(String groupId, String title) throws DataAccessException {
		String sql = "UPDATE im_group SET title = ? WHERE groupId = ?";
		return jdbc.update(sql, title, groupId);
	}
	
	/**
	 * update status of specified group
	 * @param groupId
	 * @param status
	 * @return rows
	 * @throws SQLException 
	 */
	public int updateStatus(String groupId, GroupStatus status) throws DataAccessException {
		log.info("updateStatus - " + " groupId: " + groupId);
		return setStatus(groupId, status);
	}
	
	public List<String> getTokens(String userNames) {
		String sql = "SELECT token FROM im_token WHERE username IN " + userNames;
		List<String> tokens = jdbc.queryForList(sql, String.class);
		return tokens;
	}
}
