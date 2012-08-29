<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh">
  <head>
    <title>智会</title>
	<jsp:include page="common/_head.jsp"></jsp:include>
  </head>

  <body>
    <div class="container">
    	<div class="row">
    		<div class="span4 offset2">
    			<div class="row">
    				<div class="span3 offset1">
	    				<div class="app_demo_view">
		    				<img alt="iphone" src="./img/app_screen.png"/>
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
    			<h3>立刻下载开始你的智会之旅<br>&nbsp;</h3>
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
    		</div>
    	</div>
    	
    	<jsp:include page="common/_footer.jsp"></jsp:include>
    </div> <!-- /container -->

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="/imeeting/js/lib/jquery-1.8.0.min.js"></script>
    <script src="/imeeting/js/lib/bootstrap.min.js"></script>

  </body>
</html>
