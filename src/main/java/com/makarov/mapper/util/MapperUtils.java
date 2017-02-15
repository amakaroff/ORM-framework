package com.makarov.mapper.util;

import com.makarov.annotation.JoinColumn;
import com.makarov.annotation.Table;
import com.makarov.mapper.exception.CollectionReturnTypeException;
import com.makarov.mapper.exception.GetterException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;


public class MapperUtils {

    public String getValueField(Field field, Object entity) {
        Class<?> clazz = entity.getClass();
        String name = field.getName();

        if (field.isAnnotationPresent(JoinColumn.class)) {
            try {
                field.setAccessible(true);
                entity = field.get(entity);
                if (entity == null) {
                    return "NULL";
                }
                clazz = entity.getClass();
                name = field.getAnnotation(JoinColumn.class).name();
            } catch (IllegalAccessException e) {
                throw new GetterException("Variable field : " + name + " has inadmissible access");
            }
        }

        String methodName;

        if (field.getType().isAssignableFrom(boolean.class)) {
            methodName = createMethodName("is", name);
        } else {
            methodName = createMethodName("get", name);
        }

        try {
            Object result = clazz.getMethod(methodName).invoke(entity);
            result = getParameter(result);

            return result.toString();
        } catch (ReflectiveOperationException exception) {
            throw new GetterException("Following method : "
                    + methodName + " cannot be reached or invoked");
        }
    }

    public String getParameter(Object param) {
        String parameter;

        if (param == null) {
            return "NULL";
        }

        parameter = param.toString();

        if (param.getClass().isAssignableFrom(String.class)) {
            parameter = "'" + parameter + "'";
        }

        return parameter;
    }

    public String getTableName(Method method) {
        Class<?> type = method.getReturnType();

        if (!type.isAnnotationPresent(Table.class)) {
            ParameterizedType genericReturnType = (ParameterizedType) method.getGenericReturnType();
            if (genericReturnType == null) {
                return null;
            }
            type = (Class<?>) genericReturnType.getActualTypeArguments()[0];
        }

        return getTableName(type);
    }

    public Class<?> getGenericTypeFromField(Field field) {
        ParameterizedType clazz = (ParameterizedType) field.getGenericType();
        return (Class<?>) clazz.getActualTypeArguments()[0];
    }

    public String getTableName(Class<?> type) {
        if (!type.isAnnotationPresent(Table.class)) {
            ParameterizedType genericReturnType = (ParameterizedType) type.getGenericSuperclass();
            if (genericReturnType == null) {
                return null;
            }
            type = (Class<?>) genericReturnType.getActualTypeArguments()[0];
        }

        Table table = type.getAnnotation(Table.class);

        if (table == null) {
            return null;
        } else {
            return table.name();
        }
    }

    public Collection getCollection(Class<?> collectionType, Object collection) {
        if (Collection.class.isAssignableFrom(collectionType)) {
            if (collectionType.isInterface()) {

                if (Set.class.isAssignableFrom(collectionType)) {
                    collectionType = HashSet.class;
                } else if (List.class.isAssignableFrom(collectionType)) {
                    collectionType = ArrayList.class;
                } else if (Queue.class.isAssignableFrom(collectionType)) {
                    collectionType = ArrayDeque.class;
                } else {
                    throw new CollectionReturnTypeException("This collection isn't supported");
                }
            }

            try {
                Constructor constructor = collectionType.getConstructor(Collection.class);
                if (collection == null) {
                    collection = new ArrayList();
                }

                return (Collection) constructor.newInstance(collection);
            } catch (ReflectiveOperationException e) {
                throw new CollectionReturnTypeException("Collection constructor is not worked");
            }
        } else {
            throw new CollectionReturnTypeException("Return type can be implements collection");
        }
    }

    public String createMethodName(String keyWord, String fieldName) {
        return keyWord
                + Character.toUpperCase(fieldName.charAt(0))
                + fieldName.substring(1);
    }
}
