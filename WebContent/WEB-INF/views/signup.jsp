<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@page import="com.imeeting.mvc.controller.UserController" %>
<%
  Integer errorCode = (Integer)request.getAttribute(UserController.ErrorCode);
  String errorInfo = null;
  if (null != errorCode){
	  if (HttpServletResponse.SC_GONE == errorCode){
		  errorInfo = "操作超时，请重试";
	  } else if (HttpServletResponse.SC_BAD_REQUEST == errorCode) {
		  errorInfo = "数据不完整";
	  } else if (HttpServletResponse.SC_UNAUTHORIZED == errorCode) {
		  errorInfo = "验证码与手机号码不匹配";
	  } else if (HttpServletResponse.SC_FORBIDDEN == errorCode) {
		  errorInfo = "两次输入的密码不一致";
	  } else if (HttpServletResponse.SC_INTERNAL_SERVER_ERROR == errorCode) {
		  errorInfo = "服务器内部错误";
	  }
  }
  
  Integer phoneNumberError = 
	  (Integer)request.getAttribute(UserController.PhoneNumberError);
  String phoneNumberErrorInfo = "";
  if (null != phoneNumberError){
	  if (HttpServletResponse.SC_BAD_REQUEST == phoneNumberError){
		  phoneNumberErrorInfo = "手机号码不能为空";
	  }
  }
  
  Integer phoneCodeError = 
	  (Integer)request.getAttribute(UserController.PhoneCodeError);
  String phoneCodeErrorInfo = "";
  if (null != phoneCodeError){
	  if (HttpServletResponse.SC_BAD_REQUEST == phoneCodeError){
		  phoneCodeErrorInfo = "验证码不能为空";
	  } else if (HttpServletResponse.SC_UNAUTHORIZED == phoneCodeError){
		  phoneCodeErrorInfo = "验证码不正确";
	  }
  }
  
  Integer passwordError = 
      (Integer)request.getAttribute(UserController.PasswordError);  
  String passwordErrorInfo = "";
  if (null != passwordError){
	  if (HttpServletResponse.SC_BAD_REQUEST == passwordError){
		  passwordErrorInfo = "密码不能为空";
	  } 
  }
  
  Integer confirmPasswordError = 
      (Integer)request.getAttribute(UserController.ConfirmPasswordError);
  String confirmPasswordErrorInfo = "";
  if (null != confirmPasswordError){
	  if (HttpServletResponse.SC_BAD_REQUEST == confirmPasswordError){
		  confirmPasswordErrorInfo = "确认密码不能为空";
	  } else if (HttpServletResponse.SC_FORBIDDEN == confirmPasswordError){
		  confirmPasswordErrorInfo = "两次输入密码不一致";
	  }
  }
  
  String phoneNumber = "";
  if (null != request.getParameter("phoneNumber")){
	  phoneNumber = request.getParameter("phoneNumber");
  }
  
  String phoneCode = "";
  if (null != request.getParameter("phoneCode")){
	  phoneCode = request.getParameter("phoneCode");
  }

