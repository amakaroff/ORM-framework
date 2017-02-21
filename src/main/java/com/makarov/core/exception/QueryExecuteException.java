package com.makarov.core.exception;

/**
 * @author Makarov Alexey
 * @version 1.0
 */
public class QueryExecuteException extends RuntimeException {

    public QueryExecuteException() {
    }

    public QueryExecuteException(String message) {
        super(message);
    }

    public QueryExecuteException(Throwable cause) {
        super(cause);
    }
}
