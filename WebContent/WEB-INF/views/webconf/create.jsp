<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	String confId = request.getParameter("confId");
	String attendeeName = request.getParameter("attendeeName");
%>
<!DOCTYPE html>
<html lang="zh">
<head>
<title>智会-创建群聊</title>
<jsp:include page="../common/_head.jsp"></jsp:include>
</head>

<body>
	<jsp:include page="../common/afterlogin_navibar.jsp"></jsp:include>

	<div class="container">
		<div class="row">
			<form id="formJoinConference" action="./webconf"
				class="span4 offset4" method="post">
				<label>给你的群聊起个名字吧</label> 
				<input id="iptConfTitle" name="confTitle"
					class="span4" type="text" placeholder="请叫我 红领巾" />
				<hr/>
				<label>选择要参加群聊的成员吧</label> 
				<div class="contact-selection-region">
					<table class="table">
						<thead>
							<tr>
								<th width="50%">通讯录</th>
								<th width="50%">已添加成员</th>
						</thead>
					</table>
					
					<div class="selection-list">
						<div class="input-prepend ab-search-field"><span class="add-on"><i class="icon-search"></i></span><input id="ab_search_input" size="16" type="text" placeholder="请输入拼音字母搜索"></div>
						<div class="blank"><button id="add_new_contact_bt" class="btn btn-inverse">添加新成员</button></div>
						<ul id="addressbook" class="well left unstyled">
							<li class="ab_contact">
								<strong class="name">star</strong>
								<ul class="unstyled">
									<li><span class="phone_number">13813005146</span><a class="add_contact_bt" href="#"><i class="icon-plus-sign"></i></a></li>
								</ul>
							</li>
							<li class="ab_contact">
								<strong class="name">star</strong>
								<ul class="unstyled">
									<li><span class="phone_number">13813005146</span><a class="add_contact_bt" href="#"><i class="icon-plus-sign"></i></a></li>
								</ul>
							</li>
						</ul>
						<ul id="selected_contacts" class="well right unstyled">
							<li class="selected_contact">
								<strong class="name">star</strong>
								<div><span class="phone_number">13813005146</span><a class="remove_contact_bt" href="#"><i class=" icon-remove-sign"></i></a></div>
							</li>
						</ul>
					</div>
				</div>

				<div class="create-button-region">
					<button id="create_conf_bt" type="submit" class="btn btn-success btn-large">开始群聊</button>
					<button id="cancel_create_conf_bt" class="btn btn-large">&nbsp;取&nbsp;&nbsp;消&nbsp;</button>
				</div>
			</form>
		</div>

		<jsp:include page="../common/_footer.jsp"></jsp:include>
	</div>
	<!-- /container -->
	
	<!-- template region  -->
	<div id="template" class="hidden">
		<ul>
			<li class="ab_contact">
				<strong class="name"></strong>
				<ul class="unstyled">
					<li><span class="phone_number"></span><a class="add_contact_bt" href="#"><i class="icon-plus-sign"></i></a></li>
				</ul>
			</li>
			<li class="selected_contact">
				<strong class="name"></strong>
				<div><span class="phone_number"></span><a class="remove_contact_bt" href="#"><i class=" icon-remove-sign"></i></a></div>
			</li>
		</ul>
	</div>
	
	<!-- Le javascript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="/imeeting/js/lib/jquery-1.8.0.min.js"></script>
	<script src="/imeeting/js/lib/bootstrap.js"></script>
</body>
</html>
