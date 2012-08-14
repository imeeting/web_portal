<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh">
  <head>
    <title>智会-在线充值</title>
	<jsp:include page="common/_head.jsp"></jsp:include>
  </head>

  <body>
    <div class="navbar navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          <a class="brand" href="#">智会</a>
        </div>
      </div>
    </div>

    <div class="container">
    	<div class="row">
    		<form id="pay_form" action="" class="span6 offset3">
    			<small>如需帮助请联系客服，QQ： 1622122511，电话： 0551-2379996</small>
	    		<div class="page-header">
	    			<h2>1.&nbsp;请输入要充值的账户名</h2>
	    		</div>
	    		<input type="text" />
	    		<div class="page-header">
	    			<h2>2.&nbsp;请选择你喜欢的充值方式</h2>
	    		</div>
	    		<div class="tabbable">
	    			<ul id="pay_type" class="nav nav-tabs">
	    				<li type="alipay" class="active">
	    					<a href="#pane-alipay" data-toggle="tab">支付宝充值</a>
	    				</li>
	    				<li type="netbank">
	    					<a href="#pane-bank" data-toggle="tab">网银充值</a>
	    				</li>
	    				<li type="card">
	    					<a href="#pane-zhihuicard" data-toggle="tab">智会卡充值</a>
	    				</li>
	    			</ul>
					<div class="tab-content">
						<div class="tab-pane active" id="pane-alipay">
							<label>充值金额（RMB）</label>
							<input id="alipay_input" type="text" />
						</div>				
						<div class="tab-pane" id="pane-bank">
							<label>充值金额（RMB）</label>
							<input id="netbank_input" type="text" />
						</div>
						<div class="tab-pane" id="pane-zhihuicard">
							<label>卡号</label>
							<input id="card_number" type="text" />
							<label>密码</label>
							<input id="card_pwd" type="text" />							
						</div>						
					</div>	    			
	    		</div>
	    		<hr>
	    		<button type="submit" class="btn btn-primary btn-large">确&nbsp;定</button>
    		</form>
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
