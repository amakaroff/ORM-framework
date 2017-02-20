package com.makarov.core.exception;

/**
 * @author Makarov Alexey
 * @version 1.0
 */
public class EmptyPropertyException extends RuntimeException {

    public EmptyPropertyException() {
    }

    public EmptyPropertyException(String message) {
        super(message);
    }
}
