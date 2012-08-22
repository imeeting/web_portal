<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<% 
	String confId = request.getParameter("confId"); 
	String attendeeName = request.getParameter("attendeeName");
%>        
<!DOCTYPE html>
<html lang="zh">
  <head>
    <title>智会-加入群聊</title>
	<jsp:include page="../common/_head.jsp"></jsp:include>
  </head>

  <body>
    <jsp:include page="../common/afterlogin_navibar.jsp"></jsp:include>

    <div class="container">
    	<div class="row">
			<form id="formJoinConference" action="./webconf" class="span4 offset4 well" method="post">
				<label>请输入群聊号</label>
				<input id="iptConfId" name="confId" class="span4" type="text" 
					value="<%=(null==confId ? "" : confId) %>"/>
				<% if (null != confId) { %>
				<div class="alert alert-error">
					<p>该群聊不存在，请仔细检查你输入的群聊号是否正确</p>
				</div>
				<% } %>
				<label>请输入您的姓名</label>
				<input id="iptAttendeeName" name="attendeeName" class="span4" type="text" 
					value="<%=(null==attendeeName ? "" : attendeeName) %>" />
				<button type="submit" class="btn btn-primary">加入群聊</button>
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
