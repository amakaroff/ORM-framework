package com.makarov.core.exception;

/**
 * @author Makarov Alexey
 * @version 1.0
 */
public class PropertyNotFoundException extends RuntimeException {

    public PropertyNotFoundException() {
    }

    public PropertyNotFoundException(String message) {
        super(message);
    }
}
