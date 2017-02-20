package com.makarov.mapper.manager.impl;

import com.makarov.mapper.manager.api.TypeManager;

/**
 * @author Makarov Alexey
 * @version 1.0
 */
public class DefaultTypeManager implements TypeManager {

    @Override
    public String getSavedStringFromObject(Object object) {
        String parameter;

        if (object == null) {
            return "NULL";
        }

        parameter = object.toString();
        if (object.getClass().isAssignableFrom(String.class)) {
            parameter = "'" + parameter + "'";
        }

        return parameter;
    }

    @Override
    public Object getObjectFromData(Object data) {
        return data;
    }
}
