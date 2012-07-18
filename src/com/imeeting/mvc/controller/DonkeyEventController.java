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
import com.imeeting.mvc.model.group.GroupManager;
import com.imeeting.mvc.model.group.GroupModel;
import com.imeeting.mvc.model.group.attendee.AttendeeBean;
import com.richitec.donkey.client.DonkeyClient;
import com.richitec.donkey.client.DonkeyEvent;

@Controller
@RequestMapping(value="/donkeyevent")
public class DonkeyEventController {
	
	private static Log log = LogFactory.getLog(DonkeyEventController.class);
	
	private GroupManager groupManager;

	@PostConstruct
	public void init(){
		groupManager = ContextLoader.getGroupManager();
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
		GroupModel group = groupManager.getGroup(requestId);
		String audioConfId = event.getConferenceId();
		group.setAudioConfId(audioConfId);
	}
	
	private void onConferenceCreateFailed(DonkeyEvent event){
		String requestId = event.getRequestId();
		GroupModel group = groupManager.getGroup(requestId);
		//TODO: notify all attendees in this group.
	}
	
	private void onConferenceDestroySuccess(DonkeyEvent event){
		String requestId = event.getRequestId();
		GroupModel group = groupManager.getGroup(requestId);
	}
	
	private void onConferenceStatusConflict(DonkeyEvent event){
		String requestId = event.getRequestId();
		GroupModel group = groupManager.getGroup(requestId);
		//TODO: notify all attendees in this group.
	}
	
	private void onAttendeeCallEstablished(DonkeyEvent event){
		String requestId = event.getRequestId();
		GroupModel group = groupManager.getGroup(requestId);
		//TODO: notify all attendees in this group.
		String sipUri = event.getSipUri();
		String attendeeName = DonkeyClient.getPhoneNumberFromSipUri(sipUri);
		AttendeeBean attendee = group.getAttendee(attendeeName);
		attendee.statusCallEstablished();
		group.broadcastAttendeeStatus(attendee);
		if (attendeeName.equals(group.getOwnerName())) {
			// when owner's phone is established, notify all attendees to join
			group.notifyAttendeesInvited();
		}
	}
	
	private void onAttendeeCallFailed(DonkeyEvent event){
		String requestId = event.getRequestId();
		GroupModel group = groupManager.getGroup(requestId);
		//TODO: notify all attendees in this group.
		String sipUri = event.getSipUri();
		String attendeeName = DonkeyClient.getPhoneNumberFromSipUri(sipUri);
		AttendeeBean attendee = group.getAttendee(attendeeName);
		attendee.statusCallFailed();
		group.broadcastAttendeeStatus(attendee);
	}
	
	private void onAttendeeCallTerminated(DonkeyEvent event){
		String requestId = event.getRequestId();
		GroupModel group = groupManager.getGroup(requestId);
		log.info("1 group: " + group);
		//TODO: notify all attendees in this group.
		String sipUri = event.getSipUri();
		String attendeeName = DonkeyClient.getPhoneNumberFromSipUri(sipUri);
		log.info("2 group: " + group);
		AttendeeBean attendee = group.getAttendee(attendeeName);
		log.info("3 group: " + group);
		attendee.statusCallTerminated();
		log.info("4 group: " + group);
		group.broadcastAttendeeStatus(attendee);
	}
	
	private void onAttendeeStatusConflict(DonkeyEvent event){
		String requestId = event.getRequestId();
		GroupModel group = groupManager.getGroup(requestId);
		//TODO: notify all attendees in this group.
	}
	
}
