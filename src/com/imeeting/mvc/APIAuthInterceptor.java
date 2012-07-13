package com.imeeting.mvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.imeeting.constants.AuthConstant;
import com.imeeting.framework.ContextLoader;
import com.richitec.util.MD5Util;



/**
 * API authentication interceptor
 * 
 * @author sk
 * 
 */
public class APIAuthInterceptor implements HandlerInterceptor {
	private static Log log = LogFactory.getLog(APIAuthInterceptor.class);
	@Override
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// 

	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
		// 

	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object obj) throws Exception {
		// validate the request
		String uri = request.getRequestURI().toString();
		log.info("uri: " + uri);

		String username = request.getParameter(AuthConstant.username.name());
		String sig = request.getParameter(AuthConstant.sig.name());
		
		log.info("username: " + username + " sig: " + sig);
		
		if (username == null || sig == null || username.equals("")
				|| sig.equals("")) {
			log.info("username or sig is null");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Request Parameters!");
			return false;
		}

		boolean flag = isValidSignature(request, username, sig);
		log.info("isValidSignature: " + flag);
		if (!flag) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
		}
		
		return flag;
	}

	/**
	 * 对所有参数按照key=value进行字典排序并拼接成一个完整的字符串，在字符串末尾附上userkey(ie. md5(username+password))并进行MD5，
	 * 比较该MD5与sig是否一致。
	 * 
	 * @param request
	 * @param username
	 * @param sig
	 * @return true: valid, false: invalid
	 */
	private boolean isValidSignature(HttpServletRequest request,
			String username, String sig) {
		String userkey = ContextLoader.getUserDAO().getUserKey(username);
		if (userkey == null) {
			log.info("userkey is null");
			return false;
		}
		
		log.info("username: " + username + " key: " + userkey);
		
		ArrayList<String> paramList = new ArrayList<String>();
		Enumeration<String> names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String key = names.nextElement();
			if (!key.equals(AuthConstant.sig.name())) {
				String value = request.getParameter(key);
				StringBuffer sb = new StringBuffer();
				sb.append(key).append("=").append(value);
				paramList.add(sb.toString());
			}
		}

		Collections.sort(paramList);
		log.info("params after sorting");
		printList(paramList);
		
		StringBuffer sb2 = new StringBuffer();
		for (int i = 0; i < paramList.size(); i++) {
			sb2.append(paramList.get(i));
		}

		sb2.append(userkey);

		boolean ret = false;
		String digest = MD5Util.md5(sb2.toString());
		log.info("digest: " + digest);
		if (digest.equalsIgnoreCase(sig)) {
			ret = true;
			log.info("valid signature");
		}

		return ret;
	}
	
	private static void printList(List<String> list) {
		for (int i = 0; i < list.size(); i++) {
			log.info(list.get(i));
		}
	}
}
