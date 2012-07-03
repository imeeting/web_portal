package com.imeeting.mvc.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

import akka.actor.ActorRef;

import com.imeeting.constants.GroupConstants;
import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.group.GroupDB;
import com.imeeting.mvc.model.group.GroupDB.GroupStatus;
import com.imeeting.mvc.model.group.GroupManager;
import com.imeeting.mvc.model.group.GroupModel;
import com.imeeting.mvc.model.group.attendee.AttendeeBean;
import com.imeeting.mvc.model.group.attendee.AttendeeBean.OnlineStatus;
import com.imeeting.mvc.model.group.attendee.AttendeeBean.TelephoneStatus;
import com.imeeting.mvc.model.group.attendee.AttendeeBean.VideoStatus;
import com.imeeting.mvc.model.group.message.AddAttendeesToGroupMessage;
import com.imeeting.mvc.model.group.message.DestroyConferenceMsg;
import com.imeeting.mvc.model.group.message.LoadGroupAttendeesMsg;
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
public class GroupController {
	public static final int PageSize = 20;

	private static Log log = LogFactory.getLog(GroupController.class);

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
			@RequestParam(value = "moderator", required = false) String moderator,
			@RequestParam(value = "attendees", required = false) String attendeeList)
			throws IOException, SQLException, JSONException {
		log.debug("create");
		String groupId = RandomString.genRandomNum(8);
		int r = GroupDB.insert(groupId, userName);
		if (r != 1) {
			log.error("Database operation error: cannot insert (" + groupId
					+ "," + userName + ")");
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		GroupDB.insertAttendee(groupId, userName);
		
		GroupManager groupManager = ContextLoader.getGroupManager();
		ActorRef actor = groupManager.createGroup(groupId, userName);
		// actor.tell(new CreateAudioConferenceMsg());

		// process attendees
		if (attendeeList != null) {
			actor.tell(new AddAttendeesToGroupMessage(attendeeList));
		}
		
		
		response.setStatus(HttpServletResponse.SC_CREATED);
		JSONObject ret = new JSONObject();
		ret.put(GroupConstants.groupId.name(), groupId);
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
		GroupManager confManager = ContextLoader.getGroupManager();
		GroupModel conference = confManager.getGroup(groupId);
		if (null == conference) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
					"Cannot find conference with ID:" + groupId);
			return;
		}

		if (username != conference.getOwner()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"userName and confId are not match");
			return;
		}

		conference.tell(new DestroyConferenceMsg());
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
			@RequestParam(value = "username") String username)
			throws IOException, SQLException, JSONException {

		int count = GroupDB.getGroupTotalCount(username);
		JSONArray confs = GroupDB.geGroupList(username, offset, PageSize);

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
	public void attendeeList(@RequestParam String groupId,
			HttpServletResponse response) throws IOException {
		GroupModel model = ContextLoader.getGroupManager().getGroup(groupId);
		if (model == null) {
			response.sendError(HttpServletResponse.SC_GONE,
					"group doesn't exist, may be closed.");
			return;
		}

		List<AttendeeBean> attendees = model.getAttendees();
		JSONArray ret = new JSONArray();
		if (attendees != null) {
			for (AttendeeBean att : attendees) {
				ret.put(att.toJson());
			}
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
	 */
	@RequestMapping(value = "/invite")
	public void invite(HttpServletResponse response,
			@RequestParam String groupId, @RequestParam String attendees)
			throws IOException, SQLException {
		log.info("invite attendees");
		GroupModel model = ContextLoader.getGroupManager().getGroup(groupId);
		if (model == null) {
			response.sendError(HttpServletResponse.SC_GONE,
					"group doesn't exist, may be closed.");
			return;
		}

		if (attendees != null) {
			model.tell(new AddAttendeesToGroupMessage(attendees));
		}
	}

	/**
	 * Moderator can kick out any attendee of his conference.
	 * 
	 */
	@RequestMapping(value = "/kickout")
	public void kickout(HttpServletResponse response,
			@RequestParam String groupId, @RequestParam String attendeeId) {

	}

	/**
	 * Attendee join group
	 * 
	 * @param response
	 * @param confId
	 * @param username
	 * @throws SQLException
	 * @throws IOException
	 */
	@RequestMapping(value = "/join")
	public void join(HttpServletResponse response,
			@RequestParam String groupId, @RequestParam String username)
			throws SQLException, IOException {
		GroupManager groupManager = ContextLoader.getGroupManager();
		GroupModel groupModel = groupManager.getGroup(groupId);
		if (groupModel == null) {
			// currently there is no existing group in the memory
			// the first one joining the group will be the owner

			// update the group in db
			int rows = GroupDB.updateOwnerAndStatus(groupId, username,
					GroupStatus.OPEN);
			if (rows <= 0) {
				// no group exists in the db, return error
				log.error("no existed group found in db");
				response.sendError(HttpServletResponse.SC_NOT_FOUND,
						"No Existed Group Found!");
				return;
			}

			// create in memory
			ActorRef actor = groupManager.createGroup(groupId, username);
			actor.tell(new LoadGroupAttendeesMsg());
			// actor.tell(new CreateAudioConferenceMsg());

			response.setStatus(HttpServletResponse.SC_OK);

		} else {
			// other people join the group
			AttendeeBean attendee = groupModel.findAttendee(username);
			if (attendee == null) {
				// user are prohibited to join the group for he isn't in the
				// group
				response.sendError(HttpServletResponse.SC_FORBIDDEN,
						"Not Invited!");
				return;
			}

			// update the status
			attendee.setOnlineStatus(OnlineStatus.online);
			// notify other people that User has joined
			groupModel.broadcastAttendeeStatus(attendee);

			response.setStatus(HttpServletResponse.SC_OK);
		}
	}

	/**
	 * Attendee unjoin conference
	 * 
	 * @param response
	 * @param confId
	 * @param username
	 * @throws IOException
	 */
	@RequestMapping(value = "/unjoin")
	public void unjoin(HttpServletResponse response,
			@RequestParam String groupId, @RequestParam String username)
			throws IOException {
		log.debug("unjoin group - username: " + username + "groupId: "
				+ groupId);
		GroupManager groupManager = ContextLoader.getGroupManager();
		GroupModel groupModel = groupManager.getGroup(groupId);
		if (groupModel == null) {
			response.sendError(HttpServletResponse.SC_GONE,
					"group doesn't exist, may be closed.");
			return;
		}
		AttendeeBean attendee = groupModel.findAttendee(username);
		if (attendee == null) {
			// user are prohibited to join the group for he isn't in the group
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Not Invited!");
			return;
		}
		// update the status
		attendee.setOnlineStatus(OnlineStatus.offline);
		attendee.setTelephoneStatus(TelephoneStatus.idle);
		attendee.setVideoStatus(VideoStatus.off);
		// notify other people that User has unjoined
		groupModel.broadcastAttendeeStatus(attendee);

		response.setStatus(HttpServletResponse.SC_OK);
	}

	@RequestMapping(value = "/updateAttendeeStatus")
	public void updateAttendeeStatus(HttpServletResponse response,
			@RequestParam String groupId, @RequestParam String username,
			@RequestParam(value = "online_status", required = false) String onlineStatus,
			@RequestParam(value = "video_status", required = false) String videoStatus,
			@RequestParam(value = "telephone_status", required = false) String telephoneStatus) throws IOException {
		GroupManager groupManager = ContextLoader.getGroupManager();
		GroupModel groupModel = groupManager.getGroup(groupId);
		if (groupModel == null) {
			response.sendError(HttpServletResponse.SC_GONE,
					"group doesn't exist, may be closed.");
			return;
		}
		groupModel.updateAttendeeStatus(username, onlineStatus, videoStatus, telephoneStatus);
	}

	/**
	 * Call phone number of userId.
	 * 
	 * @param response
	 * @param confId
	 * @param username
	 */
	@RequestMapping(value = "/call")
	public void call(HttpServletResponse response,
			@RequestParam String groupId, @RequestParam String username) {

	}

	/**
	 * Hang up phone of userId.
	 * 
	 * @param response
	 * @param confId
	 * @param username
	 */
	@RequestMapping(value = "/hangup", method = RequestMethod.POST)
	public void hangup(HttpServletResponse response,
			@RequestParam String groupId, @RequestParam String username) {

	}

	/**
	 * Mute phone of userId.
	 * 
	 * @param response
	 * @param confId
	 * @param username
	 */
	@RequestMapping(value = "/mute", method = RequestMethod.POST)
	public void mute(HttpServletResponse response,
			@RequestParam String groupId, @RequestParam String username) {

	}

	/**
	 * Unmute phone of userId.
	 * 
	 * @param response
	 * @param confId
	 * @param username
	 */
	@RequestMapping(value = "/unmute", method = RequestMethod.POST)
	public void unmute(HttpServletResponse response,
			@RequestParam String groupId, @RequestParam String username) {

	}

	@RequestMapping("/editTitle")
	public void editTitle(
			@RequestParam(value = "groupId", required = true) String groupId,
			@RequestParam(value = "title", required = true) String title,
			HttpServletResponse response) throws SQLException {
		GroupDB.editGroupTitle(groupId, title);
	}

	@RequestMapping("/hide")
	public void hideGroup(@RequestParam String groupId,
			@RequestParam String username, HttpServletResponse response)
			throws SQLException {
		GroupDB.hideGroup(groupId, username);
	}
}
