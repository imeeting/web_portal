<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@page import="java.util.Collection" %>
<%@page import="com.imeeting.mvc.model.conference.*" %>
<%@page import="com.imeeting.mvc.model.conference.attendee.*" %>
<%@page import="com.imeeting.web.user.UserBean" %>
<%
	UserBean user = (UserBean) session.getAttribute(UserBean.SESSION_BEAN);
	ConferenceModel conference = (ConferenceModel)request.getAttribute("conference"); 
	Collection<AttendeeModel> attendeeCollection = conference.getAllAttendees();
	
    for(AttendeeModel attendee : attendeeCollection) {
    	String telephoneClass = "im-icon-phone-" + attendee.getPhoneCallStatus().name();
%>
<div id="div<%=attendee.getPhone()%>" class="im-attendee im-attendee-conf im-attendee-name pull-left">
	<div><i class="icon-user"></i>&nbsp;<%=attendee.getNickname()%></div>
	<div><i class="<%=telephoneClass%> im-icon im-phone-icon"></i>&nbsp;<%=attendee.getPhone()%></div>
</div>
<% } %>
