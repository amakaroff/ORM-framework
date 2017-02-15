package com.makarov.core;

import com.makarov.core.exception.PropertyNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigLoader {

    private static Map<String, String> resources;

    public static Map<String, String> getResources() {
        if (resources == null) {
            resources = new HashMap<>();
            load();
        }

        return resources;
    }

    public static String getResource(String key) {
        return getResources().get(key);
    }

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

    private static void mapProperties(Properties properties) {
        resources.put("driver", properties.getProperty("driver"));
        resources.put("url", properties.getProperty("url"));
        resources.put("login", properties.getProperty("login"));
        resources.put("password", properties.getProperty("password"));
    }
}
