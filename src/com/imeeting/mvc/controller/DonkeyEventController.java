package com.imeeting.mvc.controller;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/donkeyevent")
public class DonkeyEventController {
	
	private static final String appId = "appId";
	private static final String reqId = "reqId";
	
	private static Log log = LogFactory.getLog(DonkeyEventController.class);

	@PostConstruct
	public void init(){
		
	}
	
	@RequestMapping
	public void onEvent(
			HttpServletResponse response,
			@RequestBody String requestBody){
		response.setStatus(HttpServletResponse.SC_OK);
		log.info("\nDonkey Event : \n" + requestBody);
		try {
			JSONObject event = new JSONObject(requestBody);
			
		} catch (JSONException e) {
			log.error("\nCannot parse donkey event to JSON object : \n" + requestBody);
			e.printStackTrace();
		}
	}
	
}
