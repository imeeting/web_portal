<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh">
  <head>
    <title>智会-系统设置</title>
	<jsp:include page="common/_head.jsp"></jsp:include>
  </head>

  <body>
    <jsp:include page="common/afterlogin_navibar.jsp"></jsp:include>

    <div class="container">
		<div class="row">
			<div class="span6 offset3 tabbable tabs-left">
				<ul class="nav nav-tabs">
					<li class="active">
						<a href="#pane-change-password" data-toggle="tab">修改密码</a>
					</li>
					<li>
						<a href="#pane-user-info" data-toggle="tab">基本信息</a>
					</li>
					<li>
						<a href="#pane-user-avatar" data-toggle="tab">设置头像</a>
					</li>					
				</ul>
				<div class="tab-content">			
					<div class="tab-pane active" id="pane-change-password">
						<h3>修改密码</h3>
						<hr>
						<form action="setting/changepassword" method="post" id="formChangePwd" >
							<label>输入当前使用的密码</label>
							<input type="password" name="oldPwd" id="iptOldPwd" />
							<label>输入新密码</label>
							<input type="password" name="newPwd" id="iptNewPwd" />
							<label>再次输入新密码</label>
							<input type="password" name="newPwdConfirm" id="iptNewPwdConfirm" />
							<br>
							<button type="submit" class="btn btn-primary">确&nbsp;定</button>
						</form>
					</div>
					<div class="tab-pane" id="pane-user-info">
						<h3>基本信息</h3>
						<hr>
						<p>我们正在紧张开发中，请耐心等待。。。</p>
					</div>	
					<div class="tab-pane" id="pane-user-avatar">
						<h3>设置头像</h3>
						<hr>
						<p>我们正在紧张开发中，请耐心等待。。。</p>
					</div>	
				</div>
			</div>
		</div>    	
		<jsp:include page="common/_footer.jsp"></jsp:include>
    </div> <!-- /container -->

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.0/jquery.min.js"></script>
    <script src="js/lib/bootstrap.js"></script>
	<script type="text/javascript" src="js/lib/md5.js"></script>
	<script type="text/javascript">
		$(function(){
			$("#formChangePwd").submit(function(){
				var oldPassword = $("#iptOldPwd").val();
				var newPassword = $("#iptNewPwd").val();
				var newPasswordConfirm = $("#iptNewPwdConfirm").val();
				var jqxhr = $.post("setting/changepassword", 
					{
						oldPwd: md5(oldPassword),
						newPwd: newPassword,
						newPwdConfirm: newPasswordConfirm
					},
					function(data){
						if ("200" == data){
							alert("修改密码成功");
						} else
						if ("400" == data){
							alert("原始密码输入错误");
						} else
						if ("403" == data){
							alert("新密码两次输入不一致");
						} else
						if ("500" == data){
							alert("服务器内部错误");
						} else {
							alert("未知错误：[" + data + "]");
						}
					}
				);
				jqxhr.fail(function(jqXHR, textStatus, errorThown){
					if ("error" == textStatus){
						alert("操作失败[" + jqXHR.status + "]");
					} else {
						alert(textStatus);
					}
				});
				jqxhr.always(function(){
					$("#iptOldPwd").val("");
					$("#iptNewPwd").val("");
					$("#iptNewPwdConfirm").val("");
				});
				return false;
			});
		});
	</script>
  </body>
</html>
