$(function() {
	$.ajaxSetup({
		cache: false,
		statusCode: {
			
		}
	});
});

var Util = {
	log : function(text) {
		try {
			console.log(text);
		} catch(err) {
			
		}
	},
	
	isValidPhoneNumber : function(number) {
		var reg = /(^[0]\d{2,3}\-\d{7,8})|(^[1-9]\d{6,7})|(^[0]\d{10,11})|(^[1][\d]{10})/;
		return reg.exec(number);
	},
	
	isValidEmail: function(email) {
		var regex = /^([a-zA-Z0-9_\.\-\+])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		return regex.test(email);
	}

};