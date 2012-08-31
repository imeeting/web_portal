<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<% 
	String confId = request.getParameter("confId");
    String errorInfo = (String)request.getAttribute("errorInfo");
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
				<% if (null != errorInfo) { %>
				<div class="alert alert-error">
				    <% if ("noconference".equals(errorInfo)) { %>
					<p>该群聊不存在，请仔细检查你输入的群聊号是否正确</p>
					<% } else %>
                    <% if ("donkeyFailed".equals(errorInfo)) { %>
                    <p>加入群聊失败</p>
                    <% } else %>
                    <% if ("kickout".equals(errorInfo)) { %>
                    <p>你已被该主持人移出群聊，请联系主持人。</p>
                    <% } else { %> 
                    <p>未知错误</p>
                    <% } %>
				</div>
				<% } %>
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
