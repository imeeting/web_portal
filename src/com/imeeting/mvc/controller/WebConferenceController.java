package com.imeeting.mvc.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.imeeting.constants.ConferenceConstants;
import com.imeeting.constants.WebConstants;
import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.addressbook.ContactBean;
import com.imeeting.mvc.model.addressbook.ContactDAO;
import com.imeeting.mvc.model.conference.ConferenceDB;
import com.imeeting.mvc.model.conference.ConferenceManager;
import com.imeeting.mvc.model.conference.ConferenceModel;
import com.imeeting.mvc.model.conference.attendee.AttendeeModel;
import com.imeeting.mvc.model.conference.attendee.AttendeeModel.OnlineStatus;
import com.imeeting.web.user.UserBean;
import com.richitec.donkey.client.DonkeyClient;
import com.richitec.donkey.client.DonkeyHttpResponse;
import com.richitec.ucenter.model.UserDAO;
import com.richitec.util.RandomString;
import com.richitec.vos.client.VOSClient;

@Controller
@RequestMapping(value = "/webconf")
public class WebConferenceController {

	private static Log log = LogFactory.getLog(WebConferenceController.class);

	private ConferenceManager conferenceManager;
	private ConferenceDB conferenceDao;
	private DonkeyClient donkeyClient;
	private ContactDAO contactDao;
	private UserDAO userDao;

	@PostConstruct
	public void init() {
		conferenceManager = ContextLoader.getConferenceManager();
		conferenceDao = ContextLoader.getConferenceDAO();
		donkeyClient = ContextLoader.getDonkeyClient();
		contactDao = ContextLoader.getContactDAO();
		userDao = ContextLoader.getUserDAO();
	}

	@RequestMapping(value = "arrange")
	public ModelAndView arrange(HttpSession session) {
		ModelAndView mv = new ModelAndView();
		UserBean user = (UserBean) session.getAttribute(UserBean.SESSION_BEAN);
		List<ContactBean> contactList = contactDao.getContactList(user.getUserName());
		mv.addObject(WebConstants.addressbook.name(), contactList);
		mv.setViewName("webconf/arrange");
		return mv;
	}
	
	@RequestMapping(value="schedule", method=RequestMethod.POST)
	public void schedule(
			HttpSession session,
			HttpServletResponse response,
			@RequestParam(value = "attendees", required = false) String attendeeList,
			@RequestParam(value="scheduleTime", required = true) String scheduleTime )
			throws JSONException, IOException {
		//step 1. save conference
		UserBean user = (UserBean) session.getAttribute(UserBean.SESSION_BEAN);
		String conferenceId = RandomString.genRandomNum(6);
		conferenceDao.saveScheduledConference(conferenceId, scheduleTime, user.getUserName());
		
		// step 2. save attendees
		if (attendeeList != null && attendeeList.length() > 0) {
			JSONArray jsonArray = new JSONArray(attendeeList);
			conferenceDao.saveJSONAttendee(conferenceId, jsonArray);
		}
		
		//TODO: save attendees to contact database.
		//TODO: send Email or SMS to all attendess. 
		
		// step 3. response to user
		JSONObject ret = new JSONObject();
		ret.put(ConferenceConstants.conferenceId.name(), conferenceId);
		ret.put(ConferenceConstants.schedule_time.name(), scheduleTime);
		response.setStatus(HttpServletResponse.SC_CREATED);
		response.getWriter().print(ret.toString());
	}
	
