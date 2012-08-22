<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@page import="java.util.Collection" %>
<%@page import="com.imeeting.mvc.model.conference.*" %>
<%@page import="com.imeeting.mvc.model.conference.attendee.*" %>
<% 
	ConferenceModel conference = (ConferenceModel)request.getAttribute("conference"); 
	Collection<AttendeeModel> attendeeCollection = conference.getAllAttendees();
	AttendeeModel [] attendeeList = (AttendeeModel [])attendeeCollection.toArray();
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
    	<div>
			<h1><%=conference.getConferenceId() %></h1>    	
    	</div>
		<div class="clearfix">
			<% for (int i=0; i<attendeeList.length; i++) {
					AttendeeModel attendee = attendeeList[i];
			%>
			<div class="im-conf-attendee im-attendee-name pull-left">
				<i class="icon-user"></i>&nbsp;<%=attendee.getUsername() %>
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
