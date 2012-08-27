<%@page import="com.imeeting.web.user.UserBean"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh">
  <head>
    <title>智会-在线充值</title>
	<jsp:include page="common/_head.jsp"></jsp:include>
  </head>

  <body>
    <jsp:include page="common/beforelogin_navibar.jsp"></jsp:include>
	
	<%
		UserBean userBean = (UserBean) session.getAttribute(UserBean.SESSION_BEAN);
	%>

    <div class="container">
    	<div class="row">
    		<div class="page-header span6 offset3">
    			<h2>请选择你喜欢的充值方式</h2>
    			<small>如需帮助请联系客服，QQ： 1622122511，电话： 0551-2379996</small>
    		</div>
    		<div class="tabbable span6 offset3">
    			<ul id="pay_type" class="nav nav-tabs">
    				<li type="alipay" class="active">
    					<a href="#pane-pay" data-toggle="tab">支付宝充值</a>
    				</li>
    				<!--  
    				<li type="netbank">
    					<a href="#pane-pay" data-toggle="tab">网银充值</a>
    				</li>
    				-->
    				<li type="card">
    					<a href="#pane-zhihuicard" data-toggle="tab">智会卡充值</a>
    				</li>
    			</ul>
				<div class="tab-content">
					<div class="tab-pane active" id="pane-pay">
			    		<form id="formAlipay" action="alipay" method="post">
				    		<label>请输入要充值的账户名</label>
				    		<input id="account_name_input" type="text" name="account_name" value="<%=userBean != null ? userBean.getName() : "" %>" />
							<label>请选择充值金额（RMB&nbsp;单位：元）</label>
							<select name="charge_amount">
								<option value="50.00">50</option>
								<option value="100.00" selected="selected">100</option>
								<option value="200.00">200</option>
								<option value="300.00">300</option>
								<option value="400.00">400</option>
								<option value="500.00">500</option>
								<option value="600.00">600</option>
								<option value="700.00">700</option>
								<option value="800.00">800</option>
								<option value="900.00">900</option>
								<option value="1000.00">1000</option>
								<option value="2000.00">2000</option>
								<option value="3000.00">3000</option>
							</select>
							<hr>
							<button id="btnGoToAlipay" type="submit" class="btn btn-warning">去支付宝充值</button>
			    		</form>
					</div>		
					<div class="tab-pane" id="pane-zhihuicard">
						<form id="formCard" action="zhihuicard", method="post">
							<label>请输入要充值的账户名</label>
				    		<input id="iptCardAccounName" type="text" name="account_name" value="<%=userBean != null ? userBean.getName() : "" %>" />						
							<label>请输入智会卡号</label>
							<input id="iptCardPin" type="text" name="pin" />
							<label>请输入智会卡密码</label>
							<input id="iptCardPassword" type="text" name="password" />
							<hr>
							<button id="btnCardSubmit" type="submit" class="btn btn-success">确&nbsp;定</button>						
						</form>
					</div>						
				</div>    			
    		</div>
    	</div>
 	
		<jsp:include page="common/_footer.jsp"></jsp:include>
    </div> <!-- /container -->

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="/imeeting/js/lib/jquery-1.8.0.min.js"></script>
    <script src="/imeeting/js/lib/bootstrap.min.js"></script>
    <script src="/imeeting/js/applib/common.js"></script>
  </body>
</html>
