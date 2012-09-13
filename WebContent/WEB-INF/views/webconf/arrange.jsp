<%@page import="com.imeeting.constants.AddressBookConstants"%>
<%@page import="com.imeeting.constants.WebConstants"%>
<%@page import="com.mongodb.DBObject"%>
<%@page import="java.util.List"%>
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
					
					<%
						List<DBObject> abContacts = (List<DBObject>) request.getAttribute(WebConstants.addressbook.name());
					%>
					
					<div class="selection-list">
						<div class="input-prepend ab-search-field"><span class="add-on"><i class="icon-search"></i></span><input id="ab_search_input" size="16" type="text" placeholder="请输入名称或号码来搜索" /></div>
						<div class="blank"><button id="add_new_contact_bt" data-toggle="modal" data-target="#add_new_contact_dlg" class="btn btn-inverse">添加新成员</button></div>
						<ul id="addressbook" class="well left unstyled">
						<% 
							if (abContacts != null) {
								for (DBObject contact : abContacts) {
									String displayName = (String) contact.get(AddressBookConstants.display_name.name());
									List<String> phones = (List<String>) contact.get(AddressBookConstants.phone_array.name());
									
									if (phones != null && phones.size() > 0) {
						%>
										<li class="ab_contact">
											<strong class="name"><%=displayName %></strong>
											<ul class="unstyled">
											<%
												for (String phone : phones) {
											%>
													<li><span class="phone_number"><%=phone %></span><a class="add_contact_bt" href="#"><i class="icon-plus"></i></a></li>
											<%
												}
											%>
											</ul>
										</li>
						<%
									}
								}
							}
						%>
							
						</ul>
						<ul id="selected_contacts" class="well right unstyled">
						</ul>
					</div>
				</div>

				<div class="create-button-region">
					<a id="cancel_create_conf_bt" href="/imeeting/myconference" class="btn btn-large">&nbsp;取&nbsp;&nbsp;消&nbsp;</a>
					<button id="create_conf_bt" type="submit" class="btn btn-success btn-large">开始群聊</button>
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
				<ul class="number_ul unstyled">
					<li class="number_li"><span class="phone_number"></span><a class="add_contact_bt" href="#"><i class="icon-plus"></i></a></li>
				</ul>
			</li>
			<li class="selected_contact">
				<strong class="name"></strong>
				<div><span class="phone_number"></span><a class="remove_contact_bt" href="#"><i class="icon-remove"></i></a></div>
			</li>
		</ul>
	</div>

	<!-- Dialog -->
	<div id="add_new_contact_dlg" class="modal hide fade">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal"
				aria-hidden="true">&times;</button>
			<h3>添加新成员</h3>
		</div>
		<div class="modal-body">
			<span>名&nbsp;&nbsp;称：</span>
			<input id="newContactName" type="text" class="span3" />
			<br/>
			<span>号&nbsp;&nbsp;码：</span>
			<input id="newContactPhoneNumber" type="text" class="span3" />
		</div>
		<div class="modal-footer">
			<a href="#" id="add_cancel_bt" class="btn" data-dismiss="modal" aria-hidden="true">取消</a> 
			<a href="#" id="add_confirm_bt" class="btn btn-primary">确定</a>
		</div>
	</div>

	<!-- Le javascript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="/imeeting/js/lib/jquery-1.8.0.min.js"></script>
	<script src="/imeeting/js/lib/bootstrap.js"></script>
	<script src="/imeeting/js/applib/common.js"></script>
	<script src="/imeeting/js/webconf/arrange.js"></script>
</body>
</html>
