package com.makarov.mapper.exception.dbmapper;

import com.makarov.mapper.exception.DBMapperException;

/**
 * @author Makarov Alexey
 * @version 1.0
 */
public class MappingFieldNotFoundException extends DBMapperException {

    public MappingFieldNotFoundException() {
    }

    public MappingFieldNotFoundException(String message) {
        super(message);
    }
}
