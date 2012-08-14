<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh">
  <head>
    <title>智会-登录</title>
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
              <li><a href="home">首页</a></li>
              <!-- 
              <li><a href="features">功能介绍</a></li>
               -->
              <li><a href="deposite">在线充值</a></li>
              <li class="active"><a href="signin">登录</a></li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <div class="container">
    	<div class="row">
			<form id="signin-form" action="" class="span4 offset4 well">
				<label>手机号码</label>
				<input id="username" class="span4" type="text" />
				<label>密码&nbsp;<small><a href="forgetpwd">（忘记密码）</a></small></label>
				<input id="password" class="span4" type="password" />
				<button type="submit" class="btn btn-primary">登&nbsp;录</button>
			</form>
    	</div>
    	
		<jsp:include page="common/_footer.jsp"></jsp:include>
    </div> <!-- /container -->

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.0/jquery.min.js"></script>
    <script src="js/lib/bootstrap.js"></script>
    <script src="js/lib/md5.js"></script>
    <script src="js/applib/common.js"></script>
	<script src="js/signin.js"></script>
  </body>
</html>
