//package com.richitec.actor;
//
//import akka.actor.UntypedActor;
//
//public class IncrementActor extends UntypedActor {
//
//	@Override
//	public void onReceive(Object msg) throws Exception {
//		if (msg instanceof Integer){
//			Integer i = (Integer)msg;
//			if (0==i){
//				System.out.println("Start : " + System.currentTimeMillis());
//			}
//			i += 1;
//			if (i<1000000){
////				Thread.sleep(5);
//				getSelf().tell(i);
//			} else {
//				System.out.println("End   : " + System.currentTimeMillis());
//			}
//		}
//	}
//
//}
