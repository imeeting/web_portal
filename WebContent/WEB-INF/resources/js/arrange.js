$(function() {

	$("#addressbook").delegate(".add_contact_bt", "click", function() {
		var $contactLi = $(this).parent().parent();
		var nickName = $contactLi.find(".name").html();
		var phoneNumber = $contactLi.find(".phone").html();
		var email = $contactLi.find(".email").html();

		ContactSelectionManager.addContactToSelectedList(nickName, phoneNumber, email);
		return false;
	});

	$("#selected_contacts").delegate(".remove_contact_bt", "click", function() {
		$li = $(this).parent().parent();
		$li.remove();
		return false;
	});

	$("#ab_search_input").focus(function() {
		ContactSelectionManager.searchTaskContinue = true;
		ContactSelectionManager.startSearchTask();
	});

	$("#ab_search_input").blur(function() {
		ContactSelectionManager.stopSearchTask();
	});
	
	$("#add_confirm_bt").click(function() {
		var name = $("#newContactName").val();
		var number = $("#newContactPhoneNumber").val();
		var email = $("#newContactEmail").val();
		
		if(number == "" && email == ""){
			alert("手机号码和电子邮件不能同时为空");
			return false;
		}
		
		if (number != ""){
			if (!Util.isValidPhoneNumber(number)) {
				alert("请输入合法的手机号");
				return false;
			}
		}
		
		if (email != ""){
			if(!Util.isValidEmail(email)){
				alert("电子邮件地址格式不正确");
				return false;
			}
		}
		
		$("#add_new_contact_dlg").modal("hide");
		$("#newContactName").val("");
		$("#newContactPhoneNumber").val("");
		$("#newContactEmail").val("");
		ContactSelectionManager.addContactToSelectedList(name, number, email);
		
		return false;
	});
	
	$("#add_cancel_bt").click(function() {
		$("#add_new_contact_dlg").modal("hide");
		$("#newContactName").val("");
		$("#newContactPhoneNumber").val("");
		
		return false;
	});
	
	$("#iptScheduleTime").focus(function(){
		WdatePicker({
			highLineWeekDay: true,
			firstDayOfWeek: 1,
			dateFmt: 'yyyy-MM-dd HH:mm',
			minDate: '%y-%M-%d %H:{%m+30}:%s',
			maxDate: '%y-%M-{%d+60} %H:%m:%s',
			errDealMode: 1
		});
	});
	
	$("#divSelectTime input[name='isScheduled']").change(function(){
		var value = $(this).val();
		if(value == "now"){
			$("#divScheduleTime").hide(200);
		} else {
			$("#divScheduleTime").show(200);
		}
	});
	
	$("#create_conf_bt").click(function() {
		//get contacts
		var $selectedContactsLis = $("#selected_contacts li");
		var contacts = Array();
		if ($selectedContactsLis && $selectedContactsLis.length > 0) {
			for (var i = 0; i < $selectedContactsLis.length; i++) {
				var li = $selectedContactsLis[i];
				var $li = $(li);
				var nameVal = $li.find(".name").html();
				var phoneNumberVal = $li.find(".phone").html();
				var emailVal = $li.find(".email").html();
				contacts[i] = {nickname: nameVal, phone: phoneNumberVal,
						email: emailVal};
			}
		}
		var attendeesString = JSON.stringify(contacts);
		Util.log("contacts: " + attendeesString);

		var isScheduledVal = $("#divSelectTime input[name='isScheduled']:checked").val();
		Util.log(isScheduledVal);
		if (isScheduledVal == "now"){
			createRequest(attendeesString);
		} else {
			//get scheduled time
			var scheduleTimeVal = $("#iptScheduleTime").val();
			if (scheduleTimeVal != ""){
				scheduleRequest(attendeesString, scheduleTimeVal);
			} else {
				alert("请选择会议时间！");
			}
		}
		
		return false;
	});
	
	function createRequest(attendeesString){
		$.ajax({
			type : "post",
			url : "/imeeting/webconf/scheduleNow",
			dataType : "json",
			data : {
				attendees : attendeesString,
			},
			statusCode : {
				201 : function(result) {
					window.location = "/imeeting/webconf/ajax?confId=" + result.conferenceId;
				}
			},
			error : function(jqXHR) {
				if (jqXHR.status == 402){
					alert("账户余额不足，请充值后继续使用。");
				} else {
					alert("额。。会议创建失败！请重试～");
				}
			}
		});
	}
	
	function scheduleRequest(attendeesString, scheduleTimeVal){
		$.ajax({
			type : "post",
			url : "/imeeting/webconf/schedule",
			dataType : "json",
			data : {
				attendees : attendeesString,
				scheduleTime: scheduleTimeVal
			},
			statusCode : {
				201 : function(data) {
					showScheduleSuccessDlg(data);
				}
			},
			error : function(jqXHR) {
				if (jqXHR.status == 402){
					alert("账户余额不足，请充值后继续使用。");
				} else {
					alert("额。。预约失败！请重试～");
				}
			}
		});
	}
	
	function showScheduleSuccessDlg(data){
		var $scheduledSuccessDlg = $("#schedule_success_dlg");
		$scheduledSuccessDlg.find("#schedule_conf_id").html(data.conferenceId);
		$scheduledSuccessDlg.find("#schedule_conf_time").html(data.schedule_time);
		$scheduledSuccessDlg.modal();
	}
});

