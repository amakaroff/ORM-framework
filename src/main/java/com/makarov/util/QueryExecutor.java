package com.makarov.util;

import com.makarov.core.DataSourceLoader;
import com.makarov.core.exception.ConnectionOpenException;
import com.makarov.mapper.impl.DBMapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class QueryExecutor {
    private static DBMapper mapper = new DBMapper();

    public void save(Object entity) {
        executeQuery(mapper.getDataFromObject(entity));
    }

    public <T> T findOne(String query, Class<T> clazz) {
        try (Connection connection = DataSourceLoader.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return mapper.getObjectFromData(resultSet, clazz, null);
            } else {
                return null;
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new ConnectionOpenException(exception.getMessage());
        }
    }

    public <T> List<T> findSome(String query, Class<T> clazz) {
        List<T> list = new ArrayList<>();

        try (Connection connection = DataSourceLoader.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                list.add(mapper.getObjectFromData(resultSet, clazz, null));
            }
            return list;
        } catch (SQLException exception) {
            throw new ConnectionOpenException(exception.getMessage());
        }

    }

    public void executeQuery(String query) {
        try (Connection connection = DataSourceLoader.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(query);
        } catch (SQLException exception) {
            throw new ConnectionOpenException(exception.getMessage());
        }
    }
}
