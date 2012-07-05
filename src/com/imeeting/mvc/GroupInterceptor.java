package com.imeeting.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.group.GroupManager;

public class GroupInterceptor implements HandlerInterceptor {

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
		if (request.getRequestURI().endsWith("/join") ||
			request.getRequestURI().endsWith("/create")	){
			return true;
		}
		
		String groupId = request.getParameter("groupId");
		if (null != groupId){
			GroupManager groupManager = ContextLoader.getGroupManager();
			if (null == groupManager.getGroup(groupId)){
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "GROUP:"+groupId);
				return false;
			}
		}
		
		return true;
	}

}
