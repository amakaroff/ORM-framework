package com.makarov.mapper.api;

import java.sql.ResultSet;


public interface Mapper {

    <T> String getDataFromObject(T obj);

    <T> T getObjectFromData(ResultSet data, Class<T> clazz, Object mappedEntity);
}
