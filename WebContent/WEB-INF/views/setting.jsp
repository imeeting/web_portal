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
						  <div id="divOldPwd" class="control-group">
							<label class="control-label">输入当前使用的密码</label>
							<input type="password" name="oldPwd" id="iptOldPwd" />
							<span class="help-inline"></span>
						  </div>
						  <div id="divNewPwd" class="control-group">
							<label class="control-label">输入新密码</label>
							<input type="password" name="newPwd" id="iptNewPwd" />
							<span class="help-inline"></span>
						  </div>
						  <div id="divNewPwdConfirm" class="control-group">
							<label class="control-label">再次输入新密码</label>
							<input type="password" name="newPwdConfirm" id="iptNewPwdConfirm" />
							<span class="help-inline"></span>
						  </div>
						  <div id="divSubmit" class="control-group">
							<button type="submit" class="btn btn-primary">确&nbsp;定</button>
							<span class="help-inline"></span>
						  </div>
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
    <script src="/imeeting/js/lib/jquery-1.8.0.min.js"></script>
    <script src="/imeeting/js/lib/bootstrap.min.js"></script>
	<script type="text/javascript" src="/imeeting/js/lib/md5.js"></script>
	<script type="text/javascript">
		function isValidPassword($div, val){
			var $span = $div.find(".help-inline");
			if (val == ""){
				$div.addClass("error");
				$span.html("输入不能为空");
				return false;
			} else {
				$div.removeClass("error");
				$span.html("");
				return true;
			}
		}
		
		$("#formChangePwd").submit(function(){
			var $divOldPwd = $("#divOldPwd");
			var oldPwd = $("#iptOldPwd").val();
			var $divNewPwd = $("#divNewPwd");
			var newPwd = $("#iptNewPwd").val();
			var $divNewPwdConfirm = $("#divNewPwdConfirm");
			var newPwdConfirm = $("#iptNewPwdConfirm").val();
			if (isValidPassword($divOldPwd, oldPwd) &&
				isValidPassword($divNewPwd, newPwd) &&
				isValidPassword($divNewPwdConfirm, newPwdConfirm))
			{
				var $divSubmit = $("#divSubmit");
				var $spanSubmit = $divSubmit.find(".help-inline");
				var jqxhr = $.post("setting/changepassword", 
					{
						oldPwd: md5(oldPwd),
						newPwd: newPwd,
						newPwdConfirm: newPwdConfirm
					},
					function(data){
						if ("200" == data){
							$divSubmit.removeClass("error");
							$spanSubmit.html("");
							alert("修改密码成功");
						} else
						if ("400" == data){
							$divSubmit.addClass("error");
							$spanSubmit.html("原始密码输入错误");
						} else
						if ("403" == data){
							$divSubmit.addClass("error");
							$spanSubmit.html("新密码两次输入不一致");
						} else
						if ("500" == data){
							$divSubmit.addClass("error");
							$spanSubmit.html("服务器内部错误");
						} else {
							$divSubmit.addClass("error");
							$spanSubmit.html("未知错误：[" + data + "]");
						}
					}
				);
				jqxhr.fail(function(jqXHR, textStatus, errorThown){
					$divSubmit.addClass("error");
					if ("error" == textStatus){
						$spanSubmit.html("请求错误：" + jqXHR.status);
					} else {
						$spanSubmit.html("请求失败：" + textStatus);
					}
				});
				jqxhr.always(function(){
					$("#iptOldPwd").val("");
					$("#iptNewPwd").val("");
					$("#iptNewPwdConfirm").val("");
				});
			}
			return false;
		});
	</script>
  </body>
</html>
