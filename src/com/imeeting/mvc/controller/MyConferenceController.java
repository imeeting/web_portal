package com.imeeting.mvc.controller;

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
import com.imeeting.mvc.model.conference.ConferenceDB;

@Controller
@RequestMapping("/myconference")
public class MyConferenceController {
	
	private static Log log = LogFactory.getLog(MyConferenceController.class);
	
	private ConferenceDB confDao;
	
	@PostConstruct
	public void init(){
		confDao = ContextLoader.getConferenceDAO();
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		ModelAndView view = new ModelAndView();
		view.setViewName("myconference");
		view.addObject(WebConstants.page_name.name(), "myconference");
		return view;
	}
	
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public ModelAndView list(
			HttpSession session,
			@RequestParam(value = "offset", defaultValue="1") int offset){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("common/_conflist");
		return mv;
	}
}