	@RequestMapping(value="scheduleNow", method=RequestMethod.POST)
	public void scheduleNow(HttpSession session,
			HttpServletResponse response,
			@RequestParam(value = "attendees", required = false) String attendeeList) 
			throws JSONException, IOException{
		//step 1. save conference
		UserBean user = (UserBean) session.getAttribute(UserBean.SESSION_BEAN);
		String conferenceId = RandomString.genRandomNum(6);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date now = new Date();
		String scheduleTime = df.format(now);
		conferenceDao.saveScheduledConference(conferenceId, scheduleTime, user.getUserName());
		
		// step 2. save attendees
		JSONArray jsonArray = new JSONArray(attendeeList);
		if (attendeeList != null && attendeeList.length() > 0) {
			conferenceDao.saveJSONAttendee(conferenceId, jsonArray);
		}
		
		// save attendees to contact database.
		contactDao.saveJSONContact(user.getUserName(), jsonArray);
		
		// step 3. create audio conference
		ConferenceModel conference = conferenceManager.creatConference(
				conferenceId, user.getUserName());
		conference.setAudioConfId(conferenceId);
		Integer vosPhoneNumber = userDao.getVOSPhoneNumber(user.getUserName());
		DonkeyHttpResponse donkeyResp = donkeyClient.createNoControlConference(
				conferenceId, vosPhoneNumber.toString(),
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
		session.setAttribute(ConferenceConstants.conferenceId.name(), conferenceId);
		
		//TODO: send Email or SMS to all attendess. 
		
		// step 3. response to user
		JSONObject ret = new JSONObject();
		ret.put(ConferenceConstants.conferenceId.name(), conferenceId);
		response.setStatus(HttpServletResponse.SC_CREATED);
		response.getWriter().print(ret.toString());
	}

	@RequestMapping(value = "create", method=RequestMethod.POST)
	public void create(
			HttpServletResponse response,
			HttpSession session,
			@RequestParam(value = "attendees", required = false) String attendeeList)
			throws JSONException, IOException {
		log.info("attendees: " + attendeeList);
		UserBean user = (UserBean) session.getAttribute(UserBean.SESSION_BEAN);
		
		// step 1. create ConferenceModel in memory
		String conferenceId = RandomString.genRandomNum(6);
		ConferenceModel conference = conferenceManager.creatConference(
				conferenceId, user.getUserName());

		AttendeeModel owner = new AttendeeModel(user.getUserName(),
				OnlineStatus.online);
		conference.addAttendee(owner);

		if (attendeeList != null && attendeeList.length() > 0) {
			try {
				JSONArray attendeesJsonArray = new JSONArray(attendeeList);
				for (int i = 0; i < attendeesJsonArray.length(); i++) {
					JSONObject attendee = attendeesJsonArray.getJSONObject(i);
					String nickName = attendee.getString("nickname");
					String phone = attendee.getString("phone");
					String email = attendee.getString("email");
					Boolean save = attendee.getBoolean("save");
					conference.addAttendee(new AttendeeModel(phone));
				}
				conference.fillNicknameForEachAttendee();
				conference.sendSMSToAttendees(attendeesJsonArray);
			} catch (JSONException e) {
				log.error("\nCannot parse attendees : " + attendeeList);
				conferenceManager.removeConference(conferenceId);
				throw e;
			}
		}

		// step 2. save ConferenceModel in Database.
		try {
			conferenceDao.saveConference(conference, user.getUserName());
		} catch (DataAccessException e) {
			log.error("\nSave conference <" + conferenceId
					+ "> to database error : \n" + "Message : "
					+ e.getMessage());
			conferenceManager.removeConference(conferenceId);
			throw e;
		}

		// step 3. create audio conference
		conference.setAudioConfId(conferenceId);
		Integer vosPhoneNumber = userDao.getVOSPhoneNumber(user.getUserName());
		DonkeyHttpResponse donkeyResp = donkeyClient.createNoControlConference(
				conferenceId, vosPhoneNumber.toString(),
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
		session.setAttribute(ConferenceConstants.conferenceId.name(), conferenceId);
		// step 4. response to user
		JSONObject ret = new JSONObject();
		ret.put(ConferenceConstants.conferenceId.name(), conferenceId);
		response.setStatus(HttpServletResponse.SC_CREATED);
		response.getWriter().print(ret.toString());
	}
	
	@RequestMapping(value="ajax", method=RequestMethod.GET)
	public ModelAndView show(HttpSession session,
	        @RequestParam(value = "confId") String confId){
	    ModelAndView mv = new ModelAndView();
	    ConferenceModel conference = conferenceManager.getConference(confId);
        mv.addObject("conference", conference);
        mv.setViewName("webconf/conf");
        return mv;
	}
	
	@RequestMapping(value="ajax", method=RequestMethod.POST)
	public @ResponseBody String joinByAjax(HttpSession session,
	        @RequestParam(value = "confId") String confId) throws JSONException{
	    JSONObject result = new JSONObject();
        ConferenceModel conference = conferenceManager.getConference(confId);
        if (null == conference) {
            result.put("result", "noconference");
        } else {
        	result.put("result", "success");
        }

	    return result.toString();
	}
	
	@RequestMapping(value = "/attendeeList")
	public ModelAndView attendeeList(@RequestParam String conferenceId)
			throws IOException {
		ConferenceModel conference = conferenceManager
				.getConference(conferenceId);
		ModelAndView mv = new ModelAndView();
		mv.addObject("conference", conference);
		mv.setViewName("webconf/attendeelist");
		return mv;
	}

}
