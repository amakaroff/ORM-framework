package com.makarov.mapper.util;

import com.makarov.annotation.Column;
import com.makarov.annotation.JoinColumn;
import com.makarov.annotation.Table;
import com.makarov.annotation.relation.ManyToOne;
import com.makarov.annotation.relation.OneToMany;
import com.makarov.annotation.relation.OneToOne;
import com.makarov.core.TypeConfigurator;
import com.makarov.mapper.exception.CollectionReturnTypeException;
import com.makarov.mapper.exception.GetterException;
import com.makarov.mapper.exception.dbmapper.MappingFieldNotFoundException;
import com.makarov.mapper.fetch.FetchType;
import com.makarov.mapper.manager.api.TypeManager;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Util class for entity mapping
 *
 * @author Makarov Alexey
 * @version 1.0
 */
public class MapperUtils {

    private static TypeManager manager = TypeConfigurator.getTypeManager();

    /**
     * Get value by field in entity
     *
     * @param field  - entity field
     * @param entity - entity
     * @return value of the field transformed into string
     */
    public static String getValueField(Field field, Object entity) {
        Class<?> clazz = getEnhancerSuperclass(entity);

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
            result = manager.getSavedStringFromObject(result);

            return result.toString();
        } catch (ReflectiveOperationException exception) {
            throw new GetterException("Following method : "
                    + methodName + " cannot be reached or invoked");
        }
    }

    /**
     * Set value into entity field
     *
     * @param field  - entity field
     * @param entity - entity
     * @param value  - set value
     */
    public static void setValueField(Field field, Object entity, Object value) {
        field.setAccessible(true);
        try {
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new GetterException("Variable field : " + field.getName() + " has inadmissible access");
        }
    }

    /**
     * Get table name from method
     *
     * @param method - method
     * @return table name
     */
    public static String getTableName(Method method) {
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

    /**
     * Get generic type in collection from the field
     *
     * @param field - field
     * @return genetic type
     */
    public static Class<?> getGenericTypeFromField(Field field) {
        if (Collection.class.isAssignableFrom(field.getType())) {
            ParameterizedType clazz = (ParameterizedType) field.getGenericType();
            return (Class<?>) clazz.getActualTypeArguments()[0];
        } else {
            return field.getType();
        }
    }

    /**
     * Get table name from entity type
     *
     * @param type - entity type
     * @return table name
     */
    public static String getTableName(Class<?> type) {
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

    /**
     * Transforms one collection to another
     * If collection is null returns empty collection
     *
     * @param collectionType - type of new collection
     * @param collection     - old collection
     * @return new collection
     */
    public static Collection getCollection(Class<?> collectionType, Object collection) {
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

    /**
     * Creates properties by field name
     *
     * @param keyWord   - property type
     * @param fieldName - field name
     * @return property name
     */
    public static String createMethodName(String keyWord, String fieldName) {
        return keyWord
                + Character.toUpperCase(fieldName.charAt(0))
                + fieldName.substring(1);
    }

    /**
     * Get type of super class from generated class
     *
     * @param enhancer - object of generated class
     * @return type of super class
     */
    public static Class<?> getEnhancerSuperclass(Object enhancer) {
        Class<?> clazz = enhancer.getClass();
        while (Enhancer.isEnhanced(clazz)) {
            clazz = clazz.getSuperclass();
        }

        return clazz;
    }

    /**
     * Check if the type of the class is generated
     *
     * @param clazz - type of the class
     * @return false - type is not generated
     * true - type is generated
     */
    public static boolean isGeneratedClass(Class<?> clazz) {
        return Enhancer.isEnhanced(clazz);
    }

    /**
     * Get join value of the field from result set
     *
     * @param set   - result set
     * @param field - join field
     * @return join value
     * @throws SQLException - join column is not found
     */
    public static Object getJoinColumn(ResultSet set, Field field) throws SQLException {
        JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
        if (field.isAnnotationPresent(OneToOne.class) || field.isAnnotationPresent(OneToMany.class)) {
            if (joinColumn == null) {
                String fieldName;
                if (field.isAnnotationPresent(OneToOne.class)) {
                    fieldName = field.getAnnotation(OneToOne.class).mappedBy();
                } else {
                    fieldName = field.getAnnotation(OneToMany.class).mappedBy();
                }
                Field joinField;
                try {
                    Class<?> clazz = getGenericTypeFromField(field);
                    System.out.println(clazz);
                    joinField = clazz.getDeclaredField(fieldName);
                } catch (NoSuchFieldException exception) {
                    throw new MappingFieldNotFoundException("Field: " + fieldName + " is not found");
                }
                joinColumn = joinField.getAnnotation(JoinColumn.class);
            }
        }
        return manager.getSavedStringFromObject(set.getObject(joinColumn.name()));
    }

    /**
     * Get fetch type from relation-annotation
     *
     * @param field - field with relation-annotation
     * @return Fetch type
     */
    public static FetchType getFetchType(Field field) {
        OneToOne oneToOne = field.getAnnotation(OneToOne.class);
        if (oneToOne != null) {
            return oneToOne.fetch();
        }

        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        if (oneToMany != null) {
            return oneToMany.fetch();
        }

        ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
        if (manyToOne != null) {
            return manyToOne.fetch();
        }

        return FetchType.EAGER;
    }

    /**
     * @deprecated Beta method
     */
    @Deprecated
    public static String getAdditionalParam(Object object) {
        if (object == null) {
            return "";
        }
        String result = " AND ";
        List<String> list = new ArrayList<>();
        for (Field field : getEnhancerSuperclass(object).getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                field.setAccessible(true);
                try {
                    list.add(field.getAnnotation(Column.class).name()
                            + "<>"
                            + manager.getSavedStringFromObject(field.get(object)));
                } catch (IllegalAccessException exception) {
                    throw new MappingFieldNotFoundException("Field: "
                            + field.getName()
                            + " have a illegal access");
                }
            }
        }

        return result + String.join(" AND ", list);
    }


    /**
     * Create query for finding mapped entity
     *
     * @param joinValue    - join value column
     * @param field        - join field
     * @param excessObject - deprecated
     * @return query
     */
    public static String createQuery(Object joinValue, Field field, Object excessObject) {
        String pattern = "SELECT * FROM %s WHERE %s=%s";
        String additionalParam = "";

        //TODO
        /*if (Collection.class.isAssignableFrom(field.getType())) {
            additionalParam = utils.getAdditionalParam(excessObject);
        }*/

        if (field.isAnnotationPresent(JoinColumn.class)) {
            String tableName = field.getType().getAnnotation(Table.class).name();
            return String.format(pattern, tableName, field.getAnnotation(JoinColumn.class).name(), joinValue + additionalParam);
        } else {
            String fieldName;
            if (field.isAnnotationPresent(OneToOne.class)) {
                fieldName = field.getAnnotation(OneToOne.class).mappedBy();
                if (fieldName.isEmpty()) {
                    return "";
                }
            } else if (field.isAnnotationPresent(OneToMany.class)) {
                fieldName = field.getAnnotation(OneToMany.class).mappedBy();
            } else {
                throw new MappingFieldNotFoundException("Join field: " + field.getName() + " is not annotation present");
            }

            Class<?> insideClass = MapperUtils.getGenericTypeFromField(field);
            Field joinField;

            try {
                joinField = insideClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                throw new MappingFieldNotFoundException("Join field: " + field.getName() + " is not found");
            }

            JoinColumn column = joinField.getAnnotation(JoinColumn.class);
            Class<?> clazz = MapperUtils.getGenericTypeFromField(field);
            String tableName = clazz.getAnnotation(Table.class).name();
            return String.format(pattern, tableName, column.name(), joinValue + additionalParam);
        }
    }
}
