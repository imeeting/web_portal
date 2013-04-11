package com.imeeting.mvc.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.imeeting.constants.ConferenceConstants;
import com.imeeting.constants.WebConstants;
import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.conference.ConferenceDB;
import com.imeeting.mvc.model.conference.ConferenceManager;
import com.imeeting.mvc.model.conference.ConferenceModel;
import com.imeeting.mvc.model.contact.ContactBean;
import com.imeeting.mvc.model.contact.ContactDAO;
import com.imeeting.web.user.UserBean;
import com.richitec.donkey.client.DonkeyClient;
import com.richitec.donkey.client.DonkeyHttpResponse;
import com.richitec.ucenter.model.UserDAO;
import com.richitec.util.RandomString;

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
	
	private void sendSMSEmailNotice(String confId, String scheduleTime, JSONArray jsonArray) throws JSONException{
		StringBuffer allPhone = new StringBuffer();
		LinkedList<String> emailList = new LinkedList<String>();
		for(int i=0; i< jsonArray.length(); i++){
			JSONObject attendee = jsonArray.getJSONObject(i);
			String phone = (String)attendee.get("phone");
			if (null != phone && phone.length()>0){
				allPhone.append(phone).append(",");
			}
			String email = (String)attendee.get("email");
			if (null != email && email.length()>0){
				emailList.add(email);
			}
		}
		
		String subject = "电话会议通知";
		String content = "您在" + scheduleTime + "有电话会议，会议密码：" + confId
		+ "，到时拨打 0551-62379997 加入会议。";
		
		try {
			ContextLoader.getSMSClient().sendTextMessage(
					allPhone.toString(), content);
			ContextLoader.getMailSender().sendMail(emailList, subject, content);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
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
		JSONArray jsonArray = new JSONArray(attendeeList);
		if (attendeeList != null && attendeeList.length() > 0) {
			conferenceDao.saveJSONAttendee(conferenceId, jsonArray);
			contactDao.saveJSONContact(user.getUserName(), jsonArray);
		}
		
		sendSMSEmailNotice(conferenceId, scheduleTime, jsonArray);
		
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
		
		sendSMSEmailNotice(conferenceId, scheduleTime, jsonArray);
		
		// step 3. response to user
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
