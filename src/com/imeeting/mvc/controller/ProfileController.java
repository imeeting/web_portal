package com.imeeting.mvc.controller;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.richitec.util.ConfigManager;
import com.richitec.util.RandomString;

@Controller
@RequestMapping(value="/profile")
public class ProfileController {
	
	private static Log log = LogFactory.getLog(ProfileController.class);
	
	@RequestMapping(value="/changepassword")
	public void changePassword(){
		
	}
	
	@RequestMapping(value="/avatar", method=RequestMethod.GET)
	public String uploadAvatar(){
		return "avatar";
	}	
	
	@RequestMapping(value="/avatar", method=RequestMethod.POST)
	public String avatarHandler(
			@RequestParam("username") String username,
			@RequestParam("avatar") MultipartFile avatarFile) throws IllegalStateException, IOException{
		log.info("Username: " + username);
		log.info("File Origin Name: " + avatarFile.getOriginalFilename());
		log.info("File Name: " + avatarFile.getName());
		log.info("File Size: " + avatarFile.getSize());
		String tmpDir = ConfigManager.getInstance().getAttribute("upload_tmp_dir");
		String source_id = "im_" + RandomString.genRandomNum(10);
		String tmpFile = tmpDir + source_id;
		avatarFile.transferTo(new File(tmpFile));
		return "avatar";
	}	
}
