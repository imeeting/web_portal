<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@page import="com.imeeting.mvc.controller.IMeetingWebController"%>
<%
	Integer confCount = (Integer)request.getAttribute("confCount");
%>   
<!DOCTYPE html>
<html lang="zh">
  <head>
    <title>智会-我的会议</title>
	<jsp:include page="common/_head.jsp"></jsp:include>
  </head>
  <body>
    <jsp:include page="common/afterlogin_navibar.jsp"></jsp:include>

    <div class="container">
    	<div class="row-fluid im-container">
    		<div class="span8 offset2">
		    	<div class="clearfix">
		    		<div class="pull-left">
		    			<% if (null == confCount || confCount<=0) { %>
		    			<h2>您还没有安排过任何会议</h2>
		    			<% } else { %>
		    			<h2>我有<%=confCount %>次会议</h2>
		    			<% } %>
		    		</div>
		    		<div class="pull-right">
			    		<a class="btn btn-success btn-large im-btn" href="./webconf/arrange">创建新的会议</a>
			    		<a href="#dlg_join_conf" class="btn btn-success btn-large" data-toggle="modal">进入会议</a>
		    		</div>
		    	</div>
		    	<hr>
				<div id="divConfListContainer">
					<small>正在加载数据...</small>
				</div>
    		</div>
    	</div>
		<jsp:include page="common/_footer.jsp"></jsp:include>
    </div> <!-- /container -->
    
    <div id="dlg_join_conf" class="modal hide fade">
       <form id="formJoinConference" action="./webconf" method="post">
       <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h3>进入会议</h3>
       </div>
       <div class="modal-body">
           <label for="iptConfId">请输入会议密码</label>
           <input type="text" id="iptConfId" name="confId" />
           <span id="spanInfo"></span>
       </div>
       <div class="modal-footer">
           <button type="button" class="btn" data-dismiss="modal" aria-hidden="true">取消</button>
           <button type="submit" class="btn btn-success">进入会议</button>
       </div>
       </form>
    </div>

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="/imeeting/js/lib/jquery-1.8.0.min.js"></script>
    <script src="/imeeting/js/lib/bootstrap.min.js"></script>
    <script type="text/javascript">
    	$("#divConfListContainer").load("myconference/list");
    	$("#formJoinConference").submit(function(){
    		var spanInfo = $("#spanInfo");
    		spanInfo.html("处理中，请稍后。。。");
    		var strConfId = $("#iptConfId").val();
    		$.post("/imeeting/webconf/ajax", {confId: strConfId}, 
    			function(data){
	    			var result = data.result;
	    			switch(result){
	    			case "success":
	    				spanInfo.html("加入会议成功！");
	    				window.location.href = "/imeeting/webconf/ajax?confId="+strConfId;
	    				break;
	    			case "noconference":
	    				spanInfo.html("会议不存在！");
	    				break;
	    			case "kickout":
	    				spanInfo.html("你已经被移出会议，不能加入该会议！");
	    				break;
    			}
    		}, "json");
    		
    		return false;
    	});
    </script>
    
  </body>
</html>
