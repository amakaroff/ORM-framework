package com.makarov.core;

import com.makarov.core.exception.PropertyNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Class for loading configuration from property file
 *
 * @author Makarov Alexey
 * @version 1.0
 */
public class ConfigLoader {

    private static Map<String, String> resources;


    /**
     * Get mapping properties
     *
     * @return property map
     */
    static Map<String, String> getResources() {
        if (resources == null) {
            resources = new HashMap<>();
            load();
        }

        return resources;
    }


    /**
     * Get property by key-name
     *
     * @param key - property name
     * @return property
     */
    static String getResource(String key) {
        return getResources().get(key);
    }

    /**
     * Load configuration
     */
    private static void load() {
        Properties properties = new Properties();

        try (InputStream stream = ConfigLoader.class.getClassLoader()
                .getResourceAsStream("persistence.properties")) {
            if (stream != null) {
                properties.load(stream);
            } else {
                throw new PropertyNotFoundException("Property has not found");
            }
        } catch (IOException e) {
            throw new RuntimeException("Load error");
        }

        mapProperties(properties);
    }


    /**
     * Mapping property from file
     *
     * @param properties - properties
     */
    private static void mapProperties(Properties properties) {
        resources.put("driver", properties.getProperty("driver"));
        resources.put("url", properties.getProperty("url"));
        resources.put("login", properties.getProperty("login"));
        resources.put("password", properties.getProperty("password"));
    }
}
