package com.imeeting.mvc.controller;


import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.conference.ConferenceDB;
import com.imeeting.mvc.model.conference.ConferenceManager;
import com.imeeting.mvc.model.conference.ConferenceModel;
import com.imeeting.mvc.model.conference.attendee.AttendeeModel;
import com.imeeting.mvc.model.conference.attendee.AttendeeModel.OnlineStatus;
import com.imeeting.mvc.model.conference.attendee.AttendeeModel.VideoStatus;
import com.imeeting.web.user.UserBean;
import com.richitec.donkey.client.DonkeyClient;
import com.richitec.donkey.client.DonkeyHttpResponse;

@Controller
@RequestMapping(value="/webconf")
public class WebConferenceController {
	
	private static Log log = LogFactory.getLog(WebConferenceController.class);
	
	private ConferenceManager conferenceManager;
	private ConferenceDB conferenceDao;
	private DonkeyClient donkeyClient;
	
	@PostConstruct
	public void init(){
		conferenceManager = ContextLoader.getConferenceManager();
		conferenceDao = ContextLoader.getConferenceDAO();
		donkeyClient = ContextLoader.getDonkeyClient();
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public String join() {
		return "webconf/join";
	}

	@RequestMapping(method=RequestMethod.POST)
	public ModelAndView join(
			HttpSession session,
			@RequestParam(value="confId") String confId){
		UserBean user = (UserBean) session.getAttribute(UserBean.SESSION_BEAN);
		ModelAndView mv = new ModelAndView();
		ConferenceModel conference = conferenceManager.getConference(confId);
		if (null == conference){
			mv.setViewName("webconf/join");
			return mv;
		}
		
		AttendeeModel attendee = conference.getAttendee(user.getName());
		if (null == attendee){
			attendee = new AttendeeModel(user.getName());
			conference.addAttendee(attendee);
			conferenceDao.saveAttendee(confId, user.getName());
		}
		
		attendee.setOnlineStatus(AttendeeModel.OnlineStatus.online);
		// notify all attendees to update attendee list
		conference.notifyAttendeesToUpdateMemberList();
		
		mv.addObject("conference", conference);
		mv.setViewName("webconf/conf");
		return mv;
	}
	
	@RequestMapping(value="create")
	public ModelAndView create(){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("webconf/create");
		return mv;
	}
	
	@RequestMapping(value="unjoin", method=RequestMethod.GET)
	public String unjoin(
			HttpSession session,
			HttpServletResponse response,
			@RequestParam(value="confId") String confId) throws IOException{
		UserBean user = (UserBean) session.getAttribute(UserBean.SESSION_BEAN);
		ConferenceModel conferenceModel = conferenceManager.getConference(confId);
		AttendeeModel attendee = conferenceModel.getAttendee(user.getName());
		if (attendee == null) {
			// user are prohibited to join the conference for he isn't in it
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Not Invited!");
		} else {
			// update the status
			attendee.setOnlineStatus(OnlineStatus.offline);
			attendee.setVideoStatus(VideoStatus.off);
			
			// notify other people that User has unjoined
			conferenceModel.broadcastAttendeeStatus(attendee);
	
			conferenceManager.removeConferenceIfEmpty(confId);
		}
		
		return "redirect:/myconference";
	}
	
	@RequestMapping(value = "/call")
	public void call(HttpServletResponse response,
			@RequestParam(value = "conferenceId") String conferenceId,
			@RequestParam(value = "dstUserName") String dstUserName)
			throws IOException {
		ConferenceModel conference = conferenceManager
				.getConference(conferenceId);
		AttendeeModel attendee = conference.getAttendee(dstUserName);
		if (attendee == null) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Not Invited!");
			return;
		}

		// transfer attendee status from Initial to CallWait
		if (!attendee.statusCall()) {
			log.error("Cannot call <" + dstUserName
					+ ">, beacuse attendee status is "
					+ attendee.getPhoneCallStatus());
			response.sendError(HttpServletResponse.SC_CONFLICT,
					"Conflicted Command!");
			return;
		}

		String sipUri = DonkeyClient.generateSipUriFromPhone(dstUserName);
		DonkeyHttpResponse donkeyResp = donkeyClient.callAttendee(
				conference.getAudioConfId(), sipUri, conferenceId);
		if (null == donkeyResp || !donkeyResp.isAccepted()) {
			attendee.statusCallTerminated();
			log.error("Call <"
					+ dstUserName
					+ "> in conferece <"
					+ conferenceId
					+ "> failed : "
					+ (null == donkeyResp ? "NULL Response" : donkeyResp
							.getStatusCode()));
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Call <" + dstUserName + "> failed!");
			return;
		}
		conference.broadcastAttendeeStatus(attendee);
		response.setStatus(HttpServletResponse.SC_OK);
	}	
	
	@RequestMapping(value = "/hangup", method = RequestMethod.POST)
	public void hangup(HttpServletResponse response,
			@RequestParam(value = "conferenceId") String conferenceId,
			@RequestParam(value = "dstUserName") String dstUserName)
			throws IOException {
		ConferenceModel conference = conferenceManager
				.getConference(conferenceId);
		AttendeeModel attendee = conference.getAttendee(dstUserName);
		if (attendee == null) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		// transfer attendee status from Initial to CallWait
		if (!attendee.statusHangup()) {
			log.error("Cannot hangup <" + dstUserName
					+ ">, beacuse attendee status is "
					+ attendee.getPhoneCallStatus());
			response.sendError(HttpServletResponse.SC_CONFLICT);
			return;
		}

		String sipUri = DonkeyClient.generateSipUriFromPhone(dstUserName);
		DonkeyHttpResponse donkeyResp = donkeyClient.hangupAttendee(
				conference.getAudioConfId(), sipUri, conferenceId);
		if (null == donkeyResp || !donkeyResp.isAccepted()) {
			log.error("Hangup <"
					+ dstUserName
					+ "> in conference <"
					+ conferenceId
					+ "> failed : "
					+ (null == donkeyResp ? "NULL Response" : donkeyResp
							.getStatusCode()));
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Hangup <" + dstUserName + "> failed!");
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}	
}
