package com.imeeting.mvc.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.imeeting.framework.Configuration;
import com.imeeting.framework.ContextLoader;
import com.imeeting.web.user.UserBean;
import com.richitec.ucenter.model.UserDAO;
import com.richitec.vos.client.VOSClient;
import com.richitec.vos.client.VOSHttpResponse;

@Controller
@RequestMapping("/user")
public class UserController extends ExceptionController {

	private static Log log = LogFactory.getLog(UserController.class);

	private UserDAO userDao;
	private VOSClient vosClient;
	private Configuration config;

	@PostConstruct
	public void init() {
		userDao = ContextLoader.getUserDAO();
		vosClient = ContextLoader.getVOSClient();
		config = ContextLoader.getConfiguration();
	}

	@RequestMapping("/login")
	public void login(
			@RequestParam(value = "loginName") String loginName,
			@RequestParam(value = "loginPwd") String loginPwd,
			@RequestParam(value = "brand", required = false, defaultValue = "") String brand,
			@RequestParam(value = "model", required = false, defaultValue = "") String model,
			@RequestParam(value = "release", required = false, defaultValue = "") String release,
			@RequestParam(value = "sdk", required = false, defaultValue = "") String sdk,
			@RequestParam(value = "width", required = false, defaultValue = "0") String width,
			@RequestParam(value = "height", required = false, defaultValue = "0") String height,
			HttpServletResponse response, HttpSession session) throws Exception {
		JSONObject jsonUser = userDao.login(loginName, loginPwd);
		log.info("result: " + jsonUser.toString());
		String result = jsonUser.getString("result");
		if (result != null && result.equals("0")) {
			// login success, add UserBean to Session
			UserBean userBean = new UserBean();
			userBean.setName(loginName);
			userBean.setPassword(loginPwd);
			session.setAttribute(UserBean.SESSION_BEAN, userBean);
		}
		response.getWriter().print(jsonUser.toString());
	}

	@RequestMapping("/getPhoneCode")
	public void getPhoneCode(@RequestParam(value = "phone") String phone,
			HttpServletResponse response, HttpSession session) throws Exception {
		JSONObject jsonUser = new JSONObject();
		try {
			String result = "0";
			result = userDao.checkRegisterPhone(phone);
			log.info("check register phone return: " + result);
			if (result.equals("0")) {
				result = userDao.getPhoneCode(session, phone);
			}
			jsonUser.put("result", result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		response.getWriter().print(jsonUser.toString());
	}

	@RequestMapping("/checkPhoneCode")
	public void checkPhoneCode(@RequestParam(value = "code") String code,
			HttpServletResponse response, HttpSession session) throws Exception {
		JSONObject jsonUser = new JSONObject();
		try {
			String result = "0";
			if (session.getAttribute("phonecode") != null) {
				result = userDao.checkPhoneCode(session, code);
			} else {
				result = "6"; // session timeout
			}
			jsonUser.put("result", result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		response.getWriter().print(jsonUser.toString());
	}

	@RequestMapping("/regUser")
	public void regUser(@RequestParam(value = "password") String password,
			@RequestParam(value = "password1") String password1,
			HttpServletResponse response, HttpSession session) throws Exception {
		log.info("regUser");
		
		String result = "";
		String phone = "";
		if (null == session.getAttribute("phonenumber")){
			result = "6"; // session过期
		} else {
			phone = (String) session.getAttribute("phonenumber");
			result = userDao.regUser(phone, password, password1);
		}
		
		if ("0".equals(result)){ //insert success
			Integer vosphone = userDao.getVOSPhoneNumber(phone);
			result = addUserToVOS(phone, vosphone.toString());
		}
		
		JSONObject jsonUser = new JSONObject();
		try {
			jsonUser.put("result", result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		response.getWriter().print(jsonUser.toString());
	}
	
	private String addUserToVOS(String username, String vosPhoneNumber){
		//create new account in VOS
		VOSHttpResponse addAccountResp = vosClient.addAccount(username);
		if (addAccountResp.getHttpStatusCode() != 200 || 
			!addAccountResp.isOperationSuccess()){
			log.error("\nCannot create VOS accont for user : " + username +
					  "\nVOS Http Response : " + addAccountResp.getHttpStatusCode() + 
					  "\nVOS Status Code : " + addAccountResp.getVOSStatusCode() + 
					  "\nVOS Response Info ：" + addAccountResp.getVOSResponseInfo());
			return "2001";
		}
		
		//create new phone in VOS
		VOSHttpResponse addPhoneResp = vosClient.addPhoneToAccount(username, vosPhoneNumber);
		if (addPhoneResp.getHttpStatusCode() != 200 || 
			!addPhoneResp.isOperationSuccess()){
			log.error("\nCannot create VOS phone <"+vosPhoneNumber+"> for user : " + username + 
					  "\nVOS Http Response : " + addPhoneResp.getHttpStatusCode() + 
					  "\nVOS Status Code : " + addPhoneResp.getVOSStatusCode() + 
					  "\nVOS Response Info ：" + addPhoneResp.getVOSResponseInfo());
			return "2002";
		}		
		
		//add suite to account
		VOSHttpResponse addSuiteResp = vosClient.addSuiteToAccount(username, config.getSuite0Id());
		if (addSuiteResp.getHttpStatusCode() != 200 || 
			!addSuiteResp.isOperationSuccess()){
			log.error("\nCannot add VOS suite <"+config.getSuite0Id()+"> for user : " + username + 
					  "\nVOS Http Response : " + addSuiteResp.getHttpStatusCode() + 
					  "\nVOS Status Code : " + addSuiteResp.getVOSStatusCode() + 
					  "\nVOS Response Info ：" + addSuiteResp.getVOSResponseInfo());
			return "2003";
		}			
		
		return "0";
	}

	@RequestMapping("/regToken")
	public void regToken(
			@RequestParam(value = "username", required = true) String userName,
			@RequestParam String token, HttpServletResponse response)
			throws JSONException, IOException {
		JSONObject resultJson = new JSONObject();
		String result = userDao.saveToken(userName, token);
		resultJson.put("result", result);
		response.getWriter().print(resultJson.toString());
	}
}
