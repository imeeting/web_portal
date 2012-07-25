package com.imeeting.mvc.model.conference;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;

import com.imeeting.mvc.model.conference.ConferenceDB.ConferenceStatus;
import com.imeeting.mvc.model.conference.attendee.AttendeeBean;
import com.imeeting.mvc.model.conference.attendee.AttendeeBean.OnlineStatus;

public class ConferenceManager {

	private static Log log = LogFactory.getLog(ConferenceManager.class);

	private Map<String, ConferenceModel> conferenceMap = null;
	private ConferenceDB conferenceDao;

	public ConferenceManager() {
		conferenceMap = new ConcurrentHashMap<String, ConferenceModel>();
	}
	
	public void setConferenceDao(ConferenceDB dao){
		conferenceDao = dao;
	}

	public ConferenceModel getConference(String conferenceId) {
		return conferenceMap.get(conferenceId);
	}

	public synchronized ConferenceModel checkConferenceModel(String conferenceId,
			String userName) {
		ConferenceModel conference = conferenceMap.get(conferenceId);
		log.info("checkConferenceModel - conference: " + conference);
		if (null == conference) {
			return null;
		}

		AttendeeBean attendee = conference.getAttendee(userName);
		attendee.setOnlineStatus(AttendeeBean.OnlineStatus.online);

		return conference;
	}

	public ConferenceModel creatConference(String conferenceId, String ownerName) {
		ConferenceModel conference = new ConferenceModel(conferenceId, ownerName);
		conferenceMap.put(conferenceId, conference);
		return conference;
	}

	public ConferenceModel removeConference(String conferenceId) {
		log.info("remove conference " + conferenceId);
		return conferenceMap.remove(conferenceId);
	}

	/**
	 * remove the conference from conference manager if all attendees are offline
	 * 
	 * @param conferenceId
	 * @throws SQLException 
	 */
	public synchronized void removeConferenceIfEmpty(String conferenceId) throws DataAccessException {
		ConferenceModel conference = getConference(conferenceId);
		if (conference != null) {
			Collection<AttendeeBean> attendees = conference.getAllAttendees();
			boolean isEmpty = true;
			for (AttendeeBean ab : attendees) {
				if (ab.getOnlineStatus() == OnlineStatus.online) {
					isEmpty = false;
					break;
				}
			}
			if (isEmpty) {
				removeConference(conferenceId);
				conferenceDao.close(conferenceId);
			}
		}
	}
}
