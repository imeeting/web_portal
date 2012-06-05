package com.richitec.imeeting.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class JacksonTest {
	
	@Test
	public void mapToJson(){
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> test = new HashMap<String, Object>();
		test.put("key", "h");
		test.put("value", 2);
		try {
			String json = mapper.writeValueAsString(test);
			System.out.println(json);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
