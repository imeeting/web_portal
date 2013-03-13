
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
		updateAttendeeStatus(event.attendee);
	};
	
	function updateAttendeeStatus(attendee){
		var attendeeId = attendee.username;
		var $div = $("#div" + attendeeId);
		
		var $phoneIcon = $div.find(".im-phone-icon");
		$phoneIcon.removeClass("im-icon-phone-Terminated im-icon-phone-Failed"
				+ " im-icon-phone-CallWait im-icon-phone-Established");
		$phoneIcon.addClass("im-icon-phone-" + attendee.telephone_status);
		
		var $phoneText = $div.find(".im-phone-text");
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
	
	function bindClickToBtnAttendeePhoneCall(){
		$(".divAttendeePhone").each(function(){
			var $this = $(this);
			var $iptStatus = $this.find(".iptAttendeePhoneCallStatus");
			var attendeeId = $this.find(".iptAttendeePhoneNumber").val();
			var $btnPhoneCall = $this.find(".btnAttendeePhoneCall");
			$btnPhoneCall.click(function(){
				var phoneStatus = $iptStatus.val();
				if ("Terminated" == phoneStatus ||
						"Failed" == phoneStatus){
					$.post("/imeeting/webconf/call", 
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
						$.post("/imeeting/webconf/hangup", 
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
	}
	
	SocketIOClient.setup(_confId, _userId, onNotify);
	
	function heartbeat(){
		$.post("/imeeting/webconf/heartbeat", 
				{conferenceId: _confId},
				function(data){
					
				});
	}
	
	setInterval(heartbeat, 10000);
});