<%@page import="com.imeeting.mvc.controller.IMeetingWebController"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
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
    			<h2>我参加过的群聊</h2>
    		</div>
    	</div>
		<div class="row">
			<div class="span8 offset2">
				<small>正在加载数据...</small>
			</div>
		</div>    	
		<jsp:include page="common/_footer.jsp"></jsp:include>
    </div> <!-- /container -->

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="js/lib/jquery-1.8.0.min.js"></script>
    <script src="js/lib/bootstrap.js"></script>
    <script type="text/javascript">
    
    </script>
    
  </body>
</html>
