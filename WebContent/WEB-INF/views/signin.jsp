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
    	<div class="row">
			<form id="signin-form" action="" class="span4 offset4 well">
				<label>手机号码</label>
				<input id="username" class="span4" type="text" />
				<label>密码&nbsp;<small><a href="forgetpwd" target="blank">（忘记密码）</a></small></label>
				<input id="password" class="span4" type="password" />
				<button type="submit" class="btn btn-primary">登&nbsp;录</button>
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
    <script src="/imeeting/js/applib/common.js"></script>
	<script src="/imeeting/js/signin.js"></script>
  </body>
</html>
