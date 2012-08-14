<%@page import="com.richitec.util.Pager"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.imeeting.constants.WebConstants"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh">
  <head>
    <title>智会-充值账户</title>
	<jsp:include page="common/_head.jsp"></jsp:include>
  </head>

  <body>
    <jsp:include page="common/afterlogin_navibar.jsp"></jsp:include>
	
	<%
		Double balance = (Double) request.getAttribute(WebConstants.balance.name());
		String strBalance = String.format("%,.2f", balance.doubleValue());
		
		List<Map<String, Object>> chargeList = (List<Map<String, Object>>) request.getAttribute(WebConstants.charge_list.name());
		Pager pager = (Pager) request.getAttribute(WebConstants.pager.name());
	%>


    <div class="container">
    	<div class="row well">
			<div class="span4 offset3">
				<h1>账户余额：￥<%=strBalance %></h1>
			</div>
			<a class="span1 btn btn-large btn-success" href="deposite">在线充值</a>
    	</div>
		<div class="row">
			<div class="page-header span6 offset3">
				<h2>我的充值记录</h2>
			</div>
		</div>
		<div class="row">
			<div class="span6 offset3">
				<table class="table table-striped">
					<thead>
						<tr>
						<th>充值日期</th>
						<th>充值金额</th>
						</tr>
					</thead>
					<tbody>
					<% 
					if (chargeList != null) {
						for(Map<String, Object> map : chargeList) {
							String time = String.valueOf(map.get("charge_time"));
							Integer money = (Integer) map.get("money");
						%>
							<tr>
							<td><%=time %></td>
							<td>￥<%=String.format("%,.2f", money.floatValue()) %></td>
							</tr>
						<% } 
					}%>
					</tbody>
				</table>
				<ul class="pager">
					<li class="previous"><a href=#>上一页</a></li>
					<li class="next"><a href=#>下一页</a></li>
				</ul>
			</div>
		</div>
    	
		<jsp:include page="common/_footer.jsp"></jsp:include>
    </div> <!-- /container -->

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.0/jquery.min.js"></script>
    <script src="js/lib/bootstrap.js"></script>

  </body>
</html>
