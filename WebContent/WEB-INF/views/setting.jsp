<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh">
  <head>
    <title>智会-系统设置</title>
	<jsp:include page="common/_head.jsp"></jsp:include>
  </head>

  <body>
    <jsp:include page="common/afterlogin_navibar.jsp"></jsp:include>

    <div class="container">
		<div class="row">
			<div class="span6 offset3 tabbable tabs-left">
				<ul class="nav nav-tabs">
					<li class="active">
						<a href="#pane-user-info" data-toggle="tab">基本信息</a>
					</li>
					<li>
						<a href="#pane-change-password" data-toggle="tab">修改密码</a>
					</li>
				</ul>
				<div class="tab-content">
					<div class="tab-pane" id="pane-user-info">
						<p>用户信息</p>
					</div>				
					<div class="tab-pane" id="pane-change-password">
						<p>修改密码啊</p>
					</div>
				</div>
			</div>
		</div>    	
		<jsp:include page="common/_footer.jsp"></jsp:include>
    </div> <!-- /container -->

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="http://code.jquery.com/jquery-1.7.2.js"></script>
    <script src="js/lib/bootstrap.js"></script>

  </body>
</html>
