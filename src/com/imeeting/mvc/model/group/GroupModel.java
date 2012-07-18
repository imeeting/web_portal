package com.imeeting.mvc.model.group;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javapns.notification.PushNotificationPayload;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.group.attendee.AttendeeAction;
import com.imeeting.mvc.model.group.attendee.AttendeeBean;
import com.imeeting.mvc.model.group.attendee.AttendeeBean.OnlineStatus;
import com.imeeting.mvc.model.group.attendee.AttendeeBean.VideoStatus;
import com.richitec.notify.Notifier;

public class GroupModel {
	
	private static Log log = LogFactory.getLog(GroupModel.class);

	private String groupId;
	private String ownerName;
	private String audioConfId;
	private Map<String, AttendeeBean> attendeeMap;
	
	public GroupModel(String groupId, String ownerName) {
		this.groupId = groupId;
		this.ownerName = ownerName;
		this.attendeeMap = new ConcurrentHashMap<String, AttendeeBean>();
	}

	public String getGroupId() {
		return this.groupId;
	}

	public String getOwnerName() {
		return this.ownerName;
	}

	public void setAudioConfId(String audioConfId) {
		this.audioConfId = audioConfId;
	}

	public String getAudioConfId() {
		return this.audioConfId;
	}

	public final Collection<AttendeeBean> getAllAttendees() {
		return attendeeMap.values();
	}
	
	public Collection<String> getAllAttendeeName(){
		List<String> list = new LinkedList<String>();
		for (AttendeeBean a : getAllAttendees()){
			list.add(a.getUsername());
		}
		return list;
	}
	
	public AttendeeBean getAttendee(String userName){
		return attendeeMap.get(userName);
	}
	
	public AttendeeBean removeAttendee(String userName) {
		AttendeeBean ab = attendeeMap.remove(userName);
		return ab;
	}
	
	public boolean containsAttendee(String userName){
		return attendeeMap.containsKey(userName);
	}

	public void addAttendee(AttendeeBean attendee) {
		attendeeMap.put(attendee.getUsername(), attendee);
	}

	public List<String> getTokensFromAttendees() {
		StringBuffer userNames = new StringBuffer();
		for (AttendeeBean ab : this.attendeeMap.values()) {
			if (!ab.getUsername().equals(ownerName)) {
				userNames.append(ab.getUsername()).append(',');
			}	
		}
		if (userNames.length() > 0 && userNames.lastIndexOf(",") == userNames.length() - 1) {
			userNames.deleteCharAt(userNames.length() - 1);
		}
		List<String> tokens = null;
		if (userNames.length() > 0) {
			tokens = ContextLoader.getGroupDAO().getTokens("(" + userNames.toString() + ")");
		}
		return tokens;
	}
	
	public void notifyAttendeesInvited() {
		log.info("notifyAttendeesInvited");
		List<String> tokens = getTokensFromAttendees();
		if (tokens == null || tokens.size() <= 0) {
			return;
		}
		
		for(String token : tokens) {
			log.info("token: " + token);
		}
		
		PushNotificationPayload payload = new PushNotificationPayload();
		try {
			payload.addAlert("" + ownerName + "邀请您加入讨论组");
			payload.addCustomDictionary("groupId", groupId);
			payload.addCustomDictionary("action", AttendeeAction.invited.name());
			payload.addSound("office_phone.caf");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ContextLoader.getDevAPNSProviderClient().pushNotification(tokens, payload);
		ContextLoader.getDistAPNSProviderClient().pushNotification(tokens, payload);
	}
	
	public void broadcastAttendeeStatus(AttendeeBean attendee) {
		JSONObject msg = new JSONObject();
		try {
			msg.put("groupId", getGroupId());
			msg.put("action", AttendeeAction.update_status.name());
			msg.put("attendee", attendee.toJson());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Notifier nf = ContextLoader.getNotifier();
		nf.notifyWithHttpPost(getGroupId(), msg.toString());
	}
	
	public void notifyAttendeeKickOut(String userName) {
		JSONObject msg = new JSONObject();
		try {
			msg.put("groupId", getGroupId());
			msg.put("username", userName);
			msg.put("action", AttendeeAction.kickout.name());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Notifier nf = ContextLoader.getNotifier();
		nf.notifyWithHttpPost(getGroupId(), msg.toString());
	}
	
	public void notifyAttendeesToUpdateMemberList() {
		JSONObject msg = new JSONObject();
		try {
			msg.put("groupId", getGroupId());
			msg.put("action", AttendeeAction.update_attendee_list.name());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Notifier nf = ContextLoader.getNotifier();
		nf.notifyWithHttpPost(getGroupId(), msg.toString());
	}
	
	public void updateAttendeeStatus(String username, String onlineStatus,
			String videoStatus, String telephoneStatus) {
		AttendeeBean attendee = getAttendee(username);
		if (attendee == null) {
			// user are prohibited to join the group for he isn't in the group
			return;
		}
		if (onlineStatus != null) {
			if (onlineStatus.equals(OnlineStatus.online.name())) {
				attendee.setOnlineStatus(OnlineStatus.online);
			} else if (onlineStatus.equals(OnlineStatus.offline.name())){
				attendee.setOnlineStatus(OnlineStatus.offline);
			}
		}
		if (videoStatus != null) {
			if (videoStatus.equals(VideoStatus.on.name())) {
				attendee.setVideoStatus(VideoStatus.on);
			} else if (videoStatus.equals(VideoStatus.off.name())) {
				attendee.setVideoStatus(VideoStatus.off);
			}
		}
		
		broadcastAttendeeStatus(attendee);
	}
}