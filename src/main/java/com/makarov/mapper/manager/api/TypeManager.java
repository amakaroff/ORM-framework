package com.makarov.mapper.manager.api;

/**
 * Type manager, maps column value on java-entity and vice versa
 *
 * @author Makarov Alexey
 * @version 1.0
 */
public interface TypeManager {

    /**
     * Transform java-entity into column value
     *
     * @param object - java-entity
     * @return column value
     */
    String getSavedStringFromObject(Object object);


    /**
     * Transform column value into java-entity
     *
     * @param data - column value
     * @return java-entity
     */
    Object getObjectFromData(Object data);
}
