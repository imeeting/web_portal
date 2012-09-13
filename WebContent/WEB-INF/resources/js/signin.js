/* JS for sign in page */

$(function() {
	
	$("#signin-form").submit(function() {
		var username = $("#username").val();
		var pwd = $("#password").val();
		var pwdMd5 = md5(pwd);
		Util.log("md5 pwd: " + pwdMd5);
		$.ajax({
			type : "post",
			url : "user/login",
			dataType : "json",
			data : {
				loginName : username,
				loginPwd : pwdMd5
			},
			success : function(data, textStatus, jqXHR) {
				var result = data.result;
				switch (result) {
				case "0":
					// login success
					location.href = "myconference";
					break;
				default:
					alert("额，登陆失败了，检查一下您的用户名和密码吧!");
					break;
				}
			},
			error : function(jqXHR) {
				alert("⊙﹏⊙b 网络故障了～～");
			}
			
		});
		return false;
	});
});