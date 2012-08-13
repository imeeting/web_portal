package com.imeeting.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IMeetingWebController {
	private static Log log = LogFactory.getLog(IMeetingWebController.class);
	public static final String PAGE_NAME = "page_name";
	
	@RequestMapping("/")
	public String index(HttpSession session, HttpServletRequest request) {
		return "index";
	}
	
	@RequestMapping(value="/home", method=RequestMethod.GET)
	public String home(HttpSession session, HttpServletRequest request) {
		return "index";
	}
	
	@RequestMapping(value="/signin", method=RequestMethod.GET)
	public String signin(){
		return "signin";
	}
	
	@RequestMapping(value="/features", method=RequestMethod.GET)
	public String features() {
		return "features";
	}
	
	@RequestMapping(value="/deposite", method=RequestMethod.GET)
	public String deposite() {
		return "deposite";
	}		
	
	@RequestMapping(value="/forgetpwd", method=RequestMethod.GET)
	public String forgetpwd() {
		return "forgetpwd";
	}		
	
	@RequestMapping(value="/accountcharge", method=RequestMethod.GET)
	public ModelAndView account() {
		ModelAndView view = new ModelAndView();
		view.setViewName("accountcharge");
		view.addObject(PAGE_NAME, "accountcharge");
		return view;
	}	
	
	@RequestMapping(value="/history", method=RequestMethod.GET)
	public ModelAndView history() {
		ModelAndView view = new ModelAndView();
		view.setViewName("history");
		view.addObject(PAGE_NAME, "history");
		return view;
	}	
	
	@RequestMapping(value="/setting", method=RequestMethod.GET)
	public ModelAndView setting() {
		ModelAndView view = new ModelAndView();
		view.setViewName("setting");
		view.addObject(PAGE_NAME, "setting");
		return view;
	}	

}
