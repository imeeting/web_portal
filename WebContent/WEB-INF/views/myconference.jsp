<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@page import="com.imeeting.mvc.controller.IMeetingWebController"%>
<%
	Integer confCount = (Integer)request.getAttribute("confCount");
%>   
<!DOCTYPE html>
<html lang="zh">
  <head>
    <title>智会-我的群聊</title>
	<jsp:include page="common/_head.jsp"></jsp:include>
  </head>
  <body>
    <jsp:include page="common/afterlogin_navibar.jsp"></jsp:include>

    <div class="container">
    	<div class="row">
    		<div class="span8 offset2 page-header">
    			<% if (null == confCount || confCount<=0) { %>
    			<h2>您还没有参加过任何群聊</h2>
    			<% } else { %>
    			<h2>我参加过<%=confCount %>个群聊</h2>
    			<% } %>
    		</div>
    	</div>
		<div class="row">
			<div id="divConfListContainer" class="span8 offset2">
				<small>正在加载数据...</small>
			</div>
		</div>    	
		<jsp:include page="common/_footer.jsp"></jsp:include>
    </div> <!-- /container -->

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="/imeeting/js/lib/jquery-1.8.0.min.js"></script>
    <script src="/imeeting/js/lib/bootstrap.js"></script>
    <script type="text/javascript">
    	$("#divConfListContainer").load("myconference/list");
    </script>
    
  </body>
</html>
