package com.imeeting.mvc.model.group;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imeeting.mvc.model.group.GroupDB.GroupStatus;
import com.imeeting.mvc.model.group.attendee.AttendeeBean;
import com.imeeting.mvc.model.group.attendee.AttendeeBean.OnlineStatus;

public class GroupManager {

	private static Log log = LogFactory.getLog(GroupManager.class);

	private Map<String, GroupModel> groupMap = null;

	public GroupManager() {
		groupMap = new ConcurrentHashMap<String, GroupModel>();
	}

	public GroupModel getGroup(String groupId) {
		return groupMap.get(groupId);
	}

	public synchronized GroupModel checkAndCreateGroupModel(String groupId,
			String ownerName) throws SQLException {
		GroupModel group = groupMap.get(groupId);
		if (null == group) {
			group = new GroupModel(groupId, ownerName);
			int r = GroupDB.updateStatus(groupId, GroupStatus.OPEN);
			if (1 != r) {
				return null;
			}
			group = GroupDB.loadAttendees(group);
			groupMap.put(groupId, group);
		}

		AttendeeBean attendee = group.getAttendee(ownerName);
		attendee.setOnlineStatus(AttendeeBean.OnlineStatus.online);

		return group;
	}

	public GroupModel creatGroup(String groupId, String ownerName) {
		GroupModel group = new GroupModel(groupId, ownerName);
		groupMap.put(groupId, group);
		return group;
	}

	public GroupModel removeGroup(String groupId) {
		log.info("remove group " + groupId);
		return groupMap.remove(groupId);
	}

	/**
	 * remove the group from group manager if all attendees are offline
	 * 
	 * @param groupId
	 * @throws SQLException 
	 */
	public synchronized void removeGroupIfEmpty(String groupId) throws SQLException {
		GroupModel group = getGroup(groupId);
		if (group != null) {
			Collection<AttendeeBean> attendees = group.getAllAttendees();
			boolean isEmpty = true;
			for (AttendeeBean ab : attendees) {
				if (ab.getOnlineStatus() == OnlineStatus.online) {
					isEmpty = false;
					break;
				}
			}
			if (isEmpty) {
				removeGroup(groupId);
				GroupDB.close(groupId);
			}
		}
	}
}
