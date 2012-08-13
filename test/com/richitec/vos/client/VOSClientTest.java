package com.richitec.vos.client;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class VOSClientTest {

	@Test
	public void getDateTime(){
		Date now = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.print(df.format(now));
	}
}
