package com.makarov.core.exception;


public class ConnectionOpenException extends RuntimeException {

    public ConnectionOpenException() {
    }

    public ConnectionOpenException(String message) {
        super(message);
    }
}
