/* JS for deposite page */

$(function() {
	$("#pay_submit_bt").click(function() {
		var accountName = $("#account_name_input").val();
		if (accountName == null || accountName == "") {
			alert("请输入充值账户名");
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
				alert("充值账户名不存在，请检查！");
			}
		}, "json")
		.error(function() {
			alert("服务器通信故障，请稍后重试！");
		});
		return false;
	});

});