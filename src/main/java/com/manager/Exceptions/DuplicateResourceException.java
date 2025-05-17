package com.manager.Exceptions;

public class DuplicateResourceException extends RuntimeException{

    public DuplicateResourceException(String message) {
        super(message);
    }
}
