package com.makarov.mapper.exception.dbmapper;

import com.makarov.mapper.exception.DBMapperException;


public class MappingFieldNotFoundException extends DBMapperException {

    public MappingFieldNotFoundException() {
    }

    public MappingFieldNotFoundException(String message) {
        super(message);
    }
}
