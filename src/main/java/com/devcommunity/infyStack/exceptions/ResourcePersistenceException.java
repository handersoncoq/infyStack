package com.devcommunity.infyStack.exceptions;

public class ResourcePersistenceException extends RuntimeException{

    private static final long serialVersionUID = 1L;
    public ResourcePersistenceException(String message){
        super(message);
    }

    public ResourcePersistenceException(){
        super("Resource could not be persisted");
    }
}
