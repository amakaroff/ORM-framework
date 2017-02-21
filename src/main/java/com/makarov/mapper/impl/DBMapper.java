package com.makarov.mapper.impl;

import com.makarov.annotation.Column;
import com.makarov.annotation.JoinColumn;
import com.makarov.annotation.Table;
import com.makarov.annotation.relation.ManyToOne;
import com.makarov.annotation.relation.OneToMany;
import com.makarov.annotation.relation.OneToOne;
import com.makarov.core.DataSourceLoader;
import com.makarov.core.TypeConfigurator;
import com.makarov.core.exception.QueryExecuteException;
import com.makarov.lazy.proxy.LazyProxyClassFactory;
import com.makarov.mapper.api.Mapper;
import com.makarov.mapper.exception.dbmapper.ClassMappingException;
import com.makarov.mapper.exception.dbmapper.CreateObjectException;
import com.makarov.mapper.fetch.FetchType;
import com.makarov.mapper.manager.api.TypeManager;
import com.makarov.mapper.util.MapperUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Makarov Alexey
 * @version 1.0
 */
public class DBMapper implements Mapper {

    private TypeManager manager = TypeConfigurator.getTypeManager();

    public <T> T getObjectFromData(ResultSet resultSet, Class<T> clazz, Object mappedEntity) {

        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new ClassMappingException("Class cannot be mapped with table");
        }

        List<Field> lazyInitializeFields = new ArrayList<>();
        List<Object> lazyValueJoinColumn = new ArrayList<>();

        try {
            Object result = clazz.newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    String columnName = column.name();
                    Object columnValue = manager.getObjectFromData(resultSet.getObject(columnName));
                    MapperUtils.setValueField(field, result, columnValue);
                }

                if (field.isAnnotationPresent(OneToOne.class) ||
                        field.isAnnotationPresent(OneToMany.class) ||
                        field.isAnnotationPresent(ManyToOne.class)) {
                    Object joinObject = MapperUtils.getJoinColumn(resultSet, field);
                    FetchType type = MapperUtils.getFetchType(field);
                    if (type == FetchType.LAZY) {
                        lazyInitializeFields.add(field);
                        lazyValueJoinColumn.add(joinObject);
                    } else {
                        if (mappedEntity != null && field.getType().isAssignableFrom(mappedEntity.getClass())) {
                            joinFieldHandle(joinObject, field, result, mappedEntity);
                        } else {
                            joinFieldHandle(joinObject, field, result, null);
                        }
                    }
                }
            }

            if (lazyInitializeFields.isEmpty()) {
                return clazz.cast(result);
            } else {
                return LazyProxyClassFactory.getProxy(clazz, result, lazyInitializeFields, lazyValueJoinColumn);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new CreateObjectException(exception.getMessage());
        }
    }

    public <T> String getDataFromObject(T entity) {
        String pattern = "INSERT INTO %s (%s) VALUES (%s);";
        Class<?> clazz;

        if (MapperUtils.isGeneratedClass(entity.getClass())) {
            clazz = MapperUtils.getEnhancerSuperclass(entity);
        } else {
            clazz = entity.getClass();
        }

        Table table = clazz.getAnnotation(Table.class);
        if (table == null) {
            throw new ClassMappingException("Class cannot be mapped with table");
        }
        String tableName = table.name();

        List<String> columnsList = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                String columnName = column.name();
                columnsList.add(columnName);
            }

            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            if (joinColumn != null) {
                String columnName = joinColumn.name();
                columnsList.add(columnName);
            }
        }

        List<String> valuesList = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)
                    || field.isAnnotationPresent(JoinColumn.class)) {
                valuesList.add(MapperUtils.getValueField(field, entity));
            }
        }

        String columns = String.join(", ", columnsList);
        String values = String.join(", ", valuesList);

        return String.format(pattern, tableName, columns, values);
    }

    public void joinFieldHandle(Object joinValue, Field field, Object object, Object mappedEntity) {
        String query = MapperUtils.createQuery(joinValue, field, mappedEntity);
        mappedEntity = getMappedEntity(query, field, object, mappedEntity);
        MapperUtils.setValueField(field, object, mappedEntity);
    }

    @SuppressWarnings(value = "unchecked")
    public Object getMappedEntity(String query, Field field, Object object, Object mappedEntity) {
        if (!query.isEmpty() && mappedEntity == null) {
            try (Connection connection = DataSourceLoader.getConnection();
                 Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(query);
                if (Collection.class.isAssignableFrom(field.getType())) {
                    Class<?> clazz = MapperUtils.getGenericTypeFromField(field);
                    Collection collection = MapperUtils.getCollection(field.getType(), null);

                    //TODO
                    /*if (mappedEntity != null) {
                        collection.add(mappedEntity);
                    }*/

                    while (resultSet.next()) {
                        collection.add(manager.getObjectFromData(getObjectFromData(resultSet, clazz, object)));
                    }
                    mappedEntity = collection;
                } else {
                    if (resultSet.next()) {
                        mappedEntity = manager.getObjectFromData(getObjectFromData(resultSet, field.getType(), object));
                    }
                }
            } catch (SQLException exception) {
                throw new QueryExecuteException(exception);
            }
        }

        return mappedEntity;
    }
}
