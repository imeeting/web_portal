package com.imeeting.mvc.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.imeeting.constants.GroupConstants;
import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.group.GroupDB;
import com.imeeting.mvc.model.group.GroupManager;
import com.imeeting.mvc.model.group.GroupModel;
import com.imeeting.mvc.model.group.attendee.AttendeeBean;
import com.imeeting.mvc.model.group.attendee.AttendeeBean.OnlineStatus;
import com.imeeting.mvc.model.group.attendee.AttendeeBean.VideoStatus;
import com.richitec.donkey.client.DonkeyClient;
import com.richitec.donkey.client.DonkeyHttpResponse;
import com.richitec.util.Pager;
import com.richitec.util.RandomString;

/**
 * 
 * client --- create group request --> Server client <-- response with confId
 * --- Server client --- attendee list --> Server client <-- response from
 * server --- Server client send short message to attendees.
 * 
 * @author huuguanghui
 * 
 */

@Controller
@RequestMapping(value = "/group")
public class GroupController extends ExceptionController {
	public static final int PageSize = 20;

	private static Log log = LogFactory.getLog(GroupController.class);
	
	private GroupManager groupManager;
	private DonkeyClient donkeyClient;
	private GroupDB groupDao;
	
	@PostConstruct
	public void init(){
		groupManager = ContextLoader.getGroupManager();
		donkeyClient = ContextLoader.getDonkeyClient();
		groupDao = ContextLoader.getGroupDAO();
	}

	/**
	 * create a new group and response with the id of the conference.
	 * 
	 * @param String
	 *            moderator - Moderator's userId.
	 * @param String
	 *            attendeeList - An JSON Array that contains all attendees'
	 *            userId.
	 * @throws IOException
	 * @throws SQLException
	 * @throws JSONException
	 */
	@RequestMapping(value = "/create")
	public void create(
			HttpServletResponse response,
			@RequestParam(value = "username") String userName,
			@RequestParam(value = "attendees", required = false) String attendeeList)
			throws IOException, SQLException, JSONException {
		//step 1. create GroupModel in memory
		String groupId = RandomString.genRandomNum(8);
		GroupModel group = groupManager.creatGroup(groupId, userName);
		
		AttendeeBean owner = new AttendeeBean(userName, OnlineStatus.online);
		group.addAttendee(owner);
		
		if (attendeeList != null && attendeeList.length() > 0) {
			try {
				JSONArray attendeesJsonArray = new JSONArray(attendeeList);
				for (int i = 0; i < attendeesJsonArray.length(); i++) {
					String name = attendeesJsonArray.getString(i);
					AttendeeBean attendee = new AttendeeBean(name);
					group.addAttendee(attendee);
				}
			} catch(JSONException e) {
				log.error("\nCannot parse attendees : " + attendeeList);
				groupManager.removeGroup(groupId);
				throw e;
			}
		}
		
		//step 2. save GroupModel in Database.
		try {
			groupDao.saveGroup(group);
		} catch (SQLException e) {
			log.error("\nSave group <" + groupId + "> to database error : \n" + 
					  "SQL Error Code : " + e.getErrorCode() + "\n" +
					  "Message : " + e.getMessage());
			groupManager.removeGroup(groupId);
			throw e;
		}
		
		//step 3. create audio conference
		String confId = RandomString.genRandomNum(6);
		group.setAudioConfId(confId);
		DonkeyHttpResponse donkeyResp = 
			donkeyClient.createNoControlConference(confId, group.getAllAttendeeName(), groupId);
		if (null == donkeyResp || !donkeyResp.isAccepted()){
			log.error("Create audio conference error : " + 
					(null==donkeyResp? "NULL Response" : donkeyResp.getStatusCode()));
			groupManager.removeGroup(groupId);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
					"Cannot create audio conference");
			return;
		}	
		
		//step 4. send iPhone notification to all attendees.
		
