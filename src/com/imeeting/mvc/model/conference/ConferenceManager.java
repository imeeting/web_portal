package com.imeeting.mvc.model.conference;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.imeeting.constants.AttendeeConstants;
import com.imeeting.constants.ConferenceConstants;
import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.conference.ConferenceDB.ConferenceStatus;
import com.imeeting.mvc.model.conference.attendee.AttendeeBean;
import com.imeeting.mvc.model.conference.attendee.AttendeeModel;
import com.richitec.donkey.client.DonkeyHttpResponse;
import com.richitec.notify.Notifier;

public class ConferenceManager {

	private static Log log = LogFactory.getLog(ConferenceManager.class);

	private Map<String, ConferenceModel> conferenceMap = null;
	private ConferenceDB conferenceDao;
	private long TIME_INTERVAL = 5 * 60 * 1000;
	private long LEADING_TIME = 10 * 60 * 1000;

	public ConferenceManager() {
		conferenceMap = new ConcurrentHashMap<String, ConferenceModel>();
	}

	public void setConferenceDao(ConferenceDB dao) {
		conferenceDao = dao;
	}

	public ConferenceModel getConference(String conferenceId) {
		return conferenceMap.get(conferenceId);
	}

	public ConferenceModel creatConference(String conferenceId, String ownerName) {
		ConferenceModel conference = new ConferenceModel(conferenceId,
				ownerName);
		conferenceMap.put(conferenceId, conference);
		return conference;
	}

	public ConferenceModel removeConference(String conferenceId) {
		log.info("remove conference " + conferenceId);
		return conferenceMap.remove(conferenceId);
	}

	/**
	 * close the conference and release all resources
	 * 
	 * @param conferenceId
	 */
	public void closeConference(String conferenceId) {
		removeConference(conferenceId);
		ContextLoader.getDonkeyClient().destroyConference(conferenceId,
				conferenceId);
		conferenceDao.close(conferenceId);
	}

	public void notifyConferenceDestoryed(String conferenceId) {
		JSONObject msg = new JSONObject();
		try {
			msg.put("conferenceId", conferenceId);
			msg.put("action", ConferenceAction.conf_destoryed.name());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Notifier nf = ContextLoader.getNotifier();
		nf.notifyWithHttpPost(conferenceId, msg.toString());
	}

	/**
	 * 每隔5分钟从数据库中查询所有预约会议， 如果会议预约时间和当前系统时间相差是否在5分钟之内，
	 * 向donkey发送请求开启一个会议，并把会议状态置为OPEN。
	 */
	public void checkAllScheduledConference() {
		log.info("checkAllScheduledConference");
		try {
			List<Map<String, Object>> confs = conferenceDao
					.getAllScheduledConference();
			log.info("confs: " + confs);
			for (Map<String, Object> conf : confs) {
				log.info("conf: " + conf);
				String confId = (String) conf
						.get(ConferenceConstants.conferenceId.name());
				Long schedTime = ((Long) conf
						.get(ConferenceConstants.scheduled_time.name())) * 1000;
				String owner = (String) conf.get(ConferenceConstants.owner
						.name());

				log.info("confid: " + confId + " owner: " + owner);
				if (Math.abs(System.currentTimeMillis()
						- (schedTime - LEADING_TIME)) < TIME_INTERVAL) {
					// open the conference
					log.info("open conf for " + confId + "owner: " + owner
							+ " sched time: " + new Date(schedTime).toString());

					ConferenceModel conference = creatConference(confId, owner);

					List<String> confIdList = new ArrayList<String>();
					confIdList.add(confId);
					List<AttendeeBean> attendees = conferenceDao
							.getConferenceAttendees(confIdList);
					for (AttendeeBean att : attendees) {
						if (att.getPhone() != null
								&& !"".equals(att.getPhone())) {
							AttendeeModel attModel = new AttendeeModel(
									att.getPhone());
							attModel.setNickname(att.getNickName());
							conference.addAttendee(attModel);
						}
					}
					conference.setAudioConfId(confId);
					DonkeyHttpResponse donkeyResp = ContextLoader
							.getDonkeyClient().createNoControlConference(
									confId, "",
									conference.getAllAttendeeName(), confId);
					if (null == donkeyResp || !donkeyResp.isAccepted()) {
						log.info("Create audio conference error : "
								+ (null == donkeyResp ? "NULL Response"
										: donkeyResp.getStatusCode()));
						removeConference(confId);
					} else {
						log.info("conf created! set status open");
						conferenceDao.updateStatus(confId,
								ConferenceStatus.OPEN);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void sendSMSEmailNotice(String confId, String scheduleTime,
			JSONArray jsonArray) throws JSONException {
		if (jsonArray == null) {
			return;
		}
		StringBuffer allPhone = new StringBuffer();
		LinkedList<String> emailList = new LinkedList<String>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject attendee = jsonArray.getJSONObject(i);
			try {
				String phone = (String) attendee.get(AttendeeConstants.phone
						.name());
				if (null != phone && phone.length() > 0) {
					allPhone.append(phone).append(",");
				}
			} catch (JSONException e) {
			}
			try {
				String email = (String) attendee.get(AttendeeConstants.email
						.name());
				if (null != email && email.length() > 0) {
					emailList.add(email);
				}
			} catch (JSONException e) {
			}
		}

		String subject = "电话会议通知";
		String content = "您在" + scheduleTime + "有电话会议，会议密码：" + confId
				+ "，到时拨打 0551-62379997 加入会议。";

		try {
			if (allPhone.length() > 0) {
				ContextLoader.getSMSClient().sendTextMessage(
						allPhone.toString(), content);
			}
			if (emailList.size() > 0) {
				ContextLoader.getMailSender().sendMail(emailList, subject,
						content);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}