%>    
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
    	<% if (null != errorCode && HttpServletResponse.SC_OK == errorCode) { %>
    	   <div class="page-header span4 offset4">
    	       <h2>恭喜你，注册成功！<a href="/imeeting/signin">点此登录</a></h2>
    	   </div>
    	<% } else { %>
	    	<form id="formSignup" action="/imeeting/user/websignup" method="post" class="span6 offset3">
		    	<div class="page-header">
			    	<h2>使用手机号码注册</h2>
			    </div>
			    <% if (null != errorInfo) { %>
			    <div class="alert alert-error">
			     <strong>错误：</strong><%=errorInfo %>
			    </div>
			    <% } %>
	    		<div id="divPhoneNumberCtrl" class="control-group <%=phoneNumberErrorInfo.isEmpty()?"":"error"%>">
	    		    <label class="control-label" for="iptPhoneNumber">手机号</label>
	    		    <div class="controls">
			    		<input type="text" name="phoneNumber" id="iptPhoneNumber" 
			    		pattern="[0-9]{11}" maxlength="11" value="<%=phoneNumber %>" />
			    		<button type="button" class="btn" id="btnGetPhoneCode">获取手机验证码</button>
			    		<span id="spanPhoneNumberInfo" class="help-inline">
			    		<%=phoneNumberErrorInfo %>
			    		</span>
		    		</div>
	    		</div>
	    		<div class="control-group <%=phoneCodeErrorInfo.isEmpty()?"":"error"%>">
	    		    <label class="control-label" for="iptPhoneCode">验证码</label>
	    		    <div class="controls">
		    		    <input type="text" name="phoneCode" id="iptPhoneCode" 
		    		    pattern="[0-9]{6}" maxlength="6" value="<%=phoneCode %>" />
		    		    <span id="spanPhoneCodeInfo" class="help-inline">
		    		    <%=phoneCodeErrorInfo %>
		    		    </span>
		    		</div>
		    	</div>
		    	<div class="control-group <%=passwordErrorInfo.isEmpty()?"":"error"%>">
		    	    <label class="control-label" for="iptPassword">密码</label>
		    	    <div class="controls">
		    		    <input type="password" name="password" id="iptPassword" />
		    		    <span id="spanPasswordInfo" class="help-inline">
		    		    <%=passwordErrorInfo %>
		    		    </span>
		    		</div>
		    	</div>
		    	<div class="control-group <%=confirmPasswordErrorInfo.isEmpty()?"":"error"%>">
		    	    <label class="control-label" for="iptConfirmPassword">确认密码</label>
		    	    <div class="controls">
		    		    <input type="password" name="confirmPassword" id="iptConfirmPassword" />
		    		    <span id="spanConfirmPasswordInfo" class="help-inline">
		    		    <%=confirmPasswordErrorInfo %>
		    		    </span>
		    		</div>
		    	</div>
		    	<hr>
		    	<div class="control-group">
		    	    <div class="controls">
			    	    <button type="submit" class="btn btn-primary">提交注册信息</button>
			    	</div>
		    	</div>
	    	</form>
	    <% } %>
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
			function disable60Seconds(){
				var $btn = $("#btnGetPhoneCode");
				var oldHtml = $btn.html();
	            $btn.attr("disabled", true);
	            var seconds = 60;
	            var itvl = setInterval(function(){
	                $btn.html(seconds + " 秒后可重试");
	                seconds -= 1;
	                if (seconds < 0){
	                    $btn.html(oldHtml);
	                    $("#spanPhoneNumberInfo").html("");
	                    clearInterval(itvl);
	                    $btn.attr("disabled", false);
	                }
	            }, 1000);
			}
			
			$("#btnGetPhoneCode").click(function(){
				var $span = $("#spanPhoneNumberInfo");
				var $divCtrl = $("#divPhoneNumberCtrl");
				var phoneNumber = $("#iptPhoneNumber").val();
				if (phoneNumber.length != 11 || !$.isNumeric(phoneNumber)){
					$divCtrl.addClass("error");
					$span.html("手机号码格式错误");
					return;
				} 
				$span.html("");
				$divCtrl.removeClass("error");
				$divCtrl.removeClass("success");
				$.post("/imeeting/user/getPhoneCode", 
					{ phone: phoneNumber },
					function(data){
						if ("0" == data.result){
							disable60Seconds();
							$divCtrl.addClass("success");
							$span.html("验证码已发送，注意查看手机短信。");
						} else
						if ("3" == data.result){
							$divCtrl.addClass("error");
							$span.html("该号码已注册，请勿重复使用！");
						} else
						if ("2" == data.result) {
							$divCtrl.addClass("error");
							$span.html("无效的手机号码！");
						} else {
							$divCtrl.addClass("error");
							$span.html("错误：" + data.result);
						}
					}, "json");
			});
			/*
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
			*/
		});
	</script>
  </body>
</html>
