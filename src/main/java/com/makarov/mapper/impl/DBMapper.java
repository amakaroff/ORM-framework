package com.makarov.mapper.impl;

import com.makarov.annotation.Column;
import com.makarov.annotation.Table;
import com.makarov.annotation.relation.ManyToOne;
import com.makarov.annotation.relation.OneToMany;
import com.makarov.annotation.JoinColumn;
import com.makarov.annotation.relation.OneToOne;
import com.makarov.core.DataSourceLoader;
import com.makarov.core.exception.ConnectionOpenException;
import com.makarov.mapper.api.Mapper;
import com.makarov.mapper.exception.GetterException;
import com.makarov.mapper.exception.dbmapper.*;
import com.makarov.mapper.util.MapperUtils;
import com.sun.deploy.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class DBMapper implements Mapper {

    private MapperUtils utils = new MapperUtils();

    private String queryPattern = "SELECT * FROM %s WHERE %s=%s";

    public <T> T getObjectFromData(ResultSet resultSet, Class<T> clazz, Object mappedEntity) {

        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new ClassMappingException("Class cannot be mapped with table");
        }

        try {
            Object result = clazz.newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    String columnName = column.name();
                    String methodName = utils.createMethodName("set", field.getName());

                    Method setter = clazz.getMethod(methodName, field.getType());
                    Object columnValue = resultSet.getObject(columnName);

                    if (columnValue != null) {
                        setter.invoke(result, columnValue);
                    }
                }

                OneToOne oneToOne = field.getAnnotation(OneToOne.class);
                if (oneToOne != null) {
                    oneToOneHandler(resultSet, field, clazz, result, mappedEntity);
                }

                ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
                if (manyToOne != null) {
                    manyToOneHandler(resultSet, field, clazz, result, mappedEntity);
                }

                OneToMany oneToMany = field.getAnnotation(OneToMany.class);
                if (oneToMany != null) {
                    oneToManyHandler(resultSet, field, clazz, result, mappedEntity);
                }
            }

            return clazz.cast(result);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new CreateObjectException("Cannot create object: invalid query or class type");
        }
    }

    public <T> String getDataFromObject(T entity) {
        String pattern = "INSERT INTO %s (%s) VALUES (%s);";
        Class<?> clazz = entity.getClass();

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
                valuesList.add(utils.getValueField(field, entity));
            }
        }

        String columns = StringUtils.join(columnsList, ", ");
        String values = StringUtils.join(valuesList, ", ");

        return String.format(pattern, tableName, columns, values);
    }

    private void oneToOneHandler(ResultSet set, Field field, Class<?> clazz,
                                 Object object, Object mappedEntity) {
        OneToOne oneToOne = field.getAnnotation(OneToOne.class);
        String query = "";

        if ("".equals(oneToOne.mappedBy())) {
            if (mappedEntity == null) {
                query = getQueryFromParentEntity(field, set);
            }
        } else {
            if (mappedEntity == null) {
                query = getQueryFromChildEntity(field, set, oneToOne.mappedBy());
            }
        }

        if (!query.equals("")) {
            try (Connection connection = DataSourceLoader.getConnection();
                 Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(query);
                if (resultSet.next()) {
                    mappedEntity = getObjectFromData(resultSet, field.getType(), object);
                } else {
                    mappedEntity = null;
                }
            } catch (SQLException exception) {
                throw new ConnectionOpenException(exception.getMessage());
            }
        }

        String methodName = utils.createMethodName("set", field.getName());
        try {
            Method setter = clazz.getMethod(methodName, field.getType());
            setter.invoke(object, mappedEntity);
        } catch (ReflectiveOperationException exception) {
            throw new GetterException("Following method : "
                    + methodName + " cannot be reached or invoked");
        }
    }

    private String getQueryFromParentEntity(Field field, ResultSet set) {
        JoinColumn column = field.getAnnotation(JoinColumn.class);
        if (column == null) {
            throw new JoinAnnotationNotFoundException("Join annotation at field: "
                    + field.getName() + "is not found");
        } else {
            try {
                Object columnValue = set.getObject(column.name());
                String foreignKey = null;

                if (columnValue != null) {
                    foreignKey = columnValue.toString();
                }

                String tableName = field.getType().getAnnotation(Table.class).name();
                return String.format(queryPattern, tableName,
                        column.name(), utils.getParameter(foreignKey));
            } catch (SQLException exception) {
                throw new ColumnNameNotFoundException("Column: " + column.name() + "is not found");
            }
        }
    }

    private String getQueryFromChildEntity(Field field, ResultSet set, String fieldName) {
        JoinColumn column = null;

        try {
            Field joinField = field.getType().getDeclaredField(fieldName);
            column = joinField.getAnnotation(JoinColumn.class);

            return String.format(queryPattern, field.getType().getAnnotation(Table.class).name(),
                    column.name(), utils.getParameter(set.getObject(column.name())));
        } catch (NoSuchFieldException exception) {
            throw new MappingFieldNotFoundException("Field: " + fieldName + " is not found");
        } catch (SQLException exception) {
            throw new ColumnNameNotFoundException("Column: " + column.name() + "is not found");
        }
    }


    @SuppressWarnings(value = "unchecked")
    private void oneToManyHandler(ResultSet set, Field field, Class<?> clazz,
                                  Object object, Object mappedEntity) {
        String query;
        Class<?> insideClass = utils.getGenericTypeFromField(field);
        JoinColumn column = null;
        String fieldName = field.getAnnotation(OneToMany.class).mappedBy();

        try {
            Field joinField = insideClass.getDeclaredField(fieldName);
            column = joinField.getAnnotation(JoinColumn.class);
            String tableName = insideClass.getAnnotation(Table.class).name();

            String additionalParameter = "";
            if (mappedEntity != null) {
                additionalParameter = getAdditionalParam(mappedEntity);
            }
            query = String.format(queryPattern, tableName, column.name(),
                    utils.getParameter(set.getObject(column.name())) + additionalParameter);
        } catch (NoSuchFieldException exception) {
            throw new MappingFieldNotFoundException("Field: " + fieldName + " is not found");
        } catch (SQLException exception) {
            throw new ColumnNameNotFoundException("Column: " + column.name() + "is not found");
        }

        if (!query.equals("")) {
            try (Connection connection = DataSourceLoader.getConnection();
                 Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(query);
                if (Collection.class.isAssignableFrom(field.getType())) {
                    Collection collection = utils.getCollection(field.getType(), null);

                    if (mappedEntity != null) {
                        collection.add(mappedEntity);
                    }
                    while (resultSet.next()) {
                        collection.add(getObjectFromData(resultSet, insideClass, object));
                    }
                    mappedEntity = collection;
                }
            } catch (SQLException exception) {
                throw new ConnectionOpenException(exception.getMessage());
            }
        }

        String methodName = utils.createMethodName("set", field.getName());
        try {
            Method setter = clazz.getMethod(methodName, field.getType());
            setter.invoke(object, mappedEntity);
        } catch (ReflectiveOperationException exception) {
            throw new GetterException("Following method : "
                    + methodName + " cannot be reached or invoked");
        }
    }

    private String getAdditionalParam(Object object) {
        String result = " AND ";
        List<String> list = new ArrayList<>();
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                field.setAccessible(true);
                try {
                    list.add(field.getAnnotation(Column.class).name()
                            + "="
                            + utils.getParameter(field.get(object)));
                } catch (IllegalAccessException exception) {
                    throw new MappingFieldNotFoundException("Field: "
                            + field.getName()
                            + " have a illegal access");
                }
            }
        }

        return result + StringUtils.join(list, " AND ");
    }

    private void manyToOneHandler(ResultSet set, Field field, Class<?> clazz,
                                  Object object, Object mappedEntity) {
        String query = "";
        if (mappedEntity == null) {
            JoinColumn column = field.getAnnotation(JoinColumn.class);
            if (column == null) {
                throw new JoinAnnotationNotFoundException("Join annotation at field: "
                        + field.getName() + "is not found");
            } else {
                try {
                    Object columnValue = set.getObject(column.name());

                    String tableName = field.getType().getAnnotation(Table.class).name();
                    query = String.format(queryPattern, tableName,
                            column.name(), utils.getParameter(columnValue));
                    System.out.println(query);
                } catch (SQLException exception) {
                    throw new ColumnNameNotFoundException("Column: " + column.name() + "is not found");
                }
            }
        }

        if (!query.equals("")) {
            try (Connection connection = DataSourceLoader.getConnection();
                 Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(query);
                resultSet.next();
                if (mappedEntity == null) {
                    mappedEntity = getObjectFromData(resultSet, field.getType(), object);
                }
            } catch (SQLException exception) {
                throw new ConnectionOpenException(exception.getMessage());
            }
        }

        String methodName = utils.createMethodName("set", field.getName());
        try {
            Method setter = clazz.getMethod(methodName, field.getType());
            setter.invoke(object, mappedEntity);
        } catch (ReflectiveOperationException exception) {
            throw new GetterException("Following method : "
                    + methodName + " cannot be reached or invoked");
        }
    }
}
