<%@page import="com.imeeting.constants.WebConstants"%>
<%@page import="com.imeeting.mvc.model.contact.ContactBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	List<ContactBean> abContacts = (List<ContactBean>) request.getAttribute(WebConstants.addressbook.name());
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
				<div class="control-group clearfix">
					<div class="selection-list pull-left">
					    <label class="control-label">常用联系人</label>
						<div class="input-prepend"><span class="add-on"><i class="icon-search"></i></span><input id="ab_search_input" size="16" type="text" placeholder="姓名/手机/Email" /></div>
						<ul id="addressbook" class="well unstyled">
						<% 
							if (abContacts != null) {
								for (ContactBean contact : abContacts) {
						%>
									<li class="ab_contact">
										<div>
											<i class="icon-user"></i>
											<span class="name"><%=contact.getNickName() %></span>
											<a class="add_contact_bt" href="#"><i class="icon-plus"></i></a>
										</div>
										<div>
											<i class="icon-envelope"></i>
											<span class="email"><%=contact.getEmail() %></span>
										</div>
										<div>
											<i class="icon-comment"></i>
											<span class="phone"><%=contact.getPhone() %></span>
										</div>
									</li>
						<%
								}
							}
						%>
							
						</ul>
					</div>
					<div class="selection-list pull-right">
					    <label class="control-label">已选中参与者</label>
					    <div class="blank">
					    	<button id="add_new_contact_bt" data-toggle="modal" data-target="#add_new_contact_dlg" class="btn btn-info">手动输入参会者</button>
					    </div>
						<ul id="selected_contacts" class="well unstyled">
						</ul>
					</div>
				</div>
				<div id="divSelectTime" class="clearfix control-group">
					<input id="rdoNow" name="isScheduled" type="radio" value="now" class="pull-left"/>
					<label for="rdoNow" class="pull-left">&nbsp;马上开始</label>
					<input id="rdoSchedule" name="isScheduled" type="radio" value="schedule" checked="checked" class="pull-left"/>
					<label for="rdoSchedule" class="pull-left">&nbsp;预约时间</label>
					<div id="divScheduleTime" class="input-append pull-left">
						<input id="iptScheduleTime" type="text"/>					
						<span class="add-on"><i class="icon-calendar"></i></span>
					</div>
				</div>
				<div class="create-button-region control-group">
					<a id="cancel_create_conf_bt" href="/imeeting/myconference" class="btn btn-large">&nbsp;取&nbsp;&nbsp;消&nbsp;</a>
					<button id="create_conf_bt" type="submit" class="btn btn-success btn-large">&nbsp;确&nbsp;&nbsp;定&nbsp;</button>
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
				<div>
					<i class="icon-user"></i>
					<span class="name"></span>
					<a class="add_contact_bt" href="#"><i class="icon-plus"></i></a>
				</div>
				<div>
					<i class="icon-envelope"></i>
					<span class="email"></span>
				</div>
				<div>
					<i class="icon-comment"></i>
					<span class="phone"></span>
				</div>
			</li>
			<li class="selected_contact">
				<div>
					<i class="icon-user"></i><span class="name"></span>
					<a class="remove_contact_bt" href="#"><i class="icon-remove"></i></a>
				</div>
				<div><i class="icon-envelope"></i><span class="email"></span></div>
				<div><i class="icon-comment"></i><span class="phone"></span></div>
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
			<div class="pull-left">
				<div class="clearfix">
					<label for="newContactName" class="pull-left">姓名</label>
					<input id="newContactName" type="text" class="span3 pull-left" />
				</div>
				<div>
					<label for="newContactEmail" class="pull-left">电邮</label>
					<input id="newContactEmail" type="text" class="span3 pull-left" />
				</div>
				<div>
					<label for="newContactPhoneNumber" class="pull-left">手机</label>
					<input id="newContactPhoneNumber" type="text" class="span3 pull-left" />
				</div>
			</div>
			<div class="span2 pull-right">
				提示：系统会自动保存该联系人信息，以后可以直接从常用联系人中查找。
			</div>
		</div>
		<div class="modal-footer">
			<a href="#" id="add_cancel_bt" class="btn" data-dismiss="modal" aria-hidden="true">取消</a> 
			<a href="#" id="add_confirm_bt" class="btn btn-primary">确定</a>
		</div>
	</div>
	
	<div id="schedule_success_dlg" class="modal hide fade">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal"
				aria-hidden="true">&times;</button>
			<h4>安排会议成功！</h4>
		</div>
		<div class="modal-body">
			<p><span>接入号码：</span><strong id="">0551-62379997</strong></p>
			<p><span>会议密码：</span><strong id="schedule_conf_id"></strong></p>
			<p><span>会议时间：</span><strong id="schedule_conf_time"></strong></p>
		</div>
		<div class="modal-footer">
			<a href="#" class="btn btn-success" data-dismiss="modal" aria-hidden="true">我知道了</a> 
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
	<script src="/imeeting/js/my97/WdatePicker.js"></script>
	<script src="/imeeting/js/applib/common.js"></script>
	<script src="/imeeting/js/arrange.js"></script>
</body>
</html>
