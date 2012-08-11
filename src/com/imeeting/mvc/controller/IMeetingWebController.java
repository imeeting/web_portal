package com.imeeting.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IMeetingWebController {
	private static Log log = LogFactory.getLog(IMeetingWebController.class);
	
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
	
	@RequestMapping(value="/account", method=RequestMethod.GET)
	public String account() {
		return "account";
	}	
	
	@RequestMapping(value="/history", method=RequestMethod.GET)
	public String history() {
		return "history";
	}	
	
	@RequestMapping(value="/setting", method=RequestMethod.GET)
	public String setting() {
		return "setting";
	}	

}
