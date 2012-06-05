package com.imeeting.mvc.model.conference;

import akka.actor.ActorSystem;

public class ActorUtil {
	
	private static ActorSystem system = null;

	public static ActorSystem getActorSystem(){
		if (null == system){
			system = ActorSystem.create("imeeting");
		}
		return system;
	}
}
