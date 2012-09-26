package com.imeeting.mvc.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.imeeting.constants.WebConstants;
import com.imeeting.framework.ContextLoader;
import com.imeeting.web.user.UserBean;
import com.richitec.sms.client.SMSHttpResponse;


@Controller
public class IMeetingWebController {
	private static Log log = LogFactory.getLog(IMeetingWebController.class);
	@RequestMapping("/")
	public ModelAndView index(HttpSession session, HttpServletRequest request) {
		ModelAndView view = new ModelAndView();
		view.setViewName("index");
		view.addObject(WebConstants.page_name.name(), "home");
		return view;
	}

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public ModelAndView home(HttpSession session, HttpServletRequest request) {
		ModelAndView view = new ModelAndView();
		view.setViewName("index");
		view.addObject(WebConstants.page_name.name(), "home");
		return view;
	}

	@RequestMapping(value = "/mobile", method = RequestMethod.GET)
	public String mobile() {
		return "index_mobile";
	}
	
	@RequestMapping(value = "/help", method = RequestMethod.GET)
	public String help() {
		return "help";
	}
	
	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public ModelAndView signup() {
		ModelAndView view = new ModelAndView();
		view.setViewName("signup");
		view.addObject(WebConstants.page_name.name(), "signup");
		return view;
	}

	@RequestMapping(value = "/signin", method = RequestMethod.GET)
	public ModelAndView signin() {
		ModelAndView view = new ModelAndView();
		view.setViewName("signin");
		view.addObject(WebConstants.page_name.name(), "signin");
		return view;
	}

	@RequestMapping(value = "/features", method = RequestMethod.GET)
	public ModelAndView features() {
		ModelAndView view = new ModelAndView();
		view.setViewName("features");
		view.addObject(WebConstants.page_name.name(), "features");
		return view;
	}

	@RequestMapping(value = "/forgetpwd", method = RequestMethod.GET)
	public ModelAndView forgetpwd() {
		ModelAndView view = new ModelAndView();
		view.setViewName("forgetpwd");
		view.addObject(WebConstants.page_name.name(), "forgetpwd");
		return view;
	}
	
	@RequestMapping(value = "/signout", method = RequestMethod.GET)
	public String signout(HttpSession session) {
		session.removeAttribute(UserBean.SESSION_BEAN);
		return "redirect:/";
	}
	
	@RequestMapping(value="/404")
	public String page404(){
		return "error/404";
	}
	
	@RequestMapping(value="/500")
	public String page500(){
		return "error/500";
	}	
	
	@RequestMapping("/getDownloadPageUrl")
	public void getDownloadPageUrl(HttpServletResponse response, @RequestParam String phoneNumber) throws JSONException, IOException {
		String url = ContextLoader.getConfiguration().getAppDonwloadPageUrl();
		String msgContent = "智会客户端下载地址：" + url;
		SMSHttpResponse resp = ContextLoader.getSMSClient().sendTextMessage(phoneNumber, msgContent);
		JSONObject ret = new JSONObject();		
		log.info("status code: " + resp.getStatusCode() + " code: " + resp.getCode());
		if (resp.getCode() == 3) {
			ret.put("result", "ok");
		} else {
			ret.put("result", "fail");
		}
		response.getWriter().print(ret.toString());
	}
	
	@RequestMapping("/appdownload")
	public String appDownloadPage() {
		return "app_download";
	}
	
	@ResponseStatus(HttpStatus.MOVED_TEMPORARILY)
	@RequestMapping("/downloadAppClient/{device}")
	public void downloadAppClient(HttpServletResponse response, @PathVariable String device) {
		String downloadUrl = ContextLoader.getConfiguration().getAppDownloadUrl();
		downloadUrl = downloadUrl + "/1/" + device; 
		response.addHeader("Location", downloadUrl);
	}
	
	@ResponseStatus(HttpStatus.MOVED_TEMPORARILY)
	@RequestMapping("/appVersion/{device}")
	public void appVersion(HttpServletResponse response, @PathVariable String device) {
		String versionUrl = ContextLoader.getConfiguration().getAppVersionUrl();
		versionUrl = versionUrl + "/1/" + device;
		response.addHeader("Location", versionUrl);
	}
}
