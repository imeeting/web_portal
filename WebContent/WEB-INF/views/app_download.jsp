<%@page import="com.imeeting.framework.ContextLoader"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>智会-客户端下载</title>
<jsp:include page="common/_head.jsp"></jsp:include>
</head>
<body>
	<h3>智会客户端下载</h3>
	<br/>
	<div>
		<a class="btn" style="margin: 5px;">
			<div>
				<img class="pull-left" alt="app store" src="./img/iphone.png">
				<div class="pull-right">
					<p>
						<strong>&nbsp;智会 iPhone 版</strong><br>App Store 下载
					</p>
				</div>
			</div>
		</a> 
		<a class="btn" style="margin: 5px;" href="/imeeting/downloadAppClient/android">
			<div>
				<img class="pull-left" alt="app store" src="./img/android.png">
				<div class="pull-right">
					<p>
						<strong>&nbsp;智会 Android 版</strong><br>下载 apk 文件
					</p>
				</div>
			</div>
		</a>
	</div>
</body>
</html>