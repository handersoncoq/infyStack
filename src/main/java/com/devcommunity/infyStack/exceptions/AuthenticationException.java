package com.devcommunity.infyStack.exceptions;


public class AuthenticationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AuthenticationException(String message) {
    	   super(message);
       }
	public AuthenticationException(){
		super("You need valid credentials to access this resource.");
	}




}

