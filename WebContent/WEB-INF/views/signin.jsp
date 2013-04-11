<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh">
  <head>
    <title>智会-登录</title>
	<jsp:include page="common/_head.jsp"></jsp:include>
  </head>

  <body>
    <jsp:include page="common/beforelogin_navibar.jsp"></jsp:include>

    <div class="container">
    	<div class="row-fluid im-container">
			<form id="signin-form" action="" class="span4 offset4 well">
			    <div id="divUserNameAlert" class="control-group">
				    <label class="control-label" for="username">手机号码</label>
				    <div class="controls">
					    <input id="iptUserName" name="loginName" class="span12" 
					    type="text" pattern="[0-9]{11}" maxlength="11"/>
	                    <span id="spanUserNameAlert" class="help-block"></span>
				    </div>
                </div>
                <div id="divPasswordAlert" class="control-group">			
					<label class="control-panel" for="password">密码</label>
					<div class="controls">
					   <input id="iptPassword" name="loginPwd" class="span12" type="password" />
	                   <span id="spanPasswordAlert" class="help-block"></span>
					</div>
				</div>
				<div id="divSigninAlert" class="control-group">
				    <p class="clearfix">
					<button type="submit" class="btn btn-primary pull-left">登&nbsp;录</button>
					<a class="pull-right" href="forgetpwd" target="blank">（忘记密码）</a>
					</p>
					<span id="spanSigninAlert" class="help-block"></span>
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
    <script src="/imeeting/js/lib/md5.js"></script>
	<script type="text/javascript">
	function isValidUserName(){
		var $divCtrl = $("#divUserNameAlert");
		var $span = $("#spanUserNameAlert");
		var userName = $("#iptUserName").val();
		if (!$.isNumeric(userName) || 
			userName.length != 11 ||
			userName.charAt(0) != '1')
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
	
	function isValidPassword(){
		var $divCtrl = $("#divPasswordAlert");
		var $span = $("#spanPasswordAlert");
		var password = $("#iptPassword").val();
		if (password == ""){
	         $divCtrl.addClass("error");
	         $span.html("密码不能为空");
	         return false;
		} else {
            $divCtrl.removeClass("error");
            $span.html("");
            return true;			
		}
	}
	
	$("#signin-form").submit(function() {
        var $divCtrl = $("#divSigninAlert");
        var $span = $("#spanSigninAlert");
        
		if (isValidUserName() && isValidPassword()){
			var username = $("#iptUserName").val();
		    var pwd = $("#iptPassword").val();
		    var jqxhr = $.post("/imeeting/user/login",
		    	{
		         loginName: username,
		         loginPwd: md5(pwd)
		    	}, 
		    	function(data){
		    		if (data.result == "0"){
		    			location.href = "/imeeting/myconference";
		    		} else {
		                $divCtrl.addClass("error");
		                $span.html("登录失败，请仔细检查你输入的用户名和密码。");
		    		}
		    	}, "json");
		    
            jqxhr.fail(function(jqXHR, textStatus, errorThown){
            	$divCtrl.addClass("error");
                if ("error" == textStatus){
                	$span.html("请求错误：" + jqXHR.status);
                } else {
                	$span.html("请求失败：" + textStatus);
                }
            });
		}
		return false;
	});
	</script>
  </body>
</html>
