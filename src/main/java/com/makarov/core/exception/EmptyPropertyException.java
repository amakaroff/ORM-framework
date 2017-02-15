package com.makarov.core.exception;


public class EmptyPropertyException extends RuntimeException {

    public EmptyPropertyException() {
    }

    public EmptyPropertyException(String message) {
        super(message);
    }
}
