package com.devcommunity.infyStack.exceptions;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(String message) {
    	   super(message);

       }

	public ResourceNotFoundException() {
		super("Resource Not Found");
	}
}
