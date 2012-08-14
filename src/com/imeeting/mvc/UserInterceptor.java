package com.imeeting.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.imeeting.web.user.UserBean;

public class UserInterceptor implements HandlerInterceptor {
	private static Log log = LogFactory.getLog(UserInterceptor.class);
	@Override
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object arg2) throws Exception {
		HttpSession session = request.getSession();
		UserBean userBean = (UserBean)session.getAttribute(UserBean.SESSION_BEAN);
		if (null == userBean ){
			response.sendRedirect("");
			return false;
		}
		
		return true;
	}

}