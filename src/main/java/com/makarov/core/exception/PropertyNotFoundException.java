package com.makarov.core.exception;


public class PropertyNotFoundException extends RuntimeException {

    public PropertyNotFoundException() {
    }

    public PropertyNotFoundException(String message) {
        super(message);
    }
}
