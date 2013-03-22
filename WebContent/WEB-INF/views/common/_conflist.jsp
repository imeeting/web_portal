<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="com.imeeting.constants.WebConstants"%>
<%@page import="com.imeeting.mvc.model.conference.ConferenceBean"%>
<%@page import="com.imeeting.mvc.model.conference.attendee.AttendeeBean"%>
<%@page import="com.richitec.util.Pager"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
	List<ConferenceBean> confList = 
		(List<ConferenceBean>)request.getAttribute(WebConstants.conf_list.name());
	if (confList.size() >0 ){
		Pager pager = (Pager)request.getAttribute(WebConstants.pager.name());
		SimpleDateFormat yearf = new SimpleDateFormat("yyyy");
		SimpleDateFormat monthf = new SimpleDateFormat("MM");
		SimpleDateFormat dayf = new SimpleDateFormat("dd");
		SimpleDateFormat hmf = new SimpleDateFormat("HH:mm");
		SimpleDateFormat apmf = new SimpleDateFormat("a");
		SimpleDateFormat ef = new SimpleDateFormat("E");
		for (int i=0; i<confList.size(); i++) { 
			ConferenceBean conf = confList.get(i);
			Date date = new Date(conf.getScheduledTimeStamp());
			String year = yearf.format(date);
			String month = monthf.format(date);
			String day = dayf.format(date);
			String hour = hmf.format(date);
			String ap = apmf.format(date);
			String week = ef.format(date);
			
			List<AttendeeBean> attendeeList = conf.getAttendeeList();
%>
			
			<div class="well clearfix">
				<div class="im-conf-title clearfix">
				<span class="pull-left"><%=year %>年<%=month %>月<%=day %>日&nbsp;<%=week %>&nbsp;<%=ap %>&nbsp;<%=hour %>	</span>
				<span class="pull-right"> 会议密码：<strong><%=conf.getId() %></strong></span>
				</div>
				<div class="clearfix">
					<% for (int j=0; j<attendeeList.size(); j++) {
							AttendeeBean attendee = attendeeList.get(j);
					%>
					<div class="im-attendee im-attendee-history im-attendee-name pull-left">
						<div><i class="icon-user"></i><%=attendee.getNickName() %></div>
						<div><i class="icon-envelope"></i><%=attendee.getEmail() %></div>
						<div><i class="icon-comment"></i><%=attendee.getPhone() %></div>
					</div>
					<% } %>
				</div>
			</div>
<% } //End of FOR%>
			
<% if (pager.getHasPrevious() || pager.getHasNext()) { %>
			<ul class="pager">
				<% if (pager.getHasPrevious()) {%>
				<li class="previous">
					<a href="<%=pager.getPreviousPage() %>">上一页</a>
				</li>
				<% } %>
				<li>
					<span><%=pager.getOffset() + "/" + pager.getPageNumber() %></span>
				</li>
				<% if (pager.getHasNext()) {%>
				<li class="next">
					<a href="<%=pager.getNextPage() %>">下一页</a>
				</li>
				<% } %>
			</ul>
			<script type="text/javascript">
		    	$(".pager li a").click(function(){
		    		var $this = $(this);
		    		var href = $this.attr("href");
		    		if (href.length > 0){
		    			$("#divConfListContainer").load(href);
		    		}
		    		return false;
		    	});
			</script>
<% 		} //End of IF %>
<% } else { // END if (confList.size() >0 ) %>
	<small>你还没有安排任何会议</small>	
<% } %>
