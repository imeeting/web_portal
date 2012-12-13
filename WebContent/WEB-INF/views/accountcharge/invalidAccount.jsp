<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<% String accountName = request.getParameter("account_name"); %>
<!DOCTYPE html>
<html lang="zh">
  <head>
    <title>智会-无效的充值账户</title>
	<jsp:include page="../common/_head.jsp"></jsp:include>
  </head>

  <body>
    <jsp:include page="../common/beforelogin_navibar.jsp"></jsp:include>

    <div class="container">
    	<div class="row-fluid im-container">
	    	<div class="hero-unit">
				<h1>亲，账户&nbsp;<%=accountName %>&nbsp;不存在！</h1>
				<hr>
				<div class="alert alert-error">
					<h2>请仔细检查你输入的账户名&nbsp;<%=accountName %>&nbsp;是否正确</h2>
					<hr>
					<h2><a href="deposite">点击这里返回充值页面</a></h2>
				</div>
	    	</div>
    	</div>
    	
		<jsp:include page="../common/_footer.jsp"></jsp:include>
    </div> <!-- /container -->

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="/imeeting/js/lib/jquery-1.8.0.min.js"></script>
    <script src="/imeeting/js/lib/bootstrap.js"></script>
  </body>
</html>
