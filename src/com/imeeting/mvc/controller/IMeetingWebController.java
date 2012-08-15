package com.imeeting.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.imeeting.web.user.UserBean;


@Controller
public class IMeetingWebController {
	
	@RequestMapping("/")
	public String index(HttpSession session, HttpServletRequest request) {
		return "index";
	}

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String home(HttpSession session, HttpServletRequest request) {
		return "index";
	}

	@RequestMapping(value = "/signin", method = RequestMethod.GET)
	public String signin() {
		return "signin";
	}

	@RequestMapping(value = "/features", method = RequestMethod.GET)
	public String features() {
		return "features";
	}

	@RequestMapping(value = "/forgetpwd", method = RequestMethod.GET)
	public String forgetpwd() {
		return "forgetpwd";
	}
	
	@RequestMapping(value = "/signout", method = RequestMethod.GET)
	public String signout(HttpSession session) {
		session.removeAttribute(UserBean.SESSION_BEAN);
		return "redirect:/";
	}
	
}
