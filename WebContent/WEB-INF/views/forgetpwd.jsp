<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh">
  <head>
    <title>智会-忘记密码</title>
	<jsp:include page="common/_head.jsp"></jsp:include>
  </head>

  <body>
  	<jsp:include page="common/beforelogin_navibar.jsp"></jsp:include>

    <div class="container">
    	<div class="row">
    		<form id="formResetPassword" action="user/resetpassword" class="span6 offset3">
	    		<div class="page-header">
	    			<h2>1.&nbsp;请输入手机号码</h2>
	    		</div>
	    		<input type="text" name="phoneNumber" id="iptPhoneNumber" pattern="[0-9]{11}" maxlength="11" />
	    		<br>
	    		<button type="button" class="btn" id="btnGetPhoneCode" >获取手机验证码</button>
	    		<div class="page-header">
	    			<h2>2.&nbsp;请输入手机验证码</h2>
	    		</div>
	    		<input type="text" name="phoneCode" id="iptPhoneCode" pattern="[0-9]{6}" maxlength="6" />
	    		<div class="page-header">
	    			<h2>3.&nbsp;设置新密码</h2>
	    		</div>
	    		<label>请输入新密码</label>
	    		<input type="password" name="newPwd" id="iptNewPwd" />
	    		<label>请再次输入新密码</label>
	    		<input type="password" name="newPwdConfirm" id="iptNewPwdConfirm" />
	    		<hr>
	    		<button type="submit" class="btn btn-primary btn-large">确&nbsp;定</button>
    		</form>
    	</div>
 	
		<jsp:include page="common/_footer.jsp"></jsp:include>
    </div> <!-- /container -->

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="/imeeting/js/lib/jquery-1.8.0.min.js"></script>
    <script src="/imeeting/js/lib/bootstrap.js"></script>
	<script type="text/javascript">
		$(function(){
			$("#btnGetPhoneCode").click(function(){
				var phoneNumber = $("#iptPhoneNumber").val();
				if (phoneNumber.length != 11 || !$.isNumeric(phoneNumber)){
					alert("手机号码格式错误");
					return;
				} 
				$.post("user/validatePhoneNumber", 
					{ phone: phoneNumber },
					function(data){
						if ("404" == data){
							alert("该号码尚未注册");
						} else
						if ("200" != data){
							alert("未知错误：" + data);
						}
					});
			});
			
			$("#formResetPassword").submit(function(){
				var phoneNumber = $("#iptPhoneNumber").val();
				var phoneCode = $("#iptPhoneCode").val();
				var newPassword = $("#iptNewPwd").val();
				var newPasswordConfirm = $("#iptNewPwdConfirm").val();
				$.post("user/resetPassword",
					{
						phone: phoneNumber,
						code: phoneCode,
						newPwd: newPassword,
						newPwdConfirm: newPasswordConfirm
					},
					function(data){
						if ("200" == data){
							alert("重置密码成功");
						} else 
						if ("400" == data){
							alert("缺少参数");
						} else
						if ("401" == data){
							alert("验证码与手机号码不匹配");
						} else 
						if ("403" == data) {
							alert("新密码两次输入不一致");
						} else 
						if ("410" == data){
							alert("操作超时，请重试");
						} else
						if ("500" == data){
							alert("服务器内部错误");
						} else {
							alert("未知错误：[" + data + "]");
						}
					});
				
				return false;
			});
		});
	</script>
  </body>
</html>
