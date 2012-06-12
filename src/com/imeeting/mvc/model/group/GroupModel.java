package com.imeeting.mvc.model.group;


import java.sql.SQLException;
import java.util.List;

import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.group.message.IGroupMessage;

import akka.actor.UntypedActor;

public class GroupModel extends UntypedActor {

	private String confId;
	private String owner;
	private String audioConfId;
	private List<String> attendees;
	
	public GroupModel(String confId, String owner){
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
	
	public List<String> getAttendees() {
		return attendees;
	}

	public void setAttendees(List<String> attendees) {
		this.attendees = attendees;
	}

	public void tell(IGroupMessage msg){
		getSelf().tell(msg);
	}
	
	public void stop() throws SQLException{
		GroupDB.close(confId);
		
		GroupManager confManager = ContextLoader.getGroupManager();
		confManager.removeConference(this.confId);

		getContext().stop(getSelf());
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof IGroupMessage){
			IGroupMessage confMsg = (IGroupMessage)message;
			confMsg.onReceive(this);
		} else {
			unhandled(message);
		}		
	}
	
}