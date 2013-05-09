package com.imeeting.mvc.controller;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.conference.ConferenceDB;
import com.imeeting.mvc.model.conference.ConferenceManager;
import com.imeeting.mvc.model.conference.ConferenceModel;
import com.imeeting.mvc.model.conference.attendee.AttendeeModel;
import com.richitec.donkey.client.DonkeyClient;
import com.richitec.donkey.client.DonkeyEvent;
import com.richitec.ucenter.model.UserDAO;

@Controller
@RequestMapping(value="/donkeyevent")
public class DonkeyEventController {
	
	private static Log log = LogFactory.getLog(DonkeyEventController.class);
	
	private ConferenceManager conferenceManager;
	private ConferenceDB conferenceDao;
	private UserDAO userDao;
	@PostConstruct
	public void init(){
		conferenceManager = ContextLoader.getConferenceManager();
		conferenceDao = ContextLoader.getConferenceDAO();
		userDao = ContextLoader.getUserDAO();
	}
	
	@RequestMapping
	public void onEvent(
			HttpServletResponse response,
			@RequestBody String requestBody){
		response.setStatus(HttpServletResponse.SC_OK);
		log.info("\nDonkey Event : \n" + requestBody);
		try {
			DonkeyEvent event = new DonkeyEvent(requestBody);
			String eventType = event.getEventType();
			if (DonkeyEvent.EV_CONFERENCE_CREATE_SUCCESS.equals(eventType)) {
				onConferenceCreateSuccess(event);
			} else 
			if (DonkeyEvent.EV_CONFERENCE_CREATE_FAILED.equals(eventType)) {
				onConferenceCreateFailed(event);
			} else 
			if (DonkeyEvent.EV_CONFERENCE_DESTROY_SUCCESS.equals(eventType)) {
				onConferenceDestroySuccess(event);
			} else 
			if (DonkeyEvent.EV_CONFERENCE_STATUS_CONFLICT.equals(eventType)) {
				onConferenceStatusConflict(event);
			} else 
			if (DonkeyEvent.EV_ATTENDEE_CALL_ESTABLISHED.equals(eventType)) {
				onAttendeeCallEstablished(event);
			} else 
			if (DonkeyEvent.EV_ATTENDEE_CALL_FAILED.equals(eventType)) {
				onAttendeeCallFailed(event);
			} else 
			if (DonkeyEvent.EV_ATTENDEE_CALL_TERMINATED.equals(eventType)) {
				onAttendeeCallTerminated(event);
			} else 
			if (DonkeyEvent.EV_ATTENDEE_STATUS_CONFLICT.equals(eventType)) {
				onAttendeeStatusConflict(event);
			} else {
				log.error("Unexpected Event Type : " + eventType);
			}
		} catch (JSONException e) {
			log.error("\nCannot parse donkey event to JSON object : \n" + requestBody);
			e.printStackTrace();
		}
	}
	
	private void onConferenceCreateSuccess(DonkeyEvent event){
		String requestId = event.getRequestId();
		ConferenceModel conference = conferenceManager.getConference(requestId);
		if (null == conference) {
			log.error("ConferenceModel of <" + requestId + "> is NULL.");
			return;
		}
		String audioConfId = event.getConferenceId();
		conference.setAudioConfId(audioConfId);
	}
	
	private void onConferenceCreateFailed(DonkeyEvent event){
		String requestId = event.getRequestId();
		ConferenceModel conference = conferenceManager.getConference(requestId);
		if (null == conference) {
			log.error("ConferenceModel of <" + requestId + "> is NULL.");
			return;
		}
		//TODO: notify all attendees in this conference.
	}
	
	private void onConferenceDestroySuccess(DonkeyEvent event){
		String requestId = event.getRequestId();
		ConferenceModel conference = conferenceManager.getConference(requestId);
		if (null == conference) {
			log.error("ConferenceModel of <" + requestId + "> is NULL.");
			return;
		}
		conferenceManager.removeConference(requestId);
		conferenceDao.close(requestId);
		conferenceManager.notifyConferenceDestoryed(conference.getOwnerName(), requestId);
	}
	
	private void onConferenceStatusConflict(DonkeyEvent event){
		String requestId = event.getRequestId();
		ConferenceModel conference = conferenceManager.getConference(requestId);
		if (null == conference) {
			log.error("ConferenceModel of <" + requestId + "> is NULL.");
			return;
		}		
		//TODO: notify all attendees in this conference.
	}
	
	private void onAttendeeCallEstablished(DonkeyEvent event){
		String requestId = event.getRequestId();
		ConferenceModel conference = conferenceManager.getConference(requestId);
		if (null == conference) {
			log.error("ConferenceModel of <" + requestId + "> is NULL.");
			return;
		}		
		// notify all attendees in this conference.
		String sipUri = event.getSipUri();
		String attendeeName = DonkeyClient.getPhoneNumberFromSipUri(sipUri);
		AttendeeModel attendee = conference.getAttendee(attendeeName);
		
		if (attendee == null) {
			log.info("onAttendeeCallEstablished - attendee is null, add to conference");
			String nickname = userDao.getNickname(attendeeName);
			attendee = new AttendeeModel(attendeeName);
			attendee.setNickname(nickname);
			conference.addAttendee(attendee);
			
			attendee.statusCallEstablished();
			conference.notifyAttendeesToUpdateMemberList();
		} else {
			attendee.statusCallEstablished();
			conference.broadcastAttendeeStatus(attendee);
		}
		log.info("onAttendeeCallEstablished - attendee: " + attendee.toJson().toString());
	}
	
	private void onAttendeeCallFailed(DonkeyEvent event){
		String requestId = event.getRequestId();
		ConferenceModel conference = conferenceManager.getConference(requestId);
		if (null == conference) {
			log.error("ConferenceModel of <" + requestId + "> is NULL.");
			return;
		}		
		// notify all attendees in this conference.
		String sipUri = event.getSipUri();
		String attendeeName = DonkeyClient.getPhoneNumberFromSipUri(sipUri);
		AttendeeModel attendee = conference.getAttendee(attendeeName);
		
		if (attendee == null) {
			log.info("onAttendeeCallFailed - attendee is null");
			return;
		}
		attendee.statusCallFailed();
		log.info("onAttendeeCallFailed - attendee: " + attendee.toJson().toString());
		
		conference.broadcastAttendeeStatus(attendee);
	}
	
	private void onAttendeeCallTerminated(DonkeyEvent event){
		String requestId = event.getRequestId();
		ConferenceModel conference = conferenceManager.getConference(requestId);
		if (null == conference) {
			log.error("ConferenceModel of <" + requestId + "> is NULL.");
			return;
		}		
		// notify all attendees in this conference.
		String sipUri = event.getSipUri();
		String attendeeName = DonkeyClient.getPhoneNumberFromSipUri(sipUri);
		AttendeeModel attendee = conference.getAttendee(attendeeName);
		
		if (attendee == null) {
			log.info("onAttendeeCallTerminated - attendee is null");
			return;
		}
		attendee.statusCallTerminated();
		log.info("onAttendeeCallTerminated - attendee: " + attendee.toJson().toString());
		conference.broadcastAttendeeStatus(attendee);
	}
	
	private void onAttendeeStatusConflict(DonkeyEvent event){
		String requestId = event.getRequestId();
		ConferenceModel conference = conferenceManager.getConference(requestId);
		if (null == conference) {
			log.error("ConferenceModel of <" + requestId + "> is NULL.");
			return;
		}		
		//TODO: notify all attendees in this conference.
	}
	
}
