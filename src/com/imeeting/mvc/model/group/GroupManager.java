package com.imeeting.mvc.model.group;

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

public class GroupManager {
	
	private static Log log = LogFactory.getLog(GroupManager.class);
	
	private ActorSystem actorSystem = null;
	
	private Map<String, GroupModel> conferenceMap = null;

	public GroupManager(){
		actorSystem = ActorSystem.create("imeeting");
		conferenceMap = new ConcurrentHashMap<String, GroupModel>();
	}
	
	public GroupModel getGroup(String confId){
		return conferenceMap.get(confId);
	}
	
	public GroupModel removeConference(String confId){
		return conferenceMap.remove(confId);
	}
	
	public ActorRef createGroup(final String confId, final String userName) throws SQLException{
		ActorRef actor = actorSystem.actorOf(new Props(new UntypedActorFactory(){
			@Override
			public Actor create() {
				GroupModel model = new GroupModel(confId, userName);
				conferenceMap.put(confId, model);
				log.info("create conference model: " + confId + " username: " + userName);
				return model;
			}
		}), confId);
		
		return actor;
	} 
	
}
