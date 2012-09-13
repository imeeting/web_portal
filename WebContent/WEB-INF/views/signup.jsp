<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh">
  <head>
    <title>智会-注册新用户</title>
	<jsp:include page="common/_head.jsp"></jsp:include>
  </head>

  <body>
  	<jsp:include page="common/beforelogin_navibar.jsp"></jsp:include>

    <div class="container">
    	<div class="row">
    		<form id="formSignup" action="user/resetpassword" class="span6 offset3">
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
	    			<h2>3.&nbsp;设置密码</h2>
	    		</div>
	    		<label>请输入密码</label>
	    		<input type="password" name="password" id="iptPassword" />
	    		<label>请再次输入密码</label>
	    		<input type="password" name="confirmPassword" id="iptConfirmPassword" />
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
    <script src="/imeeting/js/lib/bootstrap.min.js"></script>
	<script type="text/javascript">
		$(function(){
			$("#btnGetPhoneCode").click(function(){
				var phoneNumber = $("#iptPhoneNumber").val();
				if (phoneNumber.length != 11 || !$.isNumeric(phoneNumber)){
					alert("手机号码格式错误");
					return;
				} 
				$.post("user/getPhoneCode", 
					{ phone: phoneNumber },
					function(data){
						if ("0" == data.result){
							alert("验证码已发送，注意查看手机短信。");
						} else
						if ("3" == data.result){
							alert("该号码已注册，请勿重复使用！");
						} else
						if ("2" == data.result) {
							alert("无效的手机号码！");
						} else {
							alert("错误：" + data.result);
						}
					}, "json");
			});
			
			$("#formSignup").submit(function(){
				var phoneNumber = $("#iptPhoneNumber").val();
				var phoneCode = $("#iptPhoneCode").val();
				var password = $("#iptPassword").val();
				var confirmPassword = $("#iptConfirmPassword").val();
				$.post("user/websignup",
					{
						phone: phoneNumber,
						code: phoneCode,
						pwd: password,
						confirmPwd: confirmPassword
					},
					function(data){
						if ("0" == data){
							alert("注册成功！");
						} else 
						if ("400" == data){
							alert("数据不完整");
						} else
						if ("401" == data){
							alert("验证码与手机号码不匹配");
						} else 
						if ("403" == data) {
							alert("两次输入的密码不一致");
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
