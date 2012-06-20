package com.imeeting.mvc.model.group.message;

import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.group.GroupModel;
import com.richitec.donkey.client.DonkeyHttpResponse;

public class DestroyConferenceMsg implements IGroupMessage {

	@Override
	public void onReceive(GroupModel model) throws Exception {
		DonkeyHttpResponse donkeyResponse = 
			ContextLoader.getDonkeyClient().destroyConference(model.getAudioConfId(), model.getGroupId());
		if (donkeyResponse.isAccepted()){
			//
		} else {
			//
		}

		model.stop();
	}

}
