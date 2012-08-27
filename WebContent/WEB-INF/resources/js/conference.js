
$(function() {
	
	var _confId = $("#iptConfId").val();
	var _userId = $("#iptUserId").val();
	
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
		
	};
	
	function updateAttendeeStatus(attendee){
		var attendeeId = attendee.username;
		$div = $("#div" + attendeeId);
		
		$signinIcon = $div.find(".im-signin-icon");
		$signinIcon.removeClass("im-icon-signin-offline im-icon-signin-online");
		$signinIcon.addClass("im-icon-signin-" + attendee.online_status);
		
		$phoneIcon = $div.find(".im-phone-icon");
		$phoneIcon.removeClass("im-icon-phone-Terminated im-icon-phone-Failed"
				+ " im-icon-phone-CallWait im-icon-phone-Established");
		$phoneIcon.addClass("im-icon-phone-" + attendee.telephone_status);
		
		$phoneText = $div.find(".im-phone-text");
		$phoneText.html(" " + getPhoneStatusText(attendee.telephone_status));
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
	
	SocketIOClient.setup(_confId, _userId, onNotify);
});