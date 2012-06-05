package com.imeeting.mvc.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

	/**
	 * create a new conference and response with the id of the conference.
	 * @param String moderator - Moderator's userId.
	 * @param String attendeeList - An JSON Array that contains all attendees' userId.
	 * @throws IOException 
	 */
	@RequestMapping(method=RequestMethod.POST)
	public void create(
			HttpServletResponse response,
			@RequestParam String moderator,
			@RequestParam String attendeeList) throws IOException{
		String confId = "123456";
		//TODO: create conference and generate confId. create MSML conference in media server.
		response.getWriter().print(confId);
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
		//TODO: create conference and generate confId.
		response.getWriter().print(confList);
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