		//step 5. response to user
		JSONObject ret = new JSONObject();
		ret.put(GroupConstants.groupId.name(), groupId);
		ret.put(GroupConstants.audioConfId.name(), confId);
		ret.put(GroupConstants.owner.name(), group.getOwnerName());
		response.setStatus(HttpServletResponse.SC_CREATED);
		response.getWriter().print(ret.toString());
	}

	/**
	 * destroy group
	 * 
	 * @param response
	 * @param username
	 * @param groupId
	 * @throws IOException
	 * @throws SQLException
	 */
	@Deprecated
	@RequestMapping(value = "/destroy")
	public void destroy(HttpServletResponse response,
			@RequestParam String username, @RequestParam String groupId)
			throws IOException, SQLException {
		// /
		log.debug("destroy");
		GroupModel conference = groupManager.getGroup(groupId);
		if (null == conference) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
					"Cannot find conference with ID:" + groupId);
			return;
		}

		if (username != conference.getOwnerName()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"userName and confId are not match");
			return;
		}

		response.setStatus(HttpServletResponse.SC_ACCEPTED);
	}

	/**
	 * Get all groups by user name.
	 * 
	 * @param response
	 * @param username
	 * @throws IOException
	 * @throws SQLException
	 * @throws JSONException
	 */
	@RequestMapping(value = "/list")
	public void list(
			HttpServletResponse response,
			@RequestParam(value = "offset", required = false, defaultValue = "1") int offset,
			@RequestParam(value = "username") String username) throws IOException, SQLException, JSONException {
		
		int count = groupDao.getGroupTotalCount(username);
		JSONArray confs = groupDao.getGroupList(username, offset, PageSize);

		String url = "/conference/list" + "?";
		Pager pager = new Pager(offset, PageSize, count, url);

		JSONObject ret = new JSONObject();
		JSONObject jsonPager = new JSONObject();
		jsonPager.put("offset", pager.getOffset());
		jsonPager.put("pagenumber", pager.getPageNumber());
		jsonPager.put("hasPrevious", pager.getHasPrevious());
		jsonPager.put("hasNext", pager.getHasNext());
		jsonPager.put("previousPage", pager.getPreviousPage());
		jsonPager.put("nextPage", pager.getNextPage());
		jsonPager.put("count", pager.getSize());
		ret.put("pager", jsonPager);
		ret.put("list", confs);

		response.getWriter().print(ret.toString());
	}

	/**
	 * get attendee list of the group which has been opened already
	 * 
	 * @param groupId
	 * @param response
	 * 
	 * @throws IOException
	 */
	@RequestMapping(value = "/attendeeList")
	public void attendeeList(
			HttpServletResponse response,
			@RequestParam String groupId ) throws IOException {
		GroupModel model = groupManager.getGroup(groupId);
		Collection<AttendeeBean> attendees = model.getAllAttendees();
		JSONArray ret = new JSONArray();
		if (attendees != null && attendees.size()>0) {
			for (AttendeeBean att : attendees) {
				ret.put(att.toJson());
			}
		} else {
			log.error("no attendees in group <" + groupId + ">");
		}
		response.getWriter().print(ret.toString());
	}

	/**
	 * Add attendees by moderator.
	 * 
	 * @param response
	 * @param groupId
	 * @param attendees
	 *            - json array string
	 * @throws IOException
	 * @throws SQLException
	 * @throws JSONException 
	 */
	@RequestMapping(value = "/invite")
	public void invite(
			HttpServletResponse response,
			@RequestParam String groupId, 
			@RequestParam String attendees)	throws IOException, SQLException, JSONException {
		log.info("invite attendees");
		if (null == attendees || attendees.length() < 0){
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		List<String> addedAttendeeList = new LinkedList<String>();
		GroupModel group = groupManager.getGroup(groupId);
		JSONArray attendeesJsonArray = new JSONArray(attendees);
		for (int i = 0; i < attendeesJsonArray.length(); i++) {
			String name = attendeesJsonArray.getString(i);
			if (!group.containsAttendee(name)){
				AttendeeBean attendee = new AttendeeBean(name);
				group.addAttendee(attendee);
				addedAttendeeList.add(name);
			}
		}
		
		groupDao.saveAttendees(groupId, addedAttendeeList);
		
		// notify all attendees to update attendee list
		group.notifyAttendeesToUpdateMemberList();
		
		//add attendees to audio conference
		DonkeyHttpResponse donkeyResp =
			donkeyClient.addMoreAttendee(group.getAudioConfId(), addedAttendeeList, groupId);
		if (null == donkeyResp || !donkeyResp.isAccepted()){
			log.error("Add attenddes to audio conference <" +group.getAudioConfId()+ "> error : " + 
					(null==donkeyResp? "NULL Response" : donkeyResp.getStatusCode()));
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
					"Cannot add attendees to audio conference");
			return;
		}	
		
		//TODO: send iPhone notification
		
		response.setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * Moderator can kick out any attendee of his conference.
	 * @throws IOException 
	 * 
	 */
	@RequestMapping(value = "/kickout")
	public void kickout(
			HttpServletResponse response,
			@RequestParam(value="groupId") String groupId,
			@RequestParam(value="dstUserName") String dstUserName) throws IOException {
		log.debug("kickout group - username: " + dstUserName + "groupId: "
				+ groupId);
		GroupModel groupModel = groupManager.getGroup(groupId);
		AttendeeBean attendee = groupModel.getAttendee(dstUserName);
		if (attendee == null) {
			// user are prohibited to join the group for he isn't in the group
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Not Invited!");
			return;
		}
		// update the status
		attendee.setOnlineStatus(OnlineStatus.offline);
		attendee.setVideoStatus(VideoStatus.off);
		
		//update phone call status and hang up this call
		if (attendee.statusHangup()){
			String sipUri = DonkeyClient.generateSipUriFromPhone(dstUserName);
			DonkeyHttpResponse donkeyResp = donkeyClient.unjoinConference(groupModel.getAudioConfId(), sipUri, groupId);
			if (null == donkeyResp || !donkeyResp.isAccepted()){
				log.error("Hangup <" + dstUserName + "> in group <" + groupId + "> failed : " + 
						(null==donkeyResp? "NULL Response" : donkeyResp.getStatusCode()));
			}
		}
		
		groupModel.removeAttendee(dstUserName);
		groupModel.notifyAttendeesToUpdateMemberList();
		
	}

	/**
	 * Attendee join group
	 * 
	 * @param response
	 * @param confId
	 * @param userName
	 * @throws SQLException
	 * @throws IOException
	 * @throws JSONException 
	 */
	@RequestMapping(value = "/join")
	public void join(
			HttpServletResponse response,
			@RequestParam(value="groupId") String groupId, 
			@RequestParam(value="username") String userName) throws SQLException, IOException, JSONException {
		GroupModel group = groupManager.checkAndCreateGroupModel(groupId, userName);
		if (null == group){
			log.error("Cannot join <" + userName + "> to group <"+groupId+
					"> beacause the group is not existed in database.");
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		if (userName.equals(group.getOwnerName())){
			groupDao.makeGroupVisibleForEachAttendee(groupId);
			
			//create audio conference
			String confId = RandomString.genRandomNum(6);
			group.setAudioConfId(confId);
			DonkeyHttpResponse donkeyResp = 
				donkeyClient.createNoControlConference(confId, group.getAllAttendeeName(), groupId);
			if (null == donkeyResp || !donkeyResp.isAccepted()){
				log.error("Create audio conference error : " + 
						(null==donkeyResp? "NULL Response" : donkeyResp.getStatusCode()));
				groupManager.removeGroup(groupId);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Cannot create audio conference");
				return;
			}
			
			//TODO: send iPhone notification; 
		} else {
			AttendeeBean attendee = group.getAttendee(userName);
			// notify other people that User has joined
			group.broadcastAttendeeStatus(attendee);
		}
		
		JSONObject ret = new JSONObject();
		ret.put(GroupConstants.groupId.name(), group.getGroupId());
		ret.put(GroupConstants.audioConfId.name(), group.getAudioConfId());
		ret.put(GroupConstants.owner.name(), group.getOwnerName());
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().print(ret.toString());
	}

	/**
	 * Attendee unjoin conference
	 * 
	 * @param response
	 * @param confId
	 * @param userName
	 * @throws IOException
	 * @throws SQLException 
	 */
	@RequestMapping(value = "/unjoin")
	public void unjoin(
			HttpServletResponse response,
			@RequestParam(value="groupId") String groupId, 
			@RequestParam(value="username") String userName)
			throws IOException, SQLException {
		log.debug("unjoin group - username: " + userName + "groupId: "
				+ groupId);
		GroupModel groupModel = groupManager.getGroup(groupId);
		AttendeeBean attendee = groupModel.getAttendee(userName);
		if (attendee == null) {
			// user are prohibited to join the group for he isn't in the group
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Not Invited!");
			return;
		}
		// update the status
		attendee.setOnlineStatus(OnlineStatus.offline);
		attendee.setVideoStatus(VideoStatus.off);
		
		//update phone call status and hang up this call
		if (attendee.statusHangup()){
			String sipUri = DonkeyClient.generateSipUriFromPhone(userName);
			DonkeyHttpResponse donkeyResp = donkeyClient.unjoinConference(groupModel.getAudioConfId(), sipUri, groupId);
			if (null == donkeyResp || !donkeyResp.isAccepted()){
				log.error("Hangup <" + userName + "> in group <" + groupId + "> failed : " + 
						(null==donkeyResp? "NULL Response" : donkeyResp.getStatusCode()));
			}
		}
		// notify other people that User has unjoined
		groupModel.broadcastAttendeeStatus(attendee);

		groupManager.removeGroupIfEmpty(groupId);
		
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@RequestMapping(value = "/updateAttendeeStatus")
	public void updateAttendeeStatus(
			HttpServletResponse response,
			@RequestParam(value = "groupId") String groupId, 
			@RequestParam(value = "username") String userName,
			@RequestParam(value = "online_status", required = false) String onlineStatus,
			@RequestParam(value = "video_status", required = false) String videoStatus,
			@RequestParam(value = "telephone_status", required = false) String telephoneStatus) throws IOException {
		GroupModel groupModel = groupManager.getGroup(groupId);
		groupModel.updateAttendeeStatus(userName, onlineStatus, videoStatus, telephoneStatus);
	}

	/**
	 * Call phone number of userId.
	 * 
	 * @param response
	 * @param confId
	 * @param username
	 * @throws IOException 
	 */
	@RequestMapping(value = "/call")
	public void call(
			HttpServletResponse response,
			@RequestParam(value="groupId") String groupId, 
			@RequestParam(value="dstUserName") String dstUserName) throws IOException {
		GroupModel group = groupManager.getGroup(groupId);
		AttendeeBean attendee = group.getAttendee(dstUserName);
		if (attendee == null) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Not Invited!");
			return;
		}
		
		//transfer attendee status from Initial to CallWait
		if (!attendee.statusCall()){
			log.error("Cannot call <" + dstUserName + ">, beacuse attendee status is " 
					+ attendee.getPhoneCallStatus());
			response.sendError(HttpServletResponse.SC_CONFLICT, "Conflicted Command!");
			return;
		}
		
		String sipUri = DonkeyClient.generateSipUriFromPhone(dstUserName);
		DonkeyHttpResponse donkeyResp =
			donkeyClient.callAttendee(group.getAudioConfId(), sipUri, groupId);
		if (null == donkeyResp || !donkeyResp.isAccepted()){
			log.error("Call <" + dstUserName + "> in group <" + groupId + "> failed : " + 
					(null==donkeyResp? "NULL Response" : donkeyResp.getStatusCode()));
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
					"Call <" + dstUserName + "> failed!");
			return;
		}
		group.broadcastAttendeeStatus(attendee);
		response.setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * Hang up phone of userId.
	 * 
	 * @param response
	 * @param confId
	 * @param username
	 * @throws IOException 
	 */
	@RequestMapping(value = "/hangup", method = RequestMethod.POST)
	public void hangup(
			HttpServletResponse response,
			@RequestParam(value="groupId") String groupId, 
			@RequestParam(value="dstUserName") String dstUserName) throws IOException {
		GroupModel group = groupManager.getGroup(groupId);
		AttendeeBean attendee = group.getAttendee(dstUserName);
		if (attendee == null) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		
		//transfer attendee status from Initial to CallWait
		if (!attendee.statusHangup()){
			log.error("Cannot hangup <" + dstUserName + ">, beacuse attendee status is " 
					+ attendee.getPhoneCallStatus());
			response.sendError(HttpServletResponse.SC_CONFLICT);
			return;
		}
		
		String sipUri = DonkeyClient.generateSipUriFromPhone(dstUserName);
		DonkeyHttpResponse donkeyResp =
			donkeyClient.hangupAttendee(group.getAudioConfId(), sipUri, groupId);
		if (null == donkeyResp || !donkeyResp.isAccepted()){
			log.error("Hangup <" + dstUserName + "> in group <" + groupId + "> failed : " + 
					(null==donkeyResp? "NULL Response" : donkeyResp.getStatusCode()));
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
					"Hangup <" + dstUserName + "> failed!");
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * Mute phone of userId.
	 * 
	 * @param response
	 * @param confId
	 * @param username
	 */
	@RequestMapping(value = "/mute", method = RequestMethod.POST)
	public void mute(
			HttpServletResponse response,
			@RequestParam(value="groupId") String groupId, 
			@RequestParam(value="username") String userName) {

	}

	/**
	 * Unmute phone of userId.
	 * 
	 * @param response
	 * @param confId
	 * @param username
	 */
	@RequestMapping(value = "/unmute", method = RequestMethod.POST)
	public void unmute(
			HttpServletResponse response,
			@RequestParam(value="groupId") String groupId, 
			@RequestParam(value="username") String userName) {

	}

	@RequestMapping("/editTitle")
	public void editTitle(
			@RequestParam(value = "groupId") String groupId,
			@RequestParam(value = "title") String title,
			HttpServletResponse response) throws SQLException {
		int r = groupDao.editGroupTitle(groupId, title);
		if (1 != r){
			log.error("editTitle for group <" + groupId + "> error");
		}
	}

	@RequestMapping("/hide")
	public void hideGroup(
			HttpServletResponse response,
			@RequestParam(value="groupId") String groupId, 
			@RequestParam(value="username") String userName) throws SQLException {
		int r = groupDao.hideGroup(groupId, userName);
		if (1 != r){
			log.error("hide group <" + groupId + "> for user <" + userName + "> result=" + r);
		}
	}
	
}
