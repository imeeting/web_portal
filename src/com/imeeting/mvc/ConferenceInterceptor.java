package com.imeeting.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.conference.ConferenceManager;

public class ConferenceInterceptor implements HandlerInterceptor {

	@Override
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object obj) throws Exception {
		String conferenceId = request.getParameter("conferenceId");
		if (null != conferenceId){
			ConferenceManager conferenceManager = ContextLoader.getConferenceManager();
			if (null == conferenceManager.getConference(conferenceId)){
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "CONFERENCE: "+conferenceId);
				return false;
			}
		}
		
		return true;
	}

}
