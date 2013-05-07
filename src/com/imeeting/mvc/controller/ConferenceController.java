package com.imeeting.mvc.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.imeeting.constants.AttendeeConstants;
import com.imeeting.constants.ConferenceConstants;
import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.conference.ConferenceBean;
import com.imeeting.mvc.model.conference.ConferenceDB;
import com.imeeting.mvc.model.conference.ConferenceManager;
import com.imeeting.mvc.model.conference.ConferenceModel;
import com.imeeting.mvc.model.conference.ConferenceDB.ConferenceStatus;
import com.imeeting.mvc.model.conference.attendee.AttendeeBean;
import com.imeeting.mvc.model.conference.attendee.AttendeeModel;
import com.richitec.donkey.client.DonkeyClient;
import com.richitec.donkey.client.DonkeyHttpResponse;
import com.richitec.util.Pager;
import com.richitec.util.RandomString;

/**
 * 
 * client --- create conference request --> Server client <-- response with
 * confId --- Server client --- attendee list --> Server client <-- response
 * from server --- Server client send short message to attendees.
 * 
 * @author huuguanghui
 * 
 */

@Controller
@RequestMapping(value = "/conference")
public class ConferenceController extends ExceptionController {
	public static final int PageSize = 20;

	private static Log log = LogFactory.getLog(ConferenceController.class);

	private ConferenceManager conferenceManager;
	private DonkeyClient donkeyClient;
	private ConferenceDB conferenceDao;

	@PostConstruct
	public void init() {
		conferenceManager = ContextLoader.getConferenceManager();
		donkeyClient = ContextLoader.getDonkeyClient();
		conferenceDao = ContextLoader.getConferenceDAO();
	}

	@RequestMapping(value = "/generateConfId")
	public void generateConfId(HttpServletResponse response)
			throws JSONException, IOException {
		String conferenceId = RandomString.genRandomNum(6);
		JSONObject ret = new JSONObject();
		ret.put("conferenceId", conferenceId);
		response.getWriter().print(ret.toString());
	}

	@RequestMapping(value = "/schedule", method = RequestMethod.POST)
	public void schedule(
			HttpServletResponse response,
			@RequestParam(value = "conferenceId") String conferenceId,
			@RequestParam(value = "username") String userId,
			@RequestParam(value = "attendees", required = false) String attendeeList,
			@RequestParam(value = "scheduleTime", required = true) String scheduleTime)
			throws JSONException, IOException {
		// step 1. save conference
		// String conferenceId = RandomString.genRandomNum(6);
		conferenceDao.saveScheduledConference(conferenceId, scheduleTime,
				userId);

		// step 2. save attendees
		JSONArray jsonArray = new JSONArray(attendeeList);
		if (attendeeList != null && attendeeList.length() > 0) {
			conferenceDao.saveJSONAttendee(conferenceId, jsonArray);
		}

		conferenceManager.sendSMSEmailNotice(conferenceId, scheduleTime,
				jsonArray);

		// step 3. response to user
		JSONObject ret = new JSONObject();
		ret.put(ConferenceConstants.conferenceId.name(), conferenceId);
		ret.put(ConferenceConstants.scheduled_time.name(), scheduleTime);
		response.setStatus(HttpServletResponse.SC_CREATED);
		response.getWriter().print(ret.toString());
	}

	@RequestMapping(value = "/scheduleNow", method = RequestMethod.POST)
	public void scheduleNow(
			HttpServletResponse response,
			@RequestParam(value = "conferenceId") String conferenceId,
			@RequestParam(value = "username") String userId,
			@RequestParam(value = "attendees", required = false) String attendeeList)
			throws JSONException, IOException {
		// save conference
		// String conferenceId = RandomString.genRandomNum(6);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date now = new Date();
		String scheduleTime = df.format(now);
		conferenceDao.saveScheduledConference(conferenceId, scheduleTime,
				userId);

		// create audio conference
		ConferenceModel conference = conferenceManager.creatConference(
				conferenceId, userId);

		JSONArray jsonArray = null;
		if (attendeeList != null && attendeeList.length() > 0) {
			jsonArray = new JSONArray(attendeeList);
			// save attendees
			conferenceDao.saveJSONAttendee(conferenceId, jsonArray);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject attObj = jsonArray.getJSONObject(i);
				try {
					String phone = attObj.getString(AttendeeConstants.phone
							.name());
					if (phone != null && !"".equals(phone)) {
						AttendeeModel attendee = new AttendeeModel(phone);
						conference.addAttendee(attendee);
						attendee.setNickname(attObj
								.getString(AttendeeConstants.nickname.name()));
					}
				} catch (Exception e) {
				}
			}
		}

