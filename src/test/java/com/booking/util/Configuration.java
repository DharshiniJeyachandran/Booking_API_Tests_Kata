package com.booking.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    private static final Logger LOG = LogManager.getLogger(Configuration.class);
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = Configuration.class.getClassLoader().
                getResourceAsStream("application.properties")) {
            if (in == null) {
                throw new RuntimeException("application.properties not found in classpath");
            }
            PROPS.load(in);
            LOG.info("Loaded application.properties");
        } catch (IOException e) {
            LOG.error("Failed to load application.properties", e);
            throw new RuntimeException(e);
        }
    }

    private Configuration() {
        // prevent instantiation
    }

    public static String getProperty(String key) {
        String value = PROPS.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Property not found: " + key);
        }
        return value.trim();
    }
}

