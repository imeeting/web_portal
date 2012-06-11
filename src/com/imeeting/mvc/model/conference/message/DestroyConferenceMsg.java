package com.imeeting.mvc.model.conference.message;

import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.conference.ConferenceModel;
import com.richitec.donkey.client.DonkeyHttpResponse;

public class DestroyConferenceMsg implements IConferenceMessage {

	@Override
	public void onReceive(ConferenceModel model) throws Exception {
		DonkeyHttpResponse donkeyResponse = 
			ContextLoader.getDonkeyClient().destroyConference(model.getAudioConfId(), model.getConfId());
		if (donkeyResponse.isAccepted()){
			//
		} else {
			//
		}

		model.stop();
	}

}