		conference.setAudioConfId(conferenceId);
		DonkeyHttpResponse donkeyResp = donkeyClient
				.createNoControlConference(conferenceId, "",
						conference.getAllAttendeeName(), conferenceId);
		if (null == donkeyResp || !donkeyResp.isAccepted()) {
			log.error("Create audio conference error : "
					+ (null == donkeyResp ? "NULL Response" : donkeyResp
							.getStatusCode()));
			conferenceManager.removeConference(conferenceId);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Cannot create audio conference");
			return;
		}
		conferenceDao.updateStatus(conferenceId, ConferenceStatus.OPEN);
		conferenceManager.sendSMSEmailNotice(conferenceId, scheduleTime,
				jsonArray);

		// step 3. response to user
		JSONObject ret = new JSONObject();
		ret.put(ConferenceConstants.conferenceId.name(), conferenceId);
		response.setStatus(HttpServletResponse.SC_CREATED);
		response.getWriter().print(ret.toString());
	}

	/**
	 * Get all conferences by user name.
	 * 
	 * @param response
	 * @param username
	 * @throws IOException
	 * @throws SQLException
	 * @throws JSONException
	 */
	@RequestMapping(value = "/conflist")
	public void confList(
			HttpServletResponse response,
			@RequestParam(value = "offset", required = false, defaultValue = "1") int offset,
			@RequestParam(value = "username") String userId)
			throws IOException, DataAccessException, JSONException {

		int count = conferenceDao.getAllConferenceCount(userId);
		List<ConferenceBean> confList = conferenceDao.getConferenceList(userId,
				offset, PageSize);

		if (confList.size() > 0) {
			List<String> confIdList = new ArrayList<String>();
			for (ConferenceBean b : confList) {
				confIdList.add(b.getId());
			}

			List<AttendeeBean> attendeeList = conferenceDao
					.getConferenceAttendees(confIdList);

			for (AttendeeBean attendee : attendeeList) {
				for (ConferenceBean conf : confList) {
					if (attendee.getConfId().equals(conf.getId())) {
						conf.addAttendee(attendee);
						break;
					}
				}
			}
		}
		JSONArray confs = new JSONArray();
		for (ConferenceBean conf : confList) {
			confs.put(conf.toJSONObject());
		}

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
	 * get attendee list of the conference which has been opened already
	 * 
	 * @param conferenceId
	 * @param response
	 * 
	 * @throws IOException
	 */
	@RequestMapping(value = "/attendeeList")
	public void attendeeList(HttpServletResponse response,
			@RequestParam String conferenceId) throws IOException {
		ConferenceModel model = conferenceManager.getConference(conferenceId);
		JSONArray ret = new JSONArray();
		if (model != null) {
			Collection<AttendeeModel> attendees = model.getAllAttendees();
			if (attendees != null && attendees.size() > 0) {
				for (AttendeeModel att : attendees) {
					ret.put(att.toJson());
				}
			} else {
				log.error("no attendees in conference <" + conferenceId + ">");
			}
		} else {
			List<String> confIdList = new ArrayList<String>();
			confIdList.add(conferenceId);
			List<AttendeeBean> attendees = conferenceDao
					.getConferenceAttendees(confIdList);
			if (attendees != null) {
				for (AttendeeBean attendee : attendees) {
					ret.put(attendee.toJSONObject());
				}
			}
		}

		response.getWriter().print(ret.toString());
	}

	/**
	 * Add attendees by moderator.
	 * 
	 * @param response
	 * @param conferenceId
	 * @param attendees
	 *            - json array string
	 * @throws IOException
	 * @throws SQLException
	 * @throws JSONException
	 */
	@RequestMapping(value = "/invite")
	public void invite(HttpServletResponse response,
			@RequestParam String conferenceId, @RequestParam String attendees)
			throws IOException, JSONException {
		log.info("invite attendees");
		if (null == attendees || attendees.length() < 0) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		List<AttendeeModel> addedAttendeeList = new LinkedList<AttendeeModel>();
		List<String> addedAttendeeNameList = new LinkedList<String>();
		ConferenceModel conference = conferenceManager
				.getConference(conferenceId);

		JSONArray attendeesJsonArray = new JSONArray(attendees);
		for (int i = 0; i < attendeesJsonArray.length(); i++) {
			JSONObject attObj = attendeesJsonArray.getJSONObject(i);
			String phone = attObj.getString(AttendeeConstants.phone.name());
			String nickName = attObj.getString(AttendeeConstants.nickname.name());
			AttendeeModel attendee = new AttendeeModel(phone);
			attendee.setNickname(nickName);
			if (conference != null) {
				if (!conference.containsAttendee(phone)) {
					conference.addAttendee(attendee);
					addedAttendeeList.add(attendee);
					addedAttendeeNameList.add(phone);
				}
			} else {
				addedAttendeeList.add(attendee);
			}
		}
		if (conference != null) {
			conference.fillNicknameForEachAttendee();
		}

		if (addedAttendeeList.size() > 0) {
			conferenceDao.saveAttendees(conferenceId, addedAttendeeList);
		}

		// notify all attendees to update attendee list
		if (conference != null) {
			conference.notifyAttendeesToUpdateMemberList();
		}

		response.setStatus(HttpServletResponse.SC_OK);
	}

}
