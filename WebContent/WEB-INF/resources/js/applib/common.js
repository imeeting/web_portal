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
	}	
};