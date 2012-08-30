
$(function() {
	
	var _confId = $("#iptConfId").val();
	var _userId = $("#iptUserId").val();
	var $_btnPhoneCall = $("#btnPhoneCall");
	
	var SocketIOClient = {
			socket : null,
			serverAddr : 'http://msg.walkwork.net',
			setup : function(topic, subscriberID, callback) {
				SocketIOClient.socket = io.connect(SocketIOClient.serverAddr, {
					port : 80
				});

				SocketIOClient.socket.on('connect', function() {
					SocketIOClient.socket.emit('subscribe', {
						'topic' : topic,
						'subscriber_id' : subscriberID
					});
				});

				SocketIOClient.socket.on('notice', function(notice) {
					if (callback && typeof callback === 'function') {
						callback(200, notice);
					}
				});

				SocketIOClient.socket.on('error', function(event) {
					//TODO:
				});
			},

			/**
			 * get all notices from server
			 * @param topic
			 */
			getall: function(topic) {
				if (SocketIOClient.socket) {
					SocketIOClient.socket.emit('getall', {
						'topic': topic
					});
				}
			},
			
			disconnect: function() {
				if (SocketIOClient.socket) {
					SocketIOClient.socket.disconnect();
				}
			}
		};
	
	function onNotify(code, notice){
		switch (code) {
		case 200:
			if (notice && notice.notice_list) {
				var noticeArray = notice.notice_list;
				for ( var i = 0; i < noticeArray.length; i++) {
					var event = noticeArray[i];
					if ("update_status" == event.action){
						onUpdateStatus(event);
					} else if ("update_list" == event.action){
						
					} else if ("kickout" == event.action) {
						
					} else {
						//error action
					}
				}
			}
			break;
		default:
			break;
		}
	};
	
	function onUpdateStatus(event){
		var attendeeId = event.attendee.username;
		if (_userId == attendeeId){
			updateSelfStatus(event.attendee);
		} else {
			updateAttendeeStatus(event.attendee);
		}
	};
	
	function updateSelfStatus(attendee){
		$("#iptMyPhoneCallStatus").val(attendee.telephone_status);
		switch(attendee.telephone_status){
		case "CallWait":
			$_btnPhoneCall.html("取消呼叫");
			break;
		case "Terminated":
			$_btnPhoneCall.html("Call Me");
			break;
		case "Failed":
			$_btnPhoneCall.html("Call Me");
			break;
		case "Established":
			$_btnPhoneCall.html("挂 断");
			break;
		default:
			break;
		}
	};
	
	function updateAttendeeStatus(attendee){
		var attendeeId = attendee.username;
		var $div = $("#div" + attendeeId);
		
		var $signinIcon = $div.find(".im-signin-icon");
		$signinIcon.removeClass("im-icon-signin-offline im-icon-signin-online");
		$signinIcon.addClass("im-icon-signin-" + attendee.online_status);
		
		var $phoneIcon = $div.find(".im-phone-icon");
		$phoneIcon.removeClass("im-icon-phone-Terminated im-icon-phone-Failed"
				+ " im-icon-phone-CallWait im-icon-phone-Established");
		$phoneIcon.addClass("im-icon-phone-" + attendee.telephone_status);
		
		var $phoneText = $div.find(".im-phone-text");
		$phoneText.html(" " + getPhoneStatusText(attendee.telephone_status));
		
		//moderator UI
		var $phoneCallStatus = $div.find(".iptAttendeePhoneCallStatus");
		if ($phoneCallStatus){
			$phoneCallStatus.val(attendee.telephone_status);
		}
		
		var $btnPhoneCall = $div.find(".btnAttendeePhoneCall");
		if ($btnPhoneCall){
			$btnPhoneCall.html(getPhoneCallButtonText(attendee.telephone_status));
		}
	};
	
	function getPhoneStatusText(status){
		if (status == "CallWait"){
			return "正在呼叫";
		} else if (status == "Terminated"){
			return "未接通";
		} else if (status == "Failed") {
			return "呼叫失败";
		} else if (status == "Established"){
			return "已接通";
		} else {
			return status;
		}
	}
	
	function getPhoneCallButtonText(status){
		if (status == "CallWait"){
			return "取消呼叫";
		} else if (status == "Terminated"){
			return "呼叫";
		} else if (status == "Failed") {
			return "重新呼叫";
		} else if (status == "Established"){
			return "挂断";
		} else {
			return status;
		}
	}
	
	$_btnPhoneCall.click(function(){
		var currentPhoneCallStatus = $("#iptMyPhoneCallStatus").val();
		if ("Terminated" == currentPhoneCallStatus ||
			"Failed" == currentPhoneCallStatus){
			$.post("webconf/call", 
					{
						conferenceId: _confId,
						dstUserName: _userId
					}, 
					function(){
						$("#iptMyPhoneCallStatus").val("CallWait");
					});
		} else 
		if ("CallWait" == currentPhoneCallStatus ||
			"Established" == currentPhoneCallStatus){
			$.post("webconf/hangup", 
					{
						conferenceId: _confId,
						dstUserName: _userId
					}, 
					function(){
						$("#iptMyPhoneCallStatus").val("TermWait");
					});
		} else {
			//do nothing
		}
	});
	
	$(".divAttendeePhone").each(function(){
		var $this = $(this);
		var $iptStatus = $this.find(".iptAttendeePhoneCallStatus");
		var attendeeId = $this.find(".iptAttendeePhoneNumber").val();
		var $btnPhoneCall = $this.find(".btnAttendeePhoneCall");
		$btnPhoneCall.click(function(){
			var phoneStatus = $iptStatus.val();
			if ("Terminated" == phoneStatus ||
					"Failed" == phoneStatus){
					$.post("webconf/call", 
							{
								conferenceId: _confId,
								dstUserName: attendeeId
							}, 
							function(){
								$iptStatus.val("CallWait");
							});
				} else 
				if ("CallWait" == phoneStatus ||
					"Established" == phoneStatus){
					$.post("webconf/hangup", 
							{
								conferenceId: _confId,
								dstUserName: attendeeId
							}, 
							function(){
								$iptStatus.val("TermWait");
							});
				} else {
					//do nothing
				}
		});
	});
	
	SocketIOClient.setup(_confId, _userId, onNotify);
});