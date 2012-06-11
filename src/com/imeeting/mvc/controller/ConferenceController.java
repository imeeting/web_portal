package com.imeeting.mvc.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.conference.ConferenceDB;
import com.imeeting.mvc.model.conference.ConferenceManager;
import com.imeeting.mvc.model.conference.ConferenceModel;
import com.imeeting.mvc.model.conference.message.CreateAudioConferenceMsg;
import com.imeeting.mvc.model.conference.message.DestroyConferenceMsg;
import com.richitec.util.RandomString;

/**
 * 
 * client --- create conference request --> Server
 * client <-- response with confId      --- Server
 * client --- attendee list             --> Server 
 * client <-- response from server      --- Server
 * client send short message to attendees.
 * 
 * @author huuguanghui
 *
 */

@Controller
@RequestMapping(value="/conference")
public class ConferenceController {
	
	private static Log log = LogFactory.getLog(ConferenceController.class);

	/**
	 * create a new conference and response with the id of the conference.
	 * @param String moderator - Moderator's userId.
	 * @param String attendeeList - An JSON Array that contains all attendees' userId.
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws JSONException 
	 */
	@RequestMapping(value="/create", method=RequestMethod.GET)
	public void create(
			HttpServletResponse response,
			@RequestParam(value="userName") String userName,
			@RequestParam(value="moderator", required=false) String moderator,
			@RequestParam(value="attendeeList", required=false) String attendeeList) throws IOException, SQLException {
		///
		log.debug("create");
		String confId = RandomString.genRandomNum(8);
		int r = ConferenceDB.insert(confId, userName);
		if (r != 1){
			log.error("Database operation error: cannot insert (" + confId + "," + userName+")");
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		ConferenceManager confManager = ContextLoader.getConferenceManager();
		ConferenceModel model = confManager.createConference(confId, userName);
		model.tell(new CreateAudioConferenceMsg());
		
		response.setStatus(HttpServletResponse.SC_ACCEPTED);
		response.getWriter().print(confId);
	}
	
	/**
	 * Destroy conference
	 * 
	 * @param response
	 * @param userName
	 * @param confId
	 * @throws IOException
	 * @throws SQLException 
	 */
	@RequestMapping(value="/destroy", method=RequestMethod.GET)
	public void destroy(
			HttpServletResponse response,
			@RequestParam String userName,
			@RequestParam String confId) throws IOException, SQLException{
		///
		log.debug("destroy");
		ConferenceManager confManager = ContextLoader.getConferenceManager();
		ConferenceModel conference = confManager.getConference(confId);
		if (null == conference){
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Cannot find conference with ID:" + confId);
			return;
		}
		
		if (userName != conference.getOwner()){
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "userName and confId are not match");
			return;
		}
		
		conference.tell(new DestroyConferenceMsg());
		response.setStatus(HttpServletResponse.SC_ACCEPTED);
	}
	
	/**
	 * Get all conferences of userId.
	 * 
	 * @param response
	 * @param userId
	 * @throws IOException 
	 */
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public void list(
			HttpServletResponse response,
			@RequestParam String userId) throws IOException{
		String confList = "[" +
				"{confId:123456, moderator:13382794516, attendeeList:[18652970325, 13813005146]}, " +
				"{}, " +
				"{}" + "]";
		response.getWriter().print(confList);
	}
	
	/**
	 * get attendee list of the conference
	 */
	@RequestMapping(value="/attendeelist", method=RequestMethod.GET)
	public void attendeeList(){
		
	}
	
	/**
	 * Add attendees by moderator.
	 * 
	 * @param response
	 * @param confId
	 * @param attendeeList
	 * @throws IOException
	 */
	@RequestMapping(value="/invite", method=RequestMethod.POST)
	public void invite(
			HttpServletResponse response,
			@RequestParam String confId,
			@RequestParam String attendeeList) throws IOException {
		//TODO: insert MySQL
		//TODO: send message to ConferenceModel
		//TODO: create actor for attendees
	}
	
	/**
	 * Moderator can kick out any attendee of his conference.
	 * 
	 */
	@RequestMapping(value="/kickout", method=RequestMethod.POST)
	public void kickout(
			HttpServletResponse response,
			@RequestParam String confId,
			@RequestParam String attendeeId) {
		
	}
	
	/**
	 * Attendee join conference
	 * 
	 * @param response
	 * @param confId
	 * @param userId
	 */
	@RequestMapping(value="/join", method=RequestMethod.POST)
	public void join(
			HttpServletResponse response,
			@RequestParam String confId,
			@RequestParam String userId){
		//
	}
	
	/**
	 * Attendee unjoin conference
	 * 
	 * @param response
	 * @param confId
	 * @param userId
	 */
	@RequestMapping(value="/unjoin", method=RequestMethod.POST)
	public void unjoin(
			HttpServletResponse response,
			@RequestParam String confId,
			@RequestParam String userId){
		
	}
	
	/**
	 * Call phone number of userId.
	 * 
	 * @param response
	 * @param confId
	 * @param userId
	 */
	@RequestMapping(value="/call", method=RequestMethod.POST)
	public void call(
			HttpServletResponse response,
			@RequestParam String confId,
			@RequestParam String userId){
		
	}
	
	/**
	 * Hang up phone of userId.
	 * 
	 * @param response
	 * @param confId
	 * @param userId
	 */
	@RequestMapping(value="/hangup", method=RequestMethod.POST)
	public void hangup(
			HttpServletResponse response,
			@RequestParam String confId,
			@RequestParam String userId){
		
	}
	
	/**
	 * Mute phone of userId.
	 * 
	 * @param response
	 * @param confId
	 * @param userId
	 */
	@RequestMapping(value="/mute", method=RequestMethod.POST)
	public void mute(
			HttpServletResponse response,
			@RequestParam String confId,
			@RequestParam String userId){
		
	}
	
	/**
	 * Unmute phone of userId.
	 * 
	 * @param response
	 * @param confId
	 * @param userId
	 */
	@RequestMapping(value="/unmute", method=RequestMethod.POST)
	public void unmute(
			HttpServletResponse response,
			@RequestParam String confId,
			@RequestParam String userId){
		
	}

}
