<%@page import="com.imeeting.framework.ContextLoader"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh">
  <head>
    <title>智会</title>
	<jsp:include page="common/_head.jsp"></jsp:include>
  </head>

  <body>
	<jsp:include page="common/beforelogin_navibar.jsp"></jsp:include>

    <div class="container">
    	<div class="row-fluid im-container">
    		<div class="span4 offset2">
 			    <div class="app_demo_view">
   				 <img alt="iphone" src="./img/app_screen.png"/>
  				</div>
    		</div>
    		<div class="span4">
    			<h1>智会&nbsp;<small>高效能人士的智慧选择</small></h1>
    			<ul>
    				<li><h2><small>多人同时通话</small></h2></li>
	    			<li><h2><small>高清语音质量</small></h2></li>
	    			<li><h2><small>多路视频随意切换</small></h2></li>
	    			<li><h2><small>操作快捷方便</small></h2></li>
    			</ul>
    			<hr>
    			<!-- <h3>现在注册就送<strong>100元</strong>话费<br>&nbsp;</h3>-->
    			<!-- <h3>立刻下载开始你的智会之旅<br>&nbsp;</h3> -->
    			<div>
    				<a class="btn btn-inverse" href="https://itunes.apple.com/us/app/zhi-hui/id554959651?ls=1&mt=8">
    					<div>
    						<img class="pull-left" alt="app store" src="./img/iphone.png">
    						<div class="pull-right">
    							<p><strong>&nbsp;智会 iPhone 版</strong><br>App Store 下载</p>
    						</div>
    					</div>
    				</a>
    				<a class="btn btn-inverse" href="/imeeting/downloadAppClient/android">
    					<div>
    						<img class="pull-left" alt="app store" src="./img/android.png">
    						<div class="pull-right">
    							<p><strong>&nbsp;智会 Android 版</strong><br>下载 apk 文件</p>
    						</div>
    					</div>
    				</a>    				
    			</div>
    			<hr>
    			<form id="formGetDownlaodUrl" action="#">
                    <div id="divControlGroup" class="control-group">
	    				<label class="control-label" for="iptPhoneNumber">输入手机号码，短信获取下载地址</label>
	    				<div class="controls input-prepend input-append">
		    				<span class="add-on">+86</span><input type="text" 
		    				class="input-medium" name="phoneNumber" id="iptPhoneNumber" 
		    				pattern="[0-9]{11}" maxlength="11" 
		    				placeholder="仅限中国大陆手机号码"/><button type="submit" 
		    				class="btn" id="btnGetDownloadURL" >获取下载地址</button>
	    				</div>
                    </div>
                    <div id="divAlert" class="control-group">
	    				<span id="spanAlert" class="help-inline"></span>
                    </div>
    			</form>    			
    		</div>
    	</div>
    	
    	<jsp:include page="common/_footer.jsp"></jsp:include>
    </div> <!-- /container -->
    
    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="/imeeting/js/lib/jquery-1.8.0.min.js"></script>
    <script src="/imeeting/js/lib/bootstrap.min.js"></script>
	<script type="text/javascript">
	    var $divCtrl = $("#divAlert");
	    var $spanAlert = $("#spanAlert");
    
        function info(level, msg){
            $divCtrl.removeClass("success");
            $divCtrl.removeClass("error");
            if (level){
	            $divCtrl.addClass(level);
            }
            $spanAlert.html(msg);
        }
	
		$("#formGetDownlaodUrl").submit(function(){
			var phoneNumber = $("#iptPhoneNumber").val();
			if (phoneNumber == null || phoneNumber == "") {
				info("error", "还没有输入手机号码呢！");
				return false;
			}
			
			var $btn = $("#btnGetDownloadURL");
			var btnTitle = $btn.html();
			$btn.attr("disabled", true);
			var seconds = 60;
			var itvl = setInterval(function(){
				$btn.html(seconds + "秒后可重试");
				seconds -= 1;
				if (seconds < 0){
				    info(null, "");					
					$btn.html(btnTitle);
					$btn.attr("disabled", false);
					clearInterval(itvl);
				}
			}, 1000);
			
			$.post("/imeeting/getDownloadPageUrl", 
				{phoneNumber: phoneNumber},
				function(data){
					var result = data.result;
					switch (result) {
					case "ok":
						info("success", "短信已发送，注意查看手机");
						break;
					case "fail":
						info("error", "短信发送失败，检查一下手机号码吧，或者直接点击图标下载。");
						break;
					}
				}, "json")
				.error(function() {
					info("error", "额。。网络正忙，请稍后再试吧！");
				});
			return false;
		});
	</script>
  </body>
</html>
