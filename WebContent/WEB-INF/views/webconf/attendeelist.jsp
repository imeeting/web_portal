<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@page import="java.util.Collection" %>
<%@page import="com.imeeting.mvc.model.conference.*" %>
<%@page import="com.imeeting.mvc.model.conference.attendee.*" %>
<%@page import="com.imeeting.web.user.UserBean" %>
<%
	UserBean user = (UserBean) session.getAttribute(UserBean.SESSION_BEAN);
	ConferenceModel conference = (ConferenceModel)request.getAttribute("conference"); 
	Collection<AttendeeModel> attendeeCollection = conference.getAvaliableAttendees();
	
    for(AttendeeModel attendee : attendeeCollection) {
    	String telephoneClass = "im-icon-phone-" + attendee.getPhoneCallStatus().name();
    	
        String phoneCallStatusText = "";
        if (AttendeeModel.PhoneCallStatus.Terminated.equals(attendee.getPhoneCallStatus()) ){
            phoneCallStatusText = "未接通";
        } else 
        if (AttendeeModel.PhoneCallStatus.Failed.equals(attendee.getPhoneCallStatus()) ){
            phoneCallStatusText = "呼叫失败";
        } else 
        if(AttendeeModel.PhoneCallStatus.CallWait.equals(attendee.getPhoneCallStatus()) ) {
            phoneCallStatusText = "正在呼叫";
        } else 
        if (AttendeeModel.PhoneCallStatus.Established.equals(attendee.getPhoneCallStatus()) ){
            phoneCallStatusText = "已接通";
        }
%>
<div id="div<%=attendee.getUsername()%>" class="im-attendee im-attendee-conf im-attendee-name pull-left">
	<p>
	    <span>&nbsp;<%=attendee.getDisplayName()%></span>
	</p>
    <p>
        <i class="<%=telephoneClass%> im-icon im-phone-icon"></i>
        <span>&nbsp;<%=attendee.getUsername()%></span><br>
        <i class="im-icon"></i>
        <span class="im-phone-text">&nbsp;<%=phoneCallStatusText%></span>
    </p>
</div>
<% } %>
