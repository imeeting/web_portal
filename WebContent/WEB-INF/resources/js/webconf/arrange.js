$(function() {

	$("#addressbook").delegate(".add_contact_bt", "click", function() {
		var $number_li = $(this).parent();
		var phoneNumber = $number_li.find(".phone_number").html();

		var $name_text = $number_li.parent().prev(".name");
		var name = $name_text.html();

		ContactSelectionManager.addContactToSelectedList(name, phoneNumber);
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
		if (!Util.isValidPhoneNumber(number)) {
			alert("请输入合法的手机号！");
			return false;
		}
		
		$("#add_new_contact_dlg").modal("hide");
		$("#newContactName").val("");
		$("#newContactPhoneNumber").val("");
		ContactSelectionManager.addContactToSelectedList(name, number);
		
		return false;
	});
	
	$("#add_cancel_bt").click(function() {
		$("#add_new_contact_dlg").modal("hide");
		$("#newContactName").val("");
		$("#newContactPhoneNumber").val("");
		
		return false;
	});
	
	$("#create_conf_bt").click(function() {
		var confTitle = $("#iptConfTitle").val();
		var $selectedContactsLis = $("#selected_contacts li");
		var contacts = Array();
		if ($selectedContactsLis && $selectedContactsLis.length > 0) {
			for (var i = 0; i < $selectedContactsLis.length; i++) {
				var li = $selectedContactsLis[i];
				var phoneNumber = $(li).find(".phone_number").html();
				contacts[i] = phoneNumber;
			}
		}
		var attendeesString = JSON.stringify(contacts);
		Util.log("contacts: " + attendeesString);
		$.ajax({
			type : "post",
			url : "/imeeting/webconf/create",
			dataType : "json",
			data : {
				title : confTitle,
				attendees : attendeesString
			},
			statusCode : {
				201 : function(result) {
					window.location = "/imeeting/webconf/enterConf";
				}
			},
			error : function(jqXHR) {
				alert("额。。会议创建失败！请重试～");
			}
			

		});
		
		
		return false;
	});
});

var ContactSelectionManager = {
	$selectedContactUI : $("#selected_contacts"),
	searchTaskContinue : false,
	prevSearchWord : "",
	currentSearchWord : "",

	addContactToSelectedList : function(name, number) {
		if (ContactSelectionManager.isContactSelected(number)) {
			alert("联系人已经添加啦！");
			return;
		}

		var $contactLiUI = $("#template .selected_contact").clone();
		$nameUI = $contactLiUI.find(".name");
		$nameUI.html(name);

		$numberUI = $contactLiUI.find(".phone_number");
		$numberUI.html(number);

		ContactSelectionManager.$selectedContactUI.append($contactLiUI);
	},

	isContactSelected : function(number) {
		var $contactLis = $("#selected_contacts li");
		var selected = false;
		for ( var i = 0; i < $contactLis.length; i++) {
			var li = $contactLis[i];
			var phoneNumber = $(li).find(".phone_number").html();
			if (number == phoneNumber) {
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
		if (name == null || name == "") {
			// get all contacts
			$.ajax({
				type : "post",
				url : "/imeeting/addressbook/allContacts",
				dataType : "json",
				data : {
				},
				success : function(data, textStatus, jqXHR) {
					ContactSelectionManager.clearAddressBookUI();
					if (data) {
						for ( var i = 0; i < data.length; i++) {
							var contact = data[i];
							Util.log("contact: " + contact.display_name);
							if (contact.phone_array && contact.phone_array.length > 0) {
								ContactSelectionManager.addContactToAddressBook(contact.display_name, contact.phone_array);
							}
						}
					}
				},
				error : function(jqXHR) {

				}

			});
		} else {
			// do search
			$.ajax({
				type : "post",
				url : "/imeeting/addressbook/search",
				dataType : "json",
				data : {
					searchWord : name
				},
				success : function(data, textStatus, jqXHR) {
					ContactSelectionManager.clearAddressBookUI();
					if (data) {
						for ( var i = 0; i < data.length; i++) {
							var contact = data[i];
							Util.log("contact: " + contact.display_name);
							if (contact.phone_array && contact.phone_array.length > 0) {
								ContactSelectionManager.addContactToAddressBook(contact.display_name, contact.phone_array);
							}
						}
					}
				},
				error : function(jqXHR) {

				}

			});
		}
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

	addContactToAddressBook : function(name, phoneArray) {
		$contactLiUI = $("#template .ab_contact").clone();
		$contactLiUI.find(".name").html(name);
		$numberUL = $contactLiUI.find(".number_ul");
		$numberLI = $numberUL.find(".number_li");
		$numberLI.remove();

		if (phoneArray) {
			for ( var i = 0; i < phoneArray.length; i++) {
				var phoneNumber = phoneArray[i];
				var $tempNumberLi = $numberLI.clone();
				$tempNumberLi.find(".phone_number").html(phoneNumber);
				$numberUL.append($tempNumberLi);
			}
		}

		$("#addressbook").append($contactLiUI);
	}
};