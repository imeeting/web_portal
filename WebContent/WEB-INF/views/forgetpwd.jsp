<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh">
  <head>
    <title>智会-忘记密码</title>
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
    		<form action="" class="span6 offset3">
	    		<div class="page-header">
	    			<h2>1.&nbsp;请输入手机号码</h2>
	    		</div>
	    		<input type="text" />
	    		<button class="btn" >获取手机验证码</button>
	    		<div class="page-header">
	    			<h2>2.&nbsp;请输入手机验证码</h2>
	    		</div>
	    		<input type="text" />
	    		<div class="page-header">
	    			<h2>3.&nbsp;设置新密码</h2>
	    		</div>
	    		<label>请输入新密码</label>
	    		<input type="text" />
	    		<label>请再次输入新密码</label>
	    		<input type="text" />
	    		<hr>
	    		<button type="submit" class="btn btn-primary btn-large">确&nbsp;定</button>
    		</form>
    	</div>
 	
		<jsp:include page="common/_footer.jsp"></jsp:include>
    </div> <!-- /container -->

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="http://code.jquery.com/jquery-1.7.2.js"></script>
    <script src="js/lib/bootstrap.js"></script>

  </body>
</html>
