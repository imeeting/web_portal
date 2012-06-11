package com.imeeting.mvc.model.conference.message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.conference.ConferenceModel;
import com.richitec.donkey.client.DonkeyClient;
import com.richitec.donkey.client.DonkeyHttpResponse;

public class CreateAudioConferenceMsg implements IConferenceMessage {
	
	private static Log log = LogFactory.getLog(CreateAudioConferenceMsg.class);

	@Override
	public void onReceive(ConferenceModel model) throws Exception {
		DonkeyClient donkeyClient = ContextLoader.getDonkeyClient();
		//using confId ad request ID.
		DonkeyHttpResponse donkeyResponse = donkeyClient.createConference(model.getConfId());
		if (donkeyResponse.isAccepted()){
			JSONObject json = new JSONObject(donkeyResponse.getEntityAsString());
			String audioConfId = json.getString(DonkeyHttpResponse.CONFERENCE);
			model.setAudioConfId(audioConfId);
			log.info(audioConfId);
		} else {
			//
			log.error(donkeyResponse.getStatusCode());
			model.stop();
		}		
	}

}
