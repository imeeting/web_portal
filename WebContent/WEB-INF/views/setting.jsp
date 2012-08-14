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
						<a href="#pane-change-password" data-toggle="tab">修改密码</a>
					</li>
					<li>
						<a href="#pane-user-info" data-toggle="tab">基本信息</a>
					</li>
					<li>
						<a href="#pane-user-avatar" data-toggle="tab">设置头像</a>
					</li>					
				</ul>
				<div class="tab-content">			
					<div class="tab-pane active" id="pane-change-password">
						<h3>修改密码</h3>
						<hr>
						<form action="profile/changepassword" method="post" >
							<label>输入当前使用的密码</label>
							<input type="password" name="oldPwd" />
							<label>输入新密码</label>
							<input type="password" name="newPwd" />
							<label>再次输入新密码</label>
							<input type="password" name="newPwdConfirm" />
							<br>
							<button type="submit" class="btn btn-primary">确&nbsp;定</button>
						</form>
					</div>
					<div class="tab-pane" id="pane-user-info">
						<p>我们正在紧张开发中，请耐心等待。。。</p>
					</div>	
					<div class="tab-pane" id="pane-user-avatar">
						<p>我们正在紧张开发中，请耐心等待。。。</p>
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
