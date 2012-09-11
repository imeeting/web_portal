<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@page import="org.json.JSONArray" %>    
<%@page import="java.util.Collection" %>
<%@page import="com.imeeting.mvc.model.conference.*" %>
<%@page import="com.imeeting.mvc.model.conference.attendee.*" %>
<%@page import="com.imeeting.web.user.UserBean" %>
<% 
	UserBean user = (UserBean) session.getAttribute(UserBean.SESSION_BEAN);
	ConferenceModel conference = (ConferenceModel)request.getAttribute("conference"); 
	Collection<AttendeeModel> attendeeCollection = conference.getAvaliableAttendees();
	AttendeeModel myself = null;
	JSONArray videoOnAttendees = new JSONArray();
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
				<a class="btn btn-danger btn-large" href="webconf/unjoin?confId=<%=conference.getConferenceId() %>">离开群聊</a>
			</h1>  	
    	</div>
    	<div class="im-flash">
	    	<div id="flashContent">
	    	    <p>
	                To view this page ensure that Adobe Flash Player version 
	                11.0.0 or greater is installed. 
	            </p>
	            <script type="text/javascript"> 
	                var pageHost = ((document.location.protocol == "https:") ? "https://" : "http://"); 
	                document.write("<a href='http://www.adobe.com/go/getflashplayer'><img src='" 
	                                + pageHost + "www.adobe.com/images/shared/download_buttons/get_flash_player.gif' alt='Get Adobe Flash player' /></a>" ); 
	            </script> 
	    	</div>
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
					String videoStatusText = "";
					if (AttendeeModel.VideoStatus.on.equals(attendee.getVideoStatus())){
						videoStatusText = "视频已打开";
						videoOnAttendees.put(attendee.getUsername());
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
			<div id="div<%=attendee.getUsername() %>" class="im-attendee im-attendee-conf im-attendee-name pull-left">
				<p>
				    <i class="<%=onlineClass %> im-icon im-signin-icon"></i>
				    <span>&nbsp;<%=attendee.getUsername() %></span>
				</p>
				<p class="divAttendeeVideo">
				    <input class="<%=videoClass %> iptAttendeeId" type="hidden" value="<%=attendee.getUsername() %>"/>
				    <i class="<%=videoClass %> im-icon im-video-icon"></i>
				    <span class="im-video-text">&nbsp;<%=videoStatusText %></span>
				</p>
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
    <script type="text/javascript" src="/imeeting/flex/swfobject.js"></script>
    <script type="text/javascript">
         // For version detection, set to min. required Flash Player version, or 0 (or 0.0.0), for no version detection. 
         var swfVersionStr = "11.0.0";
         // To use express install, set to playerProductInstall.swf, otherwise the empty string. 
         var xiSwfUrlStr = "/imeeting/flex/playerProductInstall.swf";
         var flashvars = {};
         var params = {};
         params.quality = "high";
         params.bgcolor = "#ffffff";
         params.allowscriptaccess = "always";
         params.allowfullscreen = "true";
         var attributes = {};
         attributes.id = "imeeting_flash";
         attributes.name = "imeeting_flash";
         attributes.align = "middle";
         swfobject.embedSWF(
             "/imeeting/flex/imeeting_flash.swf", "flashContent", 
             "100%", "100%", 
             swfVersionStr, xiSwfUrlStr, 
             flashvars, params, attributes);
         // JavaScript enabled so display the flashContent div in case it is not replaced with a swf object.
         swfobject.createCSS("#flashContent", "display:block;text-align:left;");
    </script>
    <script type="text/javascript">
    function js_getRTMPUri(){
    	return "rtmp://127.0.0.1/quick_server/<%=conference.getConferenceId() %>";
    }
    function js_getUserId(){
    	return "<%=user.getName() %>";
    }
    function js_getVideoOnAttendees(){
    	return <%=videoOnAttendees.toString() %>;
    }
    </script>
  </body>
</html>
