package com.makarov.factory.generator.manager;

import com.makarov.annotation.Table;
import com.makarov.mapper.api.Mapper;
import com.makarov.mapper.impl.DBMapper;

import java.lang.reflect.Array;


public class EntitySaver {

    public String getSaveQuery(Object[] params) {
        Object entity = params[0];
        Class<?> clazz = entity.getClass();

        Mapper mapper = new DBMapper();
        StringBuilder prepareQuery = new StringBuilder();

        if (clazz.isAnnotationPresent(Table.class)) {
            return mapper.getDataFromObject(entity);
        } else if (clazz.isArray() && clazz.getComponentType().isAnnotationPresent(Table.class)) {
            int length = Array.getLength(entity);

            for (int i = 0; i < length; i++) {
                prepareQuery.append(mapper.getDataFromObject(Array.get(entity, i)));
            }

            return prepareQuery.toString();
        } else {
            Iterable iterable;
            try {
                iterable = Iterable.class.cast(entity);
            } catch (ClassCastException exception) {
                throw new ClassCastException("Saved objects have to be a iterable");
            }

            for (Object anIterable : iterable) {
                prepareQuery.append(mapper.getDataFromObject(anIterable));
            }

            return prepareQuery.toString();
        }
    }
}
