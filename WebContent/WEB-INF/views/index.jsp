<%@page import="com.imeeting.framework.ContextLoader"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh">
<head>
<title>智会</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<style>
	* {
		margin: 0;
		padding: 0;
	}
	
	.header {
	 	width: 1024px;
	 	height: 525px;
	   	background: url(img/simple/header.jpg);
	   	margin: 0 auto;
	   	position: relative;
	 }
	 
	 .android {
	 	display: block;
	 	width: 234px;
	 	height: 68px;
	 	background: url(img/simple/android_download.png);
	 	position: absolute;
	 	top: 300px;
	 	left: 300px;
	 }
	 
	 .iphone {
	 	display: block;
	 	width: 234px;
	 	height: 68px;
	 	background: url(img/simple/iphone_download.png);
	 	position: absolute;
	 	top: 300px;
	 	left: 50px;	 	
	 }	 
	 
	 .headinfo {
	 	position: absolute;
	 	top: 420px; 	
	 }
	 
	 .info {
	 	margin: 0 60px;
	 }
	 
	 .info h1 {
	 	font-size: 16px;
	 	margin-bottom: 10px;
	 }	 
	 
	 .content {
	 	width: 1024px;
	 	height: 600px;
	 	background: url(img/simple/body_line.jpg);
	 	margin: 0 auto;
	 }
	 
	 .arrow {
	 	width: 16px;
	 	height: 16px;
	 	background: url(img/simple/right_arrow.png);
	 	display: inline-block;
	 }
	 
	 .content ul {
	 	list-style-type: none;
	 	margin: 0 60px;
	 }
	 
	 .content ul li {
	 	display: block;
	 	width: 204px;
	 	height: 535px;
	 	background: url(img/simple/gray_bg.png);
	 	float: left;
	 	margin: 0 10px;
	 }
	 
	 .snapshot {
	 	width: 204px;
	 	height: 369px;
	 }
	 
	 #divSnapShot1 {
	 	background: url(img/simple/snapshot_01.png);
	 }
	 
	 #divSnapShot2 {
	 	background: url(img/simple/snapshot_02.png);
	 }
	 
	 #divSnapShot3 {
	 	background: url(img/simple/snapshot_03.png);
	 }	
	 
	 #divSnapShot4 {
	 	background: url(img/simple/snapshot_04.png);
	 }
	 
	 .button {
	 	width: 160px;
	 	height: 37px;
	 	background: url(img/simple/yellow_bt.png);
	 	margin: 0 22px;
	 }
	 
	 .button span {
		display: block;
		padding-top: 9px;
		text-align: center;
		font-weight: bold;
		color: white;
	 }
	 
	 .description p {
	 	padding: 10px;
	 }
	 
	 .footer {
	 	width: 1024px;
	 	height: 100px;
	 	background: #914511;
	 	margin: 0 auto;
	 }
	 
	 .footer p {
	 	color: #999;
	 	padding: 10px;
	 }
</style>
</head>

<body>
<div class="header">
	<a class="iphone" href="#">
	</a>
	<a class="android" href="http://www.wetalking.net/imeetings/downloadapp/2/android">
	</a>
	<div class="headinfo info">
		<h1>智会说明<i class="arrow"></i></h1>
		<p>智会是基于Android和iPhone的会议办公应用。通过独创技术，可以使用手机方便发起多方通话。智会应用旨在为您带来
		随时随地安排开启电话会议的体验。智会支持绑定手机号码或者邮箱，可以在多个设备上同步，查看和安排会议。无论您身在
		何方，智会为您打造手机上的移动会议室。
		</p>
	</div>	
</div>
<div class="content">
	<div class="info">
		<h1>操作使用<i class="arrow"></i></h1>
	</div>
	<ul>
		<li>
			<div class="snapshot" id="divSnapShot1"></div>
			<div class="button"><span>第一步</span></div>
			<div class="description">
				<p>在通讯录中选择联系人，选中联系人会出现在屏幕右侧的列表中，选好联系人以后点击右侧列表下方的【邀请】按钮。</p>
			</div>
		</li>
		<li>
			<div class="snapshot" id="divSnapShot2"></div>
			<div class="button"><span>第二步</span></div>
			<div class="description">
				<p>在弹出的时间设置界面上选择开会时间，选好后点击【确定】按钮。</p>
			</div>		
		</li>
		<li>
			<div class="snapshot" id="divSnapShot3"></div>
			<div class="button"><span>第三步</span></div>
			<div class="description">
				<p>智会软件会自动调用手机发送短信界面，向所有参会者发送短信通知会议时间。</p>
			</div>			
		</li>
		<li>
			<div class="snapshot" id="divSnapShot4"></div>
			<div class="button"><span>第四步</span></div>
			<div class="description">
				<p>操作完成以后，会在会议列表界面中看到自己安排好的会议。</p>
			</div>			
		</li>
	</ul>
</div>
<div class="footer">
	<p>
		<span>© 合肥优云信息技术有限公司</span>
		<span>皖ICP备12016494号</span>
	</p>
</div>
</body>
</html>
