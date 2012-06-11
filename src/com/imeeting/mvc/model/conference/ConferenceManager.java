package com.imeeting.mvc.model.conference;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;

public class ConferenceManager {
	
	private static Log log = LogFactory.getLog(ConferenceManager.class);
	
	private ActorSystem actorSystem = null;
	
	private Map<String, ConferenceModel> conferenceMap = null;

	public ConferenceManager(){
		actorSystem = ActorSystem.create("imeeting");
		conferenceMap = new ConcurrentHashMap<String, ConferenceModel>();
	}
	
	public ConferenceModel getConference(String confId){
		return conferenceMap.get(confId);
	}
	
	public ConferenceModel removeConference(String confId){
		return conferenceMap.remove(confId);
	}
	
	public ConferenceModel createConference(final String confId, final String userName) throws SQLException{
		ActorRef actor = actorSystem.actorOf(new Props(new UntypedActorFactory(){
			@Override
			public Actor create() {
				ConferenceModel model = new ConferenceModel(confId, userName);
				conferenceMap.put(confId, model);
				return model;
			}
		}), confId);
		
		return getConference(confId);
	} 
	
}
