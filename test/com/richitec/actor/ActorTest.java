//package com.richitec.actor;
//
//import org.junit.Test;
//
//import akka.actor.ActorRef;
//import akka.actor.ActorSystem;
//import akka.actor.Props;
//
//public class ActorTest {
//	
//	@Test
//	public void testActor(){
//		ActorSystem actorSystem = ActorSystem.create("test");
//		ActorRef actor = actorSystem.actorOf(new Props(IncrementActor.class));
//		actor.tell(new Integer(0));
//		try {
//			Thread.sleep(1000 * 10);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	@Test
//	public void testSynchronized(){
//		IncrementSynchronized obj = new IncrementSynchronized();
//		obj.increment(10, 0);
//		try {
//			Thread.sleep(1000 * 10);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}
//
//}
