package com.imeeting.mvc.model.conference.attendee;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.imeeting.constants.AttendeeConstants;

public class AttendeeModel {
	private static Log log = LogFactory.getLog(AttendeeModel.class);

	public enum PhoneCallStatus {
		CallWait, Established, TermWait, Failed, Terminated
	}

	private String phone;
	private String nickname;
	private PhoneCallStatus phoneCallStatus;

	public AttendeeModel(String phone) {
		this.phone = phone;
		this.nickname = "";
		this.phoneCallStatus = PhoneCallStatus.Terminated;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String name) {
		this.phone = name;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getDisplayName() {
		if (nickname != null && nickname.length() > 0) {
			return nickname;
		} else {
			return phone;
		}
	}

	public PhoneCallStatus getPhoneCallStatus() {
		return phoneCallStatus;
	}

	public boolean statusCall() {
		log.info("statusCall");
		synchronized (phoneCallStatus) {
			if (PhoneCallStatus.Terminated.equals(phoneCallStatus)
					|| PhoneCallStatus.Failed.equals(phoneCallStatus)) {
				phoneCallStatus = PhoneCallStatus.CallWait;
				log.info("set " + phone + " status as "
						+ phoneCallStatus.name());
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean statusHangup() {
		log.info("statusHangup");
		synchronized (phoneCallStatus) {
			if (PhoneCallStatus.CallWait.equals(phoneCallStatus)
					|| PhoneCallStatus.Established.equals(phoneCallStatus)) {
				phoneCallStatus = PhoneCallStatus.TermWait;
				log.info("set " + phone + " status as "
						+ phoneCallStatus.name());
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean statusCallEstablished() {
		log.info("statusCallEstablished");
		synchronized (phoneCallStatus) {
			if (PhoneCallStatus.CallWait.equals(phoneCallStatus)
					|| PhoneCallStatus.Terminated.equals(phoneCallStatus)) {
				phoneCallStatus = PhoneCallStatus.Established;
				log.info("set " + phone + " status as "
						+ phoneCallStatus.name());
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean statusCallFailed() {
		log.info("statusCallFailed");
		synchronized (phoneCallStatus) {
			if (PhoneCallStatus.CallWait.equals(phoneCallStatus)) {
				phoneCallStatus = PhoneCallStatus.Failed;
				log.info("set " + phone + " status as "
						+ phoneCallStatus.name());
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean statusCallTerminated() {
		log.info("statusCallTerminated");
		synchronized (phoneCallStatus) {
			if (PhoneCallStatus.CallWait.equals(phoneCallStatus)
					|| PhoneCallStatus.TermWait.equals(phoneCallStatus)
					|| PhoneCallStatus.Established.equals(phoneCallStatus)
					|| PhoneCallStatus.Failed.equals(phoneCallStatus)) {
				phoneCallStatus = PhoneCallStatus.Terminated;
				log.info("set " + phone + " status as "
						+ phoneCallStatus.name());
				return true;
			} else {
				return false;
			}
		}
	}

	public JSONObject toJson() {
		JSONObject obj = new JSONObject();
		try {
			obj.put(AttendeeConstants.nickname.name(), nickname);
			obj.put(AttendeeConstants.phone.name(), phone);
			obj.put(AttendeeConstants.telephone_status.name(),
					phoneCallStatus.name());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}

}
