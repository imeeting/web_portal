
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
		alert(attendee.username + "\n"
				+ "\nOnline:" + attendee.online_status
				+ "\nTel:" + attendee.telephone_status
				+ "\nVideo:" + attendee.video_status);
	};
	
	SocketIOClient.setup(_confId, _userId, onNotify);
});