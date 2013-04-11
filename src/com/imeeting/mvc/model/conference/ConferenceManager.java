package com.imeeting.mvc.model.conference;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.dao.DataAccessException;

import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.conference.attendee.AttendeeModel;
import com.imeeting.mvc.model.conference.attendee.AttendeeModel.OnlineStatus;
import com.richitec.notify.Notifier;

public class ConferenceManager {

	private static Log log = LogFactory.getLog(ConferenceManager.class);

	private Map<String, ConferenceModel> conferenceMap = null;
	private ConferenceDB conferenceDao;

	public ConferenceManager() {
		conferenceMap = new ConcurrentHashMap<String, ConferenceModel>();
	}

	public void setConferenceDao(ConferenceDB dao) {
		conferenceDao = dao;
	}

	public ConferenceModel getConference(String conferenceId) {
		return conferenceMap.get(conferenceId);
	}

	public ConferenceModel creatConference(String conferenceId, String ownerName) {
		ConferenceModel conference = new ConferenceModel(conferenceId,
				ownerName);
		conferenceMap.put(conferenceId, conference);
		return conference;
	}

	public ConferenceModel removeConference(String conferenceId) {
		log.info("remove conference " + conferenceId);
		return conferenceMap.remove(conferenceId);
	}

	/**
	 * remove the conference from conference manager if all attendees are
	 * offline
	 * 
	 * @param conferenceId
	 * @throws SQLException
	 */
	public synchronized void removeConferenceIfEmpty(String conferenceId)
			throws DataAccessException {
		ConferenceModel conference = getConference(conferenceId);
		if (conference != null) {
			Collection<AttendeeModel> attendees = conference.getAllAttendees();
			boolean isEmpty = true;
			for (AttendeeModel ab : attendees) {
				if (ab.getOnlineStatus() == OnlineStatus.online) {
					isEmpty = false;
					break;
				}
			}
			if (isEmpty) {
				closeConference(conferenceId);
			}
		}
	}
	
	/**
	 * close the conference and release all resources
	 * @param conferenceId
	 */
	public void closeConference(String conferenceId) {
		removeConference(conferenceId);
		ContextLoader.getDonkeyClient().destroyConference(conferenceId,
				conferenceId);
		conferenceDao.close(conferenceId);
	}

	public void notifyConferenceDestoryed(String conferenceId) {
		JSONObject msg = new JSONObject();
		try {
			msg.put("conferenceId", conferenceId);
			msg.put("action", ConferenceAction.conf_destoryed.name());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Notifier nf = ContextLoader.getNotifier();
		nf.notifyWithHttpPost(conferenceId, msg.toString());
	}
	
	/**
	 * 检查系统中所有会议成员的在线状态，如果30秒未收到心跳消息，则认为该用户不在线。
	 */
	public void checkAllConfAttendeeHeartBeat() {
		Long currentTimeMillis = System.currentTimeMillis();
		for (ConferenceModel conf : conferenceMap.values()) {
			for (AttendeeModel attendee : conf.getAllAttendees()) {
				if (!attendee.isJoined() || 
					null == attendee.getLastHBTimeMillis()) {
					continue;
				}

				if (attendee.isOnline()) {
					if (currentTimeMillis - attendee.getLastHBTimeMillis() > 30 * 1000) {
						attendee.setOnlineStatus(AttendeeModel.OnlineStatus.offline);
						conf.broadcastAttendeeStatus(attendee);
					}
				} else {
					if (currentTimeMillis - attendee.getLastHBTimeMillis() <= 30 * 1000) {
						attendee.setOnlineStatus(AttendeeModel.OnlineStatus.online);
						conf.broadcastAttendeeStatus(attendee);
					}
				}
			}
		}
	}
	
	/**
	 * 每隔5分钟从数据库中查询所有预约会议，
	 * 如果会议预约时间和当前系统时间相差是否在5分钟之内，
	 * 向donkey发送请求开启一个会议，并把会议状态置为OPEN。
	 */
	public void checkAllScheduledConference() {
		//
		log.info("checkAllScheduledConference");
	}
}
