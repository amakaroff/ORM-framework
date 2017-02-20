package com.makarov.core;

import com.makarov.core.exception.EmptyPropertyException;
import org.apache.commons.dbcp.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Class for loading data source and getting connection
 *
 * @author Makarov Alexey
 * @version 1.0
 */
public class DataSourceLoader {

    private static BasicDataSource dataSource;

    /**
     * Load data source
     */
    private static void load() {
        if (isValidConfig(ConfigLoader.getResources())) {
            dataSource = new BasicDataSource();

            dataSource.setDriverClassName(ConfigLoader.getResource("driver"));
            TypeConfigurator.setCurrentDataBase(ConfigLoader.getResource("driver"));
            dataSource.setUsername(ConfigLoader.getResource("login"));
            dataSource.setPassword(ConfigLoader.getResource("password"));
            dataSource.setUrl(ConfigLoader.getResource("url"));
        } else {
            throw new EmptyPropertyException("One of the mandatory properties is empty");
        }
    }


    /**
     * Get connection to database
     *
     * @return connection
     * @throws SQLException - failed connection to database
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            load();
        }

        return dataSource.getConnection();
    }

    /**
     * Check properties
     *
     * @param config - mapped properties
     * @return false - some properties are empty
     * true - everything is ok
     */
    private static boolean isValidConfig(Map<String, String> config) {
        for (String property : config.values()) {
            if (property == null) {
                return false;
            }
        }

        return true;
    }
}
