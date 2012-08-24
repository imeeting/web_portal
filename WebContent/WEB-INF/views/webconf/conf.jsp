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
				<a class="btn btn-success btn-large">Call&nbsp;Me</a>
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
		<div class="clearfix">
			<% 
				for(AttendeeModel attendee : attendeeCollection) {
					AttendeeModel.OnlineStatus onlineStatus = attendee.getOnlineStatus();
					String onlineClass = "im-attendee-" + onlineStatus.name();
			%>
			<div class="<%=onlineClass %> im-attendee im-attendee-name pull-left">
				<p><i class="icon-user"></i>&nbsp;<%=attendee.getUsername() %></p>
				<% if (AttendeeModel.VideoStatus.on.equals(attendee.getVideoStatus())) { %>
				<p><button class="btn btn-info"><i class="icon-facetime-video btn-white"></i>&nbsp;观看视频</button></p>
				<% } else { %>
				<p><button class="btn"><i class="icon-facetime-video"></i>&nbsp;没有视频</button></p>
				<% } %>
			</div>
			<% } %>
		</div>
		<jsp:include page="../common/_footer.jsp"></jsp:include>
    </div> <!-- /container -->

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="/imeeting/js/lib/jquery-1.8.0.min.js"></script>
    <script src="/imeeting/js/lib/bootstrap.js"></script>
  </body>
</html>
