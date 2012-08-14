package com.imeeting.mvc.controller;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.imeeting.framework.ContextLoader;
import com.imeeting.web.user.UserBean;
import com.richitec.vos.client.AccountInfo;
import com.richitec.vos.client.CurrentSuiteInfo;
import com.richitec.vos.client.VOSClient;

@Controller
public class IMeetingWebController {
	private static Log log = LogFactory.getLog(IMeetingWebController.class);
	
	public static final String PAGE_NAME = "page_name";

	private VOSClient vosClient;

	@PostConstruct
	public void init() {
		vosClient = ContextLoader.getVOSClient();
	}

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

	@RequestMapping(value = "/deposite", method = RequestMethod.GET)
	public ModelAndView deposite() {
		ModelAndView view = new ModelAndView();
		view.setViewName("deposite");
		return view;
	}

	@RequestMapping(value = "/forgetpwd", method = RequestMethod.GET)
	public String forgetpwd() {
		return "forgetpwd";
	}

	@RequestMapping(value = "/accountcharge", method = RequestMethod.GET)
	public ModelAndView account(HttpSession session) {
		ModelAndView view = new ModelAndView();
		view.setViewName("accountcharge");
		view.addObject(PAGE_NAME, "accountcharge");
		// get account
		UserBean userBean = (UserBean) session
				.getAttribute(UserBean.SESSION_BEAN);
		
		// get account balance
		AccountInfo accountInfo = vosClient.getAccountInfo(userBean.getName());
		CurrentSuiteInfo suiteInfo = vosClient.getCurrentSuite(userBean.getName());
		if (accountInfo != null && suiteInfo != null) {
			Double balance = accountInfo.getBalance() + suiteInfo.getGiftBalance();
			view.addObject("balance", balance);
		} else {
			view.addObject("balance", new Double(-1));
		}
		return view;
	}

	@RequestMapping(value = "/history", method = RequestMethod.GET)
	public ModelAndView history() {
		ModelAndView view = new ModelAndView();
		view.setViewName("history");
		view.addObject(PAGE_NAME, "history");
		return view;
	}

	@RequestMapping(value = "/setting", method = RequestMethod.GET)
	public ModelAndView setting() {
		ModelAndView view = new ModelAndView();
		view.setViewName("setting");
		view.addObject(PAGE_NAME, "setting");
		return view;
	}

}
