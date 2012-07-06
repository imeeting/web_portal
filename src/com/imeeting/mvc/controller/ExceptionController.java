package com.imeeting.mvc.controller;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ExceptionController {
	
	private static Log log = LogFactory.getLog(ExceptionController.class);
	
	@ExceptionHandler(IOException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody String handleIOException(IOException e){
		log.error("\nException : " + e.getMessage());
		return e.getMessage();
	}
	
	@ExceptionHandler(SQLException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody String handleSQLException(SQLException e){
		log.error("\nErrorCode : " + e.getErrorCode() + "\n" + 
				  "\nSQLState  : " + e.getSQLState() + "\n" +
				  "\nException   : " + e.getMessage());
		return e.getMessage();
	}
	
	@ExceptionHandler(JSONException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)	
	public @ResponseBody String handleJSONException(JSONException e){
		log.error("\nException : " + e.getMessage());
		return e.getMessage();
	}
}
