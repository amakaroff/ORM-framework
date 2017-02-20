package com.makarov.mapper.api;

import java.sql.ResultSet;

/**
 * Class for mapping java-entity on table and vice versa
 *
 * @author Makarov Alexey
 * @version 1.0
 */
public interface Mapper {

    /**
     * Transform java-entity into table data
     *
     * @param obj - java-entity
     * @return row in table
     */
    <T> String getDataFromObject(T obj);

    /**
     * Transform table data into java-entity
     *
     * @param data         - result set
     * @param clazz        - java-entity type
     * @param mappedEntity - join entity
     * @return java-entity
     */
    <T> T getObjectFromData(ResultSet data, Class<T> clazz, Object mappedEntity);
}
