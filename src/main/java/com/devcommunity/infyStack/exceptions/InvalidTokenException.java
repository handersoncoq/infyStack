package com.devcommunity.infyStack.exceptions;


public class InvalidTokenException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidTokenException(String message) {
    	   super(message);
       }

	public InvalidTokenException() {
		super("Token could not be validated.");
	}




}

