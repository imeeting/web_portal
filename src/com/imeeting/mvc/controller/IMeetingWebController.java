package com.imeeting.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IMeetingWebController {
	private static Log log = LogFactory.getLog(IMeetingWebController.class);
	
	@RequestMapping("/")
	public String index(HttpSession session, HttpServletRequest request) {
		return "index";
	}

}
