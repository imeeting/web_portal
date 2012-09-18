package com.imeeting.mvc.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.imeeting.constants.WebConstants;
import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.conference.ConferenceBean;
import com.imeeting.mvc.model.conference.ConferenceDB;
import com.imeeting.mvc.model.conference.attendee.AttendeeBean;
import com.imeeting.web.user.UserBean;
import com.richitec.util.Pager;

@Controller
@RequestMapping("/myconference")
public class MyConferenceController {
	public static final int PageSize = 5;
	private static Log log = LogFactory.getLog(MyConferenceController.class);
	
	private ConferenceDB confDao;
	
	@PostConstruct
	public void init(){
		confDao = ContextLoader.getConferenceDAO();
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index(HttpSession session) {
		UserBean user = (UserBean) session.getAttribute(UserBean.SESSION_BEAN);
		int confCount = confDao.getAllConferenceCount(user.getUserName());
		
		ModelAndView view = new ModelAndView();
		view.setViewName("myconference");
		view.addObject(WebConstants.page_name.name(), "myconference");
		view.addObject("confCount", confCount);
		return view;
	}
	
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public ModelAndView list(
			HttpSession session,
			@RequestParam(value="offset", defaultValue="1") int offset){
		UserBean user = (UserBean) session.getAttribute(UserBean.SESSION_BEAN);
		List<ConferenceBean> confList = 
			confDao.getConferenceList(user.getUserName(), offset, PageSize);
		
		if (confList.size() > 0){
			List<String> confIdList = new ArrayList<String>();
			for (ConferenceBean b : confList){
				confIdList.add(b.getId());
			}
			
			List<AttendeeBean> attendeeList = confDao.getConferenceAttendees(confIdList);
			
			for (AttendeeBean attendee : attendeeList){
				for(ConferenceBean conf : confList){
					if (attendee.getConfId().equals(conf.getId())){
						conf.addAttendee(attendee);
						break;
					}
				}
			}
		}
		
		int count = confDao.getAllConferenceCount(user.getUserName());
		Pager pager = new Pager(offset, PageSize, count, "myconference/list?");
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("common/_conflist");
		mv.addObject(WebConstants.pager.name(), pager);
		mv.addObject(WebConstants.conf_list.name(), confList);
		return mv;
	}
}
