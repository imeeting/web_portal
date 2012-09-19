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
	AttendeeModel myself = null;
	
    for(AttendeeModel attendee : attendeeCollection) {
        if (user.getUserName().equals(attendee.getUsername())){
            myself = attendee;
            continue;
        }
        AttendeeModel.OnlineStatus onlineStatus = attendee.getOnlineStatus();
        String onlineClass = "im-icon-signin-" + onlineStatus.name();
        String telephoneClass = "im-icon-phone-" + attendee.getPhoneCallStatus().name();
        String videoClass = "im-icon-video-" + attendee.getVideoStatus().name();
        String videoStatusText = "";
        if (AttendeeModel.VideoStatus.on.equals(attendee.getVideoStatus())){
            videoStatusText = "视频已打开";
        } else {
            videoStatusText = "视频不可用";
        }        
        
        String btnValue = "";
        String phoneCallStatusText = "";
        if (AttendeeModel.PhoneCallStatus.Terminated.equals(attendee.getPhoneCallStatus()) ){
            btnValue = "呼叫";
            phoneCallStatusText = "未接通";
        } else 
        if (AttendeeModel.PhoneCallStatus.Failed.equals(attendee.getPhoneCallStatus()) ){
            btnValue = "重新呼叫";
            phoneCallStatusText = "呼叫失败";
        } else 
        if(AttendeeModel.PhoneCallStatus.CallWait.equals(attendee.getPhoneCallStatus()) ) {
            btnValue = "取消呼叫";
            phoneCallStatusText = "正在呼叫";
        } else 
        if (AttendeeModel.PhoneCallStatus.Established.equals(attendee.getPhoneCallStatus()) ){
            btnValue = "挂断";
            phoneCallStatusText = "已接通";
        }
%>
<div id="div<%=attendee.getUsername()%>" class="im-attendee im-attendee-conf im-attendee-name pull-left">
    <p><i class="<%=onlineClass%> im-icon im-signin-icon"></i>&nbsp;<%=attendee.getDisplayName()%></p>
    <p class="divAttendeeVideo">
        <input class="<%=videoClass%> iptAttendeeId" type="hidden" value="<%=attendee.getUsername()%>"/>
        <i class="<%=videoClass%> im-icon im-video-icon"></i>
        <span class="im-video-text">&nbsp;<%=videoStatusText%></span>
    </p>
    <p>
        <i class="<%=telephoneClass%> im-icon im-phone-icon"></i>
        <span>&nbsp;<%=attendee.getUsername()%></span><br>
        <i class="im-icon"></i>
        <span class="im-phone-text">&nbsp;<%=phoneCallStatusText%></span>
    </p>
    <%
    	if (conference.getOwnerName().equals(user.getUserName())) {
    %>
    <div class="divAttendeePhone">
        <input class="iptAttendeePhoneNumber" type="hidden" value="<%=attendee.getUsername() %>" />
        <input class="iptAttendeePhoneCallStatus" type="hidden" value="<%=attendee.getPhoneCallStatus() %>" />
        <button class="btnAttendeePhoneCall btn" class="btn"><%=btnValue %></button>    
    </div>
    <% } %>
</div>
<% } %>
