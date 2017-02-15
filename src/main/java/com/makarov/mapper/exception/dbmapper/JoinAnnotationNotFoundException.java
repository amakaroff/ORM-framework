package com.makarov.mapper.exception.dbmapper;

import com.makarov.mapper.exception.DBMapperException;


public class JoinAnnotationNotFoundException extends DBMapperException {

    public JoinAnnotationNotFoundException() {
    }

    public JoinAnnotationNotFoundException(String message) {
        super(message);
    }
}
