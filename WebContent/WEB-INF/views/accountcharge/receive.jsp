<%@page import="com.imeeting.constants.WebConstants"%>
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
			<div>
			<%
				String result = String.valueOf(request.getAttribute("result"));
				if (result.equals("0")) {
					String accountName = (String) request.getAttribute(WebConstants.pay_account_name.name());
					String chargeMoney = (String) request.getAttribute(WebConstants.charge_money.name());
					if (accountName == null) {
					%>
						<p>充值出现异常，系统未能成功入账，请联系管理员(QQ：1622122511，电话：0551-2379996)！</p>
					<%
					} else {
					%>
						<p>用户您好,</p>
						<br/>
						<p>您的账户：<%=accountName %> 成功充值<%=chargeMoney %>元。</p>
					<%
						
					}
				} else {
			%>
					<p>用户您好,</p>
					<br/>
					<p>您此次充值不成功！</p>
			<%
				}
			%>
			<div class="link_region">
				<a href="home">返回首页</a>
				<a href="deposite">继续充值</a>
			</div>
			</div>
		</div>
		<jsp:include page="../common/_footer.jsp"></jsp:include>
	</div>


	<!-- Le javascript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="js/lib/jquery-1.8.0.min.js"></script>
	<script src="js/lib/bootstrap.js"></script>
</body>