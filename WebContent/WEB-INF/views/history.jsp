<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh">
  <head>
    <title>智会-我的群聊</title>
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
          <ul class="nav">
          	<li><a href="#">退出登录</a></li>
          </ul>
          <div class="nav-collapse">
            <ul class="nav pull-right">
              <li><a href="account">充值账户</a></li>
              <li class="active"><a href="history">我的群聊</a></li>
              <li><a href="setting">系统设置</a></li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <div class="container">
    	<div class="row">
    		<div class="span8 offset2 page-header">
    			<h2>我参加过的群聊</h2>
    		</div>
    	</div>
		<div class="row">
			<div class="span8 offset2">
			<% for (int i=0; i<10; i++) { %>
			<div class="well clearfix">
				<div class="pull-left">
					<h3><%="这里是群聊标题"%></h3>
					<div class="clearfix">
						<% for (int j=0; j<4; j++) {%>
						<div class="pull-left">
							<img alt="avatar" src="img/avatar.jpg">
							<p><%="胡光辉" %></p>
						</div>
						<% } %>
					</div>
				</div>
				<div class="pull-right">
					<h3><small>群聊号：<strong><%="123456"%></strong></small></h3>
					<h3><small>
						<strong><%="08"%></strong>月<strong><%="11"%></strong>日<br>
						<%="星期日"%><br>
						<strong><%="下午"%>&nbsp;<%="17:05"%></strong>
					</small></h3>
				</div>
			</div>
			<% } %>
			<ul class="pager">
				<li class="previous"><a href=#>上一页</a></li>
				<li class="next"><a href=#>下一页</a></li>
			</ul>
			</div>
		</div>    	
		<jsp:include page="common/_footer.jsp"></jsp:include>
    </div> <!-- /container -->

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="http://code.jquery.com/jquery-1.7.2.js"></script>
    <script src="js/bootstrap.js"></script>

  </body>
</html>
