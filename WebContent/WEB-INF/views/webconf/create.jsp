<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<% 
	String confId = request.getParameter("confId"); 
	String attendeeName = request.getParameter("attendeeName");
%>        
<!DOCTYPE html>
<html lang="zh">
  <head>
    <title>智会-创建群聊</title>
	<jsp:include page="../common/_head.jsp"></jsp:include>
  </head>

  <body>
    <jsp:include page="../common/afterlogin_navibar.jsp"></jsp:include>

    <div class="container">
    	<div class="row">
			<form id="formJoinConference" action="./webconf" class="span4 offset4 well" method="post">
				<label>给你的群聊起个名字吧</label>
				<input id="iptConfTitle" name="confTitle" class="span4" type="text" 
					placeholder="请叫我 红领巾"/>
				<button type="submit" class="btn btn-success btn-large">开始群聊</button>
				<button class="btn btn-warning btn-large">取&nbsp;&nbsp;消</button>
			</form>
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
