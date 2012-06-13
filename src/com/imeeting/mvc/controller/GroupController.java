package com.imeeting.mvc.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.glassfish.api.Param;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import akka.actor.ActorRef;

import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.group.GroupDB;
import com.imeeting.mvc.model.group.GroupManager;
import com.imeeting.mvc.model.group.GroupModel;
import com.imeeting.mvc.model.group.message.CreateAudioConferenceMsg;
import com.imeeting.mvc.model.group.message.DestroyConferenceMsg;
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
			@RequestParam(value = "attendeelist", required = false) String attendeeList)
			throws IOException, SQLException {
		// /
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
		if (actor == null) {
			log.info("Conference model is null");
		}
		// actor.tell(new CreateAudioConferenceMsg());

		response.setStatus(HttpServletResponse.SC_ACCEPTED);
		response.getWriter().print(groupId);
	}

	/**
	 * destroy group
	 * 
	 * @param response
	 * @param userName
	 * @param confId
	 * @throws IOException
	 * @throws SQLException
	 */
	@Deprecated
	@RequestMapping(value = "/destroy")
	public void destroy(HttpServletResponse response,
			@RequestParam String userName, @RequestParam String confId)
			throws IOException, SQLException {
		// /
		log.debug("destroy");
		GroupManager confManager = ContextLoader.getGroupManager();
		GroupModel conference = confManager.getGroup(confId);
		if (null == conference) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
					"Cannot find conference with ID:" + confId);
			return;
		}

		if (userName != conference.getOwner()) {
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
	 * get attendee list of the conference
	 */
	@Deprecated
	@RequestMapping(value = "/attendeelist")
	public void attendeeList() {

	}

	/**
	 * Add attendees by moderator.
	 * 
	 * @param response
	 * @param groupId
	 * @param attendeeList
	 *            - json array string
	 * @throws IOException
	 * @throws SQLException
	 */
	@RequestMapping(value = "/invite")
	public void invite(HttpServletResponse response,
			@RequestParam String groupId, @RequestParam String attendeeList)
			throws IOException, SQLException {
		GroupModel model = ContextLoader.getGroupManager().getGroup(groupId);
		if (model == null) {
			response.sendError(HttpServletResponse.SC_GONE,
					"group doesn't exist, may be closed.");
			return;
		}

		// insert MySQL
		JSONArray attendees = new JSONArray();
		try {
			attendees = new JSONArray(attendeeList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		GroupDB.insertAttendees(groupId, attendees);

		List<String> attendeesArrayList = new ArrayList<String>();
		for (int i = 0; i < attendees.length(); i++) {
			try {
				attendeesArrayList.add(attendees.getString(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		model.setAttendees(attendeesArrayList);
		// TODO: create actor for attendees
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
	 * Attendee join conference
	 * 
	 * @param response
	 * @param confId
	 * @param userId
	 */
	@RequestMapping(value = "/join")
	public void join(HttpServletResponse response, @RequestParam String groupId,
			@RequestParam String userId) {
		//
	}

	/**
	 * Attendee unjoin conference
	 * 
	 * @param response
	 * @param confId
	 * @param userId
	 */
	@RequestMapping(value = "/unjoin")
	public void unjoin(HttpServletResponse response,
			@RequestParam String groupId, @RequestParam String userId) {

	}

	/**
	 * Call phone number of userId.
	 * 
	 * @param response
	 * @param confId
	 * @param userId
	 */
	@RequestMapping(value = "/call")
	public void call(HttpServletResponse response, @RequestParam String groupId,
			@RequestParam String userId) {

	}

	/**
	 * Hang up phone of userId.
	 * 
	 * @param response
	 * @param confId
	 * @param userId
	 */
	@RequestMapping(value = "/hangup", method = RequestMethod.POST)
	public void hangup(HttpServletResponse response,
			@RequestParam String groupId, @RequestParam String userId) {

	}

	/**
	 * Mute phone of userId.
	 * 
	 * @param response
	 * @param confId
	 * @param userId
	 */
	@RequestMapping(value = "/mute", method = RequestMethod.POST)
	public void mute(HttpServletResponse response, @RequestParam String groupId,
			@RequestParam String userId) {

	}

	/**
	 * Unmute phone of userId.
	 * 
	 * @param response
	 * @param confId
	 * @param userId
	 */
	@RequestMapping(value = "/unmute", method = RequestMethod.POST)
	public void unmute(HttpServletResponse response,
			@RequestParam String groupId, @RequestParam String userId) {

	}

	@RequestMapping("/editTitle")
	public void editTitle(
			@RequestParam(value = "groupId", required = true) String groupId,
			@RequestParam(value = "title", required = true) String title,
			HttpServletResponse response) throws SQLException {
		GroupDB.editGroupTitle(groupId, title);
	}

}
