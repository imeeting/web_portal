package com.imeeting.mvc.model.group;

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
	
	private Map<String, GroupModel> groupMap = null;

	public GroupManager(){
		actorSystem = ActorSystem.create("imeeting");
		groupMap = new ConcurrentHashMap<String, GroupModel>();
	}
	
	public GroupModel getGroup(String groupId){
		return groupMap.get(groupId);
	}
	
	public GroupModel removeConference(String groupId){
		return groupMap.remove(groupId);
	}
	
	@SuppressWarnings("serial")
	public ActorRef createGroup(final String groupId, final String userName) {
		ActorRef actor = actorSystem.actorOf(new Props(new UntypedActorFactory(){
			@Override
			public Actor create() {
				log.info("create group model: " + groupId + " username: " + userName);
				GroupModel model = new GroupModel(groupId, userName);
				groupMap.put(groupId, model);
				return model;
			}
		}), groupId);
		
		return actor;
	} 
	
}
