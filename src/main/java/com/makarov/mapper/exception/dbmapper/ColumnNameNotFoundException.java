package com.makarov.mapper.exception.dbmapper;

import com.makarov.mapper.exception.DBMapperException;


public class ColumnNameNotFoundException extends DBMapperException {

    public ColumnNameNotFoundException() {
    }

    public ColumnNameNotFoundException(String message) {
        super(message);
    }
}
