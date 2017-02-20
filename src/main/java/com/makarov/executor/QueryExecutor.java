package com.makarov.executor;

import com.makarov.core.DataSourceLoader;
import com.makarov.core.exception.QueryExecuteException;
import com.makarov.mapper.impl.DBMapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for sql-query to database
 *
 * @author Makarov Alexey
 * @version 1.0
 */
public class QueryExecutor {

    private static DBMapper mapper = new DBMapper();

    /**
     * Save object into table
     *
     * @param entity - stored object
     */
    public static void save(Object entity) {
        executeQuery(mapper.getDataFromObject(entity));
    }


    /**
     * Finds one element in the table
     * If several elements found, returns first
     * If nothing is found return null
     *
     * @param query - select sql query
     * @param clazz - return type of the elements
     * @return entity
     */
    public static <T> T findOne(String query, Class<T> clazz) {
        try (Connection connection = DataSourceLoader.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return mapper.getObjectFromData(resultSet, clazz, null);
            } else {
                return null;
            }

        } catch (SQLException exception) {
            throw new QueryExecuteException(exception.getMessage());
        }
    }

    /**
     * Finds several element in the table
     * If nothing is found return null
     *
     * @param query - select sql query
     * @param clazz - return type of the elements
     * @return entity
     */
    public static <T> List<T> findSome(String query, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        try (Connection connection = DataSourceLoader.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                list.add(mapper.getObjectFromData(resultSet, clazz, null));
            }
            return list;
        } catch (SQLException exception) {
            throw new QueryExecuteException(exception.getMessage());
        }
    }


    /**
     * Execute void sql query, for example update or delete
     *
     * @param query - sql query
     */
    public static void executeQuery(String query) {
        try (Connection connection = DataSourceLoader.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(query);
        } catch (SQLException exception) {
            throw new QueryExecuteException(exception.getMessage());
        }
    }
}