var ContactSelectionManager = {
	$selectedContactUI : $("#selected_contacts"),
	searchTaskContinue : false,
	prevSearchWord : "",
	currentSearchWord : "",

	addContactToSelectedList : function(name, number, email) {
		if (ContactSelectionManager.isContactSelected(number, email)) {
			alert("联系人已经添加啦！");
			return;
		}

		var $contactLiUI = $("#template .selected_contact").clone();
		$nameUI = $contactLiUI.find(".name");
		$nameUI.html(name);

		$numberUI = $contactLiUI.find(".phone");
		$numberUI.html(number);
		
		$emailUI = $contactLiUI.find(".email");
		$emailUI.html(email);

		ContactSelectionManager.$selectedContactUI.append($contactLiUI);
	},

	isContactSelected : function(number, email) {
		var $contactLis = $("#selected_contacts li");
		var selected = false;
		for ( var i = 0; i < $contactLis.length; i++) {
			var li = $contactLis[i];
			var $li = $(li);
			var phoneNumber = $li.find(".phone").html();
			var emailVal = $li.find(".email").html();
			if ( (number != "" && number == phoneNumber) || 
				 (email != "" && email == emailVal) ) {
				selected = true;
				break;
			}
		}
		return selected;
	},

	startSearchTask : function() {
		ContactSelectionManager.currentSearchWord = $("#ab_search_input").val();
		if (ContactSelectionManager.currentSearchWord != ContactSelectionManager.prevSearchWord) {
			ContactSelectionManager.prevSearchWord = ContactSelectionManager.currentSearchWord;
			ContactSelectionManager
					.searchAddressBook(ContactSelectionManager.currentSearchWord);
		}

		if (ContactSelectionManager.searchTaskContinue) {
			setTimeout(function() {
				ContactSelectionManager.startSearchTask();
			}, 500);
		}
	},

	searchAddressBook : function(name) {
		Util.log("search: " + name);
		$.ajax({
			type : "post",
			url : "/imeeting/contact/search",
			dataType : "json",
			data : {
				searchWord : name
			},			
			success : function(data, textStatus, jqXHR) {
				ContactSelectionManager.clearAddressBookUI();
				if (data) {
					for ( var i = 0; i < data.length; i++) {
						var contact = data[i];
						ContactSelectionManager.addContactToAddressBook(contact.nickname, contact.phone, contact.email);
					}
				}
			},
			error : function(jqXHR) {

			}
		});		

	},

	stopSearchTask : function() {
		ContactSelectionManager.searchTaskContinue = false;
	},

	clearAddressBookUI : function() {
		var $lis = $("#addressbook li");
		$lis.each(function() {
			$(this).remove();
		});
	},

	addContactToAddressBook : function(name, phone, email) {
		$contactLiUI = $("#template .ab_contact").clone();
		
		$nameUI = $contactLiUI.find(".name");
		$nameUI.html(name);

		$numberUI = $contactLiUI.find(".phone");
		$numberUI.html(phone);
		
		$emailUI = $contactLiUI.find(".email");
		$emailUI.html(email);

		$("#addressbook").append($contactLiUI);
	}
};