package com.imeeting.mvc.controller;


import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

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
import com.imeeting.web.user.UserBean;

@Controller
@RequestMapping(value="/webconf")
public class WebConferenceController {
	
	private ConferenceManager conferenceManager;
	private ConferenceDB conferenceDao;
	
	@PostConstruct
	public void init(){
		conferenceManager = ContextLoader.getConferenceManager();
		conferenceDao = ContextLoader.getConferenceDAO();
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public String join() {
		return "webconf/join";
	}

	@RequestMapping(method=RequestMethod.POST)
	public ModelAndView conference(
			HttpSession session,
			@RequestParam(value="confId") String confId){
		UserBean user = (UserBean) session.getAttribute(UserBean.SESSION_BEAN);
		ModelAndView mv = new ModelAndView();
		ConferenceModel conference = conferenceManager.getConference(confId);
		if (null == conference){
			mv.setViewName("webconf/join");
			return mv;
		}
		
		AttendeeModel attendee = new AttendeeModel(user.getName());
		attendee.setOnlineStatus(AttendeeModel.OnlineStatus.online);
		conference.addAttendee(attendee);
		conferenceDao.saveAttendee(confId, user.getName());
		
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
}
