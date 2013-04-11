<%@page import="com.richitec.vos.client.VOSHttpResponse"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<% 
	String accountName = request.getParameter("account_name");
	String pin = request.getParameter("pin");
	VOSHttpResponse vosResp = (VOSHttpResponse)request.getAttribute("vosResponse");
%>

<!DOCTYPE html>
<html lang="zh">
  <head>
    <title>智会-智会卡充值结果</title>
	<jsp:include page="../common/_head.jsp"></jsp:include>
  </head>

  <body>
    <jsp:include page="../common/beforelogin_navibar.jsp"></jsp:include>

    <div class="container">
    	<div class="row-fluid im-container">
	    	<div class="hero-unit">
	    		<% if (vosResp.getHttpStatusCode() != 200) { %>
	    		<h1>操作失败，请原谅。</h1>
	    		<hr>
	    		<div class="alert alert-error">
					<h2>充值账户：<%=accountName %></h2>
					<h2>充值卡号：<%=pin %></h2>
	    			<h2>内部状态：<%=vosResp.getHttpStatusCode() %></h2>
	    		</div>
				<div class="alert alert-info">
	    			<h2>如需帮助，请联系客服。电话：0551-2379997&nbsp;&nbsp;QQ：1622122511</h2>
					<hr>
					<h2><a href="deposite">点击这里返回充值页面</a></h2>	    			
	    		</div>
	    		<% } else if (vosResp.isOperationSuccess()) {%>
				<h1>恭喜你，充值成功！</h1>
				<hr>
				<div class="alert alert-success">
					<h2>充值账户：<%=accountName %></h2>
					<h2>充值卡号：<%=pin %></h2>
					<hr>
					<h2><a href="deposite">点击这里返回充值页面</a></h2>					
				</div>
	    		<% } else { %>
	    		<h1>操作失败，请原谅。</h1>
	    		<hr>
	    		<div class="alert alert-error">
					<h2>充值账户：<%=accountName %></h2>
					<h2>充值卡号：<%=pin %></h2>	    		
	    			<h2>内部状态：<%=vosResp.getHttpStatusCode() %></h2>
	    			<h2>操作结果：<%=vosResp.getVOSStatusCode() %></h2>
	    			<h2>错误信息：<%=vosResp.getVOSResponseInfo() %></h2>
	    		</div>
				<div class="alert alert-info">
	    			<h2>如需帮助，请联系客服。电话：0551-2379997&nbsp;&nbsp;QQ：1622122511</h2>
					<hr>
					<h2><a href="deposite">点击这里返回充值页面</a></h2>	    			
	    		</div>
	    		<% } %>
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
