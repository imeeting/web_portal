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
	AttendeeModel myself = null;
%>
<!DOCTYPE html>
<html lang="zh">
  <head>
    <title>智会-群聊<%=conference.getConferenceId() %></title>
	<jsp:include page="../common/_head.jsp"></jsp:include>
  </head>

  <body>
    <jsp:include page="../common/afterlogin_navibar.jsp"></jsp:include>

    <div class="container">
    	<div class="clearfix">
			<h1><%=conference.getConferenceId() %>&nbsp;
				<small>请拨打号码0551-2379997加入群聊，或者点击<span class="label label-success">Call Me</span>按钮，系统会向您发起呼叫。</small>
			</h1>
			<h1>
				欢迎您：<%=user.getName() %>
				<a id="btnPhoneCall" class="btn btn-success btn-large">Call&nbsp;Me</a>
				<a class="btn btn-info btn-large">打开摄像头</a>
				<a class="btn btn-danger btn-large" href="webconf/unjoin?confId=<%=conference.getConferenceId() %>">离开群聊</a>
			</h1>  	
    	</div>
    	<div class="clearfix">
    		<div class="im-video"></div>
    		<div class="im-video"></div>
    		<div class="im-video"></div>
    		<div class="im-video"></div>
    	</div>
		<div id="divAttendeeList" class="clearfix">
			<% 
				for(AttendeeModel attendee : attendeeCollection) {
					if (user.getName().equals(attendee.getUsername())){
						myself = attendee;
						continue;
					}
					AttendeeModel.OnlineStatus onlineStatus = attendee.getOnlineStatus();
					String onlineClass = "im-icon-signin-" + onlineStatus.name();
					String telephoneClass = "im-icon-phone-" + attendee.getPhoneCallStatus().name();
					String videoClass = "im-icon-video-" + attendee.getVideoStatus().name();
					
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
			<div id="div<%=attendee.getUsername() %>" class="im-attendee im-attendee-conf im-attendee-name pull-left">
				<p><i class="<%=onlineClass %> im-icon im-signin-icon"></i>&nbsp;<%=attendee.getUsername() %></p>
				<% if (AttendeeModel.VideoStatus.on.equals(attendee.getVideoStatus())) { %>
				<p><button class="im-btn-video btn btn-info"><i class="icon-facetime-video btn-white"></i>&nbsp;观看视频</button></p>
				<% } else { %>
				<p><i class="im-icon-video-off im-icon"></i>&nbsp;没有视频</p>
				<% } %>
				<p>
				    <i class="<%=telephoneClass %> im-icon im-phone-icon"></i>
				    <span class="im-phone-text">&nbsp;<%=phoneCallStatusText %></span>
				</p>
				<% if (conference.getOwnerName().equals(user.getName())) { %>
				<div class="divAttendeePhone">
				    <input class="iptAttendeePhoneNumber" type="hidden" value="<%=attendee.getUsername() %>" />
				    <input class="iptAttendeePhoneCallStatus" type="hidden" value="<%=attendee.getPhoneCallStatus() %>" />
				    <button class="btnAttendeePhoneCall btn" class="btn"><%=btnValue %></button>	
				</div>
				<% } %>
			</div>
			<% } %>
		</div>
		<jsp:include page="../common/_footer.jsp"></jsp:include>
    </div> <!-- /container -->
    <div>
        <input id="iptMyPhoneCallStatus" type="hidden" value="<%=myself.getPhoneCallStatus() %>">
        <input id="iptConfId" type="hidden" value="<%=conference.getConferenceId() %>">
        <input id="iptUserId" type="hidden" value="<%=user.getName() %>">
    </div>

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="/imeeting/js/lib/jquery-1.8.0.min.js"></script>
    <script src="/imeeting/js/lib/bootstrap.min.js"></script>
    <script src="http://msg.walkwork.net/socket.io/socket.io.js"></script>
    <script src="/imeeting/js/conference.js"></script>
  </body>
</html>
