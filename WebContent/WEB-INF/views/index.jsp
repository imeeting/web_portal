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
    	<div class="row">
    		<div class="span4 offset2">
    			<div class="row">
    				<div class="span3 offset1">
    					<div class="app_demo_view">
		    				<img alt="iphone" src="./img/iphone_frame.png"/>
		    				<img class="app_demo_img" src="./img/app_demo.jpg"/>
	    				</div>
    				</div>
    			</div>
    		</div>
    		<div class="span4">
    			<div class="page-header">
	    			<h1>智会&nbsp;<small>高效能人士的智慧选择</small></h1>
    			</div>
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
    				<a class="btn">
    					<div>
    						<img class="pull-left" alt="app store" src="./img/iphone.png">
    						<div class="pull-right">
    							<p><strong>&nbsp;智会 iPhone 版</strong><br>App Store 下载</p>
    						</div>
    					</div>
    				</a>
    				<a class="btn">
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
    				<label>输入手机号码，短信获取下载地址</label>
    				<input type="text" name="phoneNumber" id="iptPhoneNumber" pattern="[0-9]{11}" maxlength="11" />
    				<br>
	    			<button type="submit" class="btn btn-success" id="btnGetDownloadURL" >获取下载地址</button>
    			</form>    			
    		</div>
    	</div>
    	
    	<jsp:include page="common/_footer.jsp"></jsp:include>
    </div> <!-- /container -->

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="/imeeting/js/lib/jquery-1.8.0.min.js"></script>
    <script src="/imeeting/js/lib/bootstrap.js"></script>
	<script type="text/javascript">
		$("#formGetDownlaodUrl").submit(function(){
			var phoneNumber = $("#iptPhoneNumber").val();
			if (phoneNumber == null || phoneNumber == "") {
				alert("还没有输入手机号码呢！");
				return false;
			}
			$.post("/imeeting/getDownloadPageUrl", 
				{phoneNumber: phoneNumber},
				function(data){
					var result = data.result;
					switch (result) {
					case "ok":
						alert("短信成功发送到手机啦，快去下载吧！");
						break;
					case "fail":
						alert("短信发送失败，检查一下手机号码吧，或者直接点击图标下载。");
						break;
					
					}
				}, "json")
				.error(function() {
					alert("服务器或者网络出现现在有点困难，等会儿再试吧！");
				});
			return false;
		});
	</script>
  </body>
</html>
