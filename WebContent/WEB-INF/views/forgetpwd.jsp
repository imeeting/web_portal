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
    		<form id="formResetPassword" action="user/resetpassword" class="im-form span6 offset3">
                <h2>重新设置密码</h2>
                <hr>
                <div id="divPhoneNumberCtrl" class="control-group">
                    <label class="control-label" for="iptPhoneNumber">手机号</label>
                    <div class="controls">
                        <input type="text" name="phoneNumber" id="iptPhoneNumber" 
                        pattern="[0-9]{11}" maxlength="11" value="" />
                        <button type="button" class="btn" id="btnGetPhoneCode">获取手机验证码</button>
                        <span id="spanPhoneNumberInfo" class="help-inline"></span>
                    </div>
                </div>
                <div id="divPhoneCode" class="control-group">
                    <label class="control-label" for="iptPhoneCode">验证码</label>
                    <div class="controls">
                        <input type="text" name="phoneCode" id="iptPhoneCode" 
                        pattern="[0-9]{6}" maxlength="6" value="" />
                        <span id="spanPhoneCodeInfo" class="help-inline"></span>
                    </div>
                </div>
                <div id="divPassword" class="control-group">
                    <label class="control-label" for="iptPassword">输入新密码</label>
                    <div class="controls">
                        <input type="password" name="password" id="iptPassword" />
                        <span id="spanPasswordInfo" class="help-inline"></span>
                    </div>
                </div>
                <div id="divConfirmPassword" class="control-group">
                    <label class="control-label" for="iptConfirmPassword">再次输入新密码</label>
                    <div class="controls">
                        <input type="password" name="confirmPassword" id="iptConfirmPassword" />
                        <span id="spanConfirmPasswordInfo" class="help-inline"></span>
                    </div>
                </div>
                <hr>
                <div id="divSubmit" class="control-group">
                    <div class="controls">
                        <button type="submit" class="btn btn-primary">提交新的密码</button>
                        <span id="spanSubmit" class="help-inline"></span>
                    </div>
                </div>
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
	var $_divCtrl = $("#divPhoneNumberCtrl");
	
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
                $btn.attr("disabled", false);
                $_divCtrl.removeClass("success");
                $_divCtrl.removeClass("error");
                $("#spanPhoneNumberInfo").html("");
                clearInterval(itvl);
            }
        }, 1000);
    }
    
    function isValidPhoneNumber($divCtrl, phoneNumber){
    	var $span = $divCtrl.find(".help-inline");
        if (!$.isNumeric(phoneNumber) ||
            phoneNumber.length != 11 || 
            phoneNumber.charAt(0) != '1')
        {
            $divCtrl.addClass("error");
            $span.html("手机号码格式错误");
            return false;
        } else {
            $divCtrl.removeClass("error");
            $span.html("");
            return true;        	
        }
    }
    
    function isValidInput($divCtrl, value){
        var $span = $divCtrl.find(".help-inline");
        if (value == ""){
            $divCtrl.addClass("error");
            $span.html("不能为空");
            return false;
        } else {
            $divCtrl.removeClass("error");
            $span.html("");
            return true;            
        }
    }
    
    $("#btnGetPhoneCode").click(function(){
        var phoneNumber = $("#iptPhoneNumber").val();
        if (!isValidPhoneNumber($_divCtrl, phoneNumber)){
        	return;
        }
        
        var $span = $("#spanPhoneNumberInfo");
        $_divCtrl.removeClass("error");
        $_divCtrl.removeClass("success");
        $.post("/imeeting/user/validatePhoneNumber", 
            { phone: phoneNumber },
            function(data){
                if ("200" == data){
                    disable60Seconds();
                    $_divCtrl.addClass("success");
                    $span.html("验证码已发送，注意查看手机短信。");
                } else
                if ("404" == data){
                    $_divCtrl.addClass("error");
                    $span.html("该号码尚未注册");
                } else {
                    $_divCtrl.addClass("error");
                    $span.html("错误：" + data.result);
                }
            }, "json");
    });
    
	$("#formResetPassword").submit(function(){
		var isValid = true;
		var phoneNumber = $("#iptPhoneNumber").val();
        isValid = isValidPhoneNumber($_divCtrl, phoneNumber) && isValid;
        
        var $divPhoneCode = $("#divPhoneCode");
		var phoneCode = $("#iptPhoneCode").val();
		isValid = isValidInput($divPhoneCode, phoneCode) && isValid;
		
		var $divPassword = $("#divPassword");
		var newPassword = $("#iptPassword").val();
        isValid = isValidInput($divPassword, newPassword) && isValid;
        
        var $divCofirmPassword = $("#divConfirmPassword");
		var newPasswordConfirm = $("#iptConfirmPassword").val();
        isValid = isValidInput($divCofirmPassword, newPasswordConfirm) && isValid;
        
        if (!isValid){
        	return false;
        }
        
        $divSubmit = $("#divSubmit");
        $spanSubmit = $("#spanSubmit");
        
		$.post("user/resetPassword",
			{
				phone: phoneNumber,
				code: phoneCode,
				newPwd: newPassword,
				newPwdConfirm: newPasswordConfirm
			},
			function(data){
				if ("200" == data){
					$divSubmit.removeClass("error");
					$spanSubmit.html("");
					alert("重置密码成功");
				} else 
				if ("400" == data){
					$divSubmit.addClass("error");
					$spanSubmit.html("缺少参数");
				} else
				if ("401" == data || "410" == data){
					$divSubmit.addClass("error");
					$spanSubmit.html("验证码与手机号码不匹配");
				} else 
				if ("403" == data) {
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
			});
		
		return false;
	});
	</script>
  </body>
</html>
