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
	Pager pager = (Pager)request.getAttribute(WebConstants.pager.name());
	SimpleDateFormat yearf = new SimpleDateFormat("yyyy");
	SimpleDateFormat monthf = new SimpleDateFormat("MM");
	SimpleDateFormat dayf = new SimpleDateFormat("dd");
	SimpleDateFormat hmf = new SimpleDateFormat("HH:mm");
	SimpleDateFormat apmf = new SimpleDateFormat("a");
	SimpleDateFormat ef = new SimpleDateFormat("E");
%>    
			<% for (int i=0; i<confList.size(); i++) { 
					ConferenceBean conf = confList.get(i);
					Date date = new Date(conf.getCreatedTimeStamp());
					String year = yearf.format(date);
					String month = monthf.format(date);
					String day = dayf.format(date);
					String hour = hmf.format(date);
					String ap = apmf.format(date);
					String week = ef.format(date);
					
					List<AttendeeBean> attendeeList = conf.getAttendeeList();
			%>
			
			<div class="well clearfix">
				<div class="pull-left">
					<h3><%=conf.getTitle()%></h3>
					<div class="clearfix">
						<% for (int j=0; j<attendeeList.size(); j++) {
								AttendeeBean attendee = attendeeList.get(j);
						%>
						<div class="pull-left">
							<img alt="avatar" src="img/avatar.jpg">
							<p><%=attendee.getUserName() %></p>
						</div>
						<% } %>
					</div>
				</div>
				<div class="pull-right">
					<h3><small>群聊号：<strong><%=conf.getId() %></strong></small></h3>
					<h3><small>
						<strong><%=year %></strong>年<strong><%=month %></strong>月<strong><%=day %></strong>日<br>
						<%=week %><br>
						<strong><%=ap %>&nbsp;<%=hour %></strong>
					</small></h3>
				</div>
			</div>
			<% } %>
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
			<% } %>
