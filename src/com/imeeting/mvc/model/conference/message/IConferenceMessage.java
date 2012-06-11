package com.imeeting.mvc.model.conference.message;

import com.imeeting.mvc.model.conference.ConferenceModel;

public interface IConferenceMessage {

	public void onReceive(ConferenceModel model) throws Exception;
	
}
