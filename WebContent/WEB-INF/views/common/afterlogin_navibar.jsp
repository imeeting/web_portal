<%@page import="com.imeeting.constants.WebConstants"%>
<%@page import="com.imeeting.web.user.UserBean"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	UserBean userBean = (UserBean) session
			.getAttribute(UserBean.SESSION_BEAN);
	if (userBean == null) {
		response.sendRedirect("");
		return;
	}
	String pageName = String.valueOf(request
			.getAttribute(WebConstants.page_name.name()));
%>
<div class="navbar navbar-fixed-top">
	<div class="navbar-inner">
		<div class="container">
			<a class="btn btn-navbar" data-toggle="collapse"
				data-target=".nav-collapse"> <span class="icon-bar"></span> <span
				class="icon-bar"></span> <span class="icon-bar"></span>
			</a> <a class="brand" href="#">智会</a>
			<ul class="nav">
				<li><a id="username"><%=userBean.getName()%></a></li>
				<li><a id="logout" href="signout">退出登录</a></li>
			</ul>
			<div class="nav-collapse">
				<ul class="nav pull-right">
					<li class="<%="accountcharge".equals(pageName) ? "active" : ""%>"><a href="accountcharge">充值账户</a></li>
					<li class="<%="myconference".equals(pageName) ? "active" : ""%>"><a href="myconference">我的群聊</a></li>
					<li class="<%="setting".equals(pageName) ? "active" : ""%>"><a href="setting">系统设置</a></li>
				</ul>
			</div>
		</div>
	</div>
</div>