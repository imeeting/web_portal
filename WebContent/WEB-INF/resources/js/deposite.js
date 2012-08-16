/* JS for deposite page */

$(function() {
	$("#pay_submit_bt").click(function() {
		var accountName = $("#account_name_input").val();
		if (accountName == null || accountName == "") {
			alert("请输入您的充值账户名哟！");
			return false;
		}

		$.post("user/checkUserExist", {
			username : accountName
		}, function(data) {
			var isExist = data.result;
			if (isExist) {
				// submit charge
				var type = $("#pay_type li.active").attr("type");
				switch (type) {
				case "alipay":
					$("#pay_form").attr("action", "alipay");
					$("#pay_form").submit();
					break;
				case "netbank":
					break;
				case "card":
					break;
				default:
					break;
				}
			} else {
				alert("呀，这个账号不存在耶！");
			}
		}, "json")
		.error(function() {
			alert("啊哦，貌似现在有些故障，请稍后再试哦！");
		});
		return false;
	});

});