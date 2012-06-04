<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    	               "http://www.w3.org/TR/html4/loose.dtd">

<html>
  <head>
    	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    	<title>GlassFish JSP Page</title>
  </head>
  <body>
    <h1>Hello World!</h1>
	<form method="post" enctype="multipart/form-data">
		<fieldset>
		    <legend>上传头像</legend>
		    <p>
		        <label for="username">User Name</label><br/>
		        <input id="username" name="username"/>
		    </p>   
		    <p>
		        <label for="avatar">选择文件</label><br/>
		        <input id="avatar" name="avatar" type="file"/>
		    </p>
		    <p>
		        <input type="submit" value="提 交"/>
		    </p>
		</fieldset>
	</form>    
  </body>
</html> 
