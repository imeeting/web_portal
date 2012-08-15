<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh">
<head>
<title>智会</title>
<jsp:include page="../common/_head.jsp"></jsp:include>
</head>
<body>
	<jsp:include page="../common/beforelogin_navibar.jsp"></jsp:include>
	<div class="container">
		<div class="pay_result_center">
			<%
				String result = String.valueOf(request.getAttribute("result"));
				if (result.equals("0")) {
			%>
			<span>充值成功</span>
			<%
				} else {
			%>
			<span>充值失败</span>
			<%
				}
			%>
			<a href="/home">关闭</a>
		</div>
		<jsp:include page="../common/_footer.jsp"></jsp:include>
	</div>


	<!-- Le javascript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="js/lib/jquery-1.8.0.min.js"></script>
	<script src="js/lib/bootstrap.js"></script>
</body>