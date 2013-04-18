
$(function() {
	
	var _confId = $("#iptConfId").val();
	var _userId = $("#iptUserId").val();
	var $_divAttendeeList = $("#divAttendeeList");
	
	var SocketIOClient = {
			socket : null,
			serverAddr : 'http://msg.wetalking.net',
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
			if (notice && notice.cmd=="notify" && notice.notice_list) {
				var noticeArray = notice.notice_list;
				for ( var i = 0; i < noticeArray.length; i++) {
					var event = noticeArray[i];
					if ("update_status" == event.action){
						onUpdateStatus(event);
					} else if ("update_attendee_list" == event.action){
						onUpdateAttendeeList(event);
					} else if ("kickout" == event.action) {
						onUpdateAttendeeList(event);
						onKickout(event);
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
	
	function onUpdateAttendeeList(event){
		$_divAttendeeList.load("/imeeting/webconf/attendeeList", 
				{conferenceId: _confId}, 
				function() {
					//
				});
	}
	
	function onKickout(event){
		if (_userId == event.username){
			alert("您已被主持人移出群聊！");
			window.location = "myconference";
		}
	}
	
	function onUpdateStatus(event){
		var attendee = event.attendee;
		var attendeeId = attendee.phone;
		var $div = $("#div" + attendeeId);
		
		var $phoneIcon = $div.find(".im-phone-icon");
		$phoneIcon.removeClass("im-icon-phone-Terminated im-icon-phone-Failed"
				+ " im-icon-phone-CallWait im-icon-phone-Established");
		$phoneIcon.addClass("im-icon-phone-" + attendee.telephone_status);
	};
	
	SocketIOClient.setup(_confId, _userId, onNotify);
});