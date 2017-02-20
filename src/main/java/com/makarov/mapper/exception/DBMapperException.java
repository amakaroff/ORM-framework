package com.makarov.mapper.exception;

/**
 * @author Makarov Alexey
 * @version 1.0
 */
public class DBMapperException extends RuntimeException {

    public DBMapperException() {
    }

    public DBMapperException(String message) {
        super(message);
    }
}
