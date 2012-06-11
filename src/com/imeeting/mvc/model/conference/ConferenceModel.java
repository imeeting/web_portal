package com.imeeting.mvc.model.conference;


import java.sql.SQLException;

import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.conference.message.IConferenceMessage;

import akka.actor.UntypedActor;

public class ConferenceModel extends UntypedActor {

	private String confId;
	private String owner;
	private String audioConfId;
	
	public ConferenceModel(String confId, String owner){
		this.confId = confId;
		this.owner = owner;
	}
	
	public String getConfId(){
		return this.confId;
	}
	
	public String getOwner(){
		return this.owner;
	}
	
	public void setAudioConfId(String audioConfId){
		this.audioConfId = audioConfId;
	}
	
	public String getAudioConfId(){
		return this.audioConfId;
	}
	
	public void tell(IConferenceMessage msg){
		getSelf().tell(msg);
	}
	
	public void stop() throws SQLException{
		ConferenceDB.close(confId);
		
		ConferenceManager confManager = ContextLoader.getConferenceManager();
		confManager.removeConference(this.confId);

		getContext().stop(getSelf());
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof IConferenceMessage){
			IConferenceMessage confMsg = (IConferenceMessage)message;
			confMsg.onReceive(this);
		} else {
			unhandled(message);
		}		
	}
	
}