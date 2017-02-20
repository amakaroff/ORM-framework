package com.makarov.mapper.exception.dbmapper;

import com.makarov.mapper.exception.DBMapperException;

/**
 * @author Makarov Alexey
 * @version 1.0
 */

public class CreateObjectException extends DBMapperException {

    public CreateObjectException() {
    }

    public CreateObjectException(String message) {
        super(message);
    }
}
