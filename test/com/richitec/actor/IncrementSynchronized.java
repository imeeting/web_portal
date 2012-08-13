package com.richitec.actor;

public class IncrementSynchronized {
	
	public void increment(Integer n, Integer value){
		for (int i=0; i<n; i++){
			Thread t1 = new Thread(new Runner(value));
			t1.start();
		}
	}
	
	public static class Runner implements Runnable {
		
		private Integer value;
		
		public Runner(Integer i){
			this.value = i;
		}

		@Override
		public void run() {
			System.out.println(Thread.currentThread().getId() +  " Start : " + System.currentTimeMillis());
			while (value < 1000000){
				synchronized (value) {
//					try {
//						Thread.sleep(5);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
					value = value + 1;
				}
			}
			System.out.println(Thread.currentThread().getId() +  " End   : " + System.currentTimeMillis());
		}
		
	}

}
