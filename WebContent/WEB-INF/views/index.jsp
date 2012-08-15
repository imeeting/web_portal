<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh">
  <head>
    <title>智会</title>
	<jsp:include page="common/_head.jsp"></jsp:include>
  </head>

  <body>
    <div class="navbar navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </a>
          <a class="brand" href="#">智会</a>
          <div class="nav-collapse">
            <ul class="nav pull-right">
              <li class="active"><a href="home">首页</a></li>
              <!-- 
              <li><a href="features">功能介绍</a></li>
               -->
              <li><a href="deposite">在线充值</a></li>
              <li><a href="signin">登录</a></li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <div class="container">
    	<div class="row">
    		<div class="span4 offset2">
    			<div class="row">
    				<div class="span3 offset1">
	    				<img alt="iphone" src="./img/iphone_frame.png">
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
	    			<li><h2><small>操作方便快捷</small></h2></li>
    			</ul>
    			<hr>
    			<h3>现在注册就送<strong>100元</strong>话费<br>&nbsp;</h3>
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
    <script src="js/lib/jquery-1.8.0.min.js"></script>
    <script src="js/lib/bootstrap.js"></script>

  </body>
</html>
