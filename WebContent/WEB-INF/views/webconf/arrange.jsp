<%@page import="com.imeeting.constants.AddressBookConstants"%>
<%@page import="com.imeeting.constants.WebConstants"%>
<%@page import="com.mongodb.DBObject"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	String confId = request.getParameter("confId");
	String attendeeName = request.getParameter("attendeeName");
	List<DBObject> abContacts = (List<DBObject>) request.getAttribute(WebConstants.addressbook.name());
%>
<!DOCTYPE html>
<html lang="zh">
<head>
<title>智会-创建会议</title>
<jsp:include page="../common/_head.jsp"></jsp:include>
</head>

<body>
	<jsp:include page="../common/afterlogin_navibar.jsp"></jsp:include>

	<div class="container">
		<div class="row-fluid im-container">
			<form id="formJoinConference" action="./webconf"
				class="span8 offset2" method="post">
				<div class="control-group">
					<label class="control-label">给你的会议起个名字吧</label>
					<div class="controls">
					<input id="iptConfTitle" name="confTitle" maxlength="32"
						class="span8" type="text" placeholder="默认使用会议号作为标题" />
					</div>
				</div>
				<div class="control-group clearfix">
					<div class="selection-list pull-left">
					    <label class="control-label">请选择参与者
					       <a data-toggle="modal" href="#upload_addressbook_help_dlg">（通过手机客户端上传通讯录）</a>
					    </label>
						<div class="input-prepend"><span class="add-on"><i class="icon-search"></i></span><input id="ab_search_input" size="16" type="text" placeholder="请输入名称或号码来搜索" /></div>
						<ul id="addressbook" class="well unstyled">
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
											<% for (String phone : phones) { %>
												<li><span class="phone_number"><%=phone %></span><a class="add_contact_bt" href="#"><i class="icon-plus"></i></a></li>
											<% } %>
											</ul>
										</li>
						<%
									}
								}
							}
						%>
							
						</ul>
					</div>
					<div class="selection-list pull-right">
					    <label class="control-label">已选中参与者</label>
					    <div class="blank"><button id="add_new_contact_bt" data-toggle="modal" data-target="#add_new_contact_dlg" class="btn btn-info">添加新成员</button></div>
						<ul id="selected_contacts" class="well unstyled">
						</ul>
					</div>
				</div>
				<div class="create-button-region control-group">
					<a id="cancel_create_conf_bt" href="/imeeting/myconference" class="btn btn-large">&nbsp;取&nbsp;&nbsp;消&nbsp;</a>
					<button id="create_conf_bt" type="submit" class="btn btn-success btn-large">开始会议</button>
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
			<h4>添加新成员</h4>
		</div>
		<div class="modal-body">
			<!--  
			<span>名&nbsp;&nbsp;称：</span>
			<input id="newContactName" type="text" class="span3" />
			<br/>
			-->
			<span>号&nbsp;&nbsp;码：</span>
			<input id="newContactPhoneNumber" type="text" class="span3" />
		</div>
		<div class="modal-footer">
			<a href="#" id="add_cancel_bt" class="btn" data-dismiss="modal" aria-hidden="true">取消</a> 
			<a href="#" id="add_confirm_bt" class="btn btn-primary">确定</a>
		</div>
	</div>
	
	<div id="upload_addressbook_help_dlg" class="modal hide fade">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal"
				aria-hidden="true">&times;</button>
			<h4>手机通讯录上传方法</h4>
		</div>
		<div class="modal-body">
			<h5>第一步：在客户端首页中点击设置按钮，进入设置界面</h5>
			<img alt="首页" src="/imeeting/img/app_main_page.png" title="在客户端首页中点击设置按钮，进入设置界面">
			<hr/>
			<h5>第二步：在设置界面选择“备份通讯录”</h5>
			<img alt="设置界面" src="/imeeting/img/app_setting_page.png" title="在设置界面选择“备份通讯录”">
			<hr/>
			<h5>第三步：在弹出的对话框中选择备份，即可上传手机通讯录</h5>
			<img alt="备份对话框" src="/imeeting/img/app_addressbook_upload_page.png" title="在弹出的对话框中选择备份，即可上传手机通讯录">
		</div>
		<div class="modal-footer">
			<a href="#" data-dismiss="modal" aria-hidden="true" class="btn btn-primary">关闭</a>
		</div>
	</div>

	<!-- Le javascript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="/imeeting/js/lib/jquery-1.8.0.min.js"></script>
	<script src="/imeeting/js/lib/bootstrap.js"></script>
	<script src="/imeeting/js/lib/json2.js"></script>
	<script src="/imeeting/js/applib/common.js"></script>
	<script src="/imeeting/js/webconf/arrange.js"></script>
</body>
</html>
