package com.makarov.core;

import com.makarov.core.exception.EmptyPropertyException;
import org.apache.commons.dbcp.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class DataSourceLoader {

    private static BasicDataSource dataSource;

    private static void load() {
        if (isValidConfig(ConfigLoader.getResources())) {
            dataSource = new BasicDataSource();

            dataSource.setDriverClassName(ConfigLoader.getResource("driver"));
            dataSource.setUsername(ConfigLoader.getResource("login"));
            dataSource.setPassword(ConfigLoader.getResource("password"));
            dataSource.setUrl(ConfigLoader.getResource("url"));
        } else {
            throw new EmptyPropertyException("One of the mandatory properties is empty");
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            load();
        }

        return dataSource.getConnection();
    }

    private static boolean isValidConfig(Map<String, String> config) {
        for (String property : config.values()) {
            if (property == null) {
                return false;
            }
        }

        return true;
    }
}
