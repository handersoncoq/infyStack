package com.devcommunity.infyStack.exceptions;


public class AccessDeniedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AccessDeniedException(String message) {
    	   super(message);
       }

	public AccessDeniedException(){
		super("You do not have sufficient permissions to access this resource.");
	}




}

