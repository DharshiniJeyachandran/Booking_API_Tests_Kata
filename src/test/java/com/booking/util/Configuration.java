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
        try (InputStream in = Configuration.class.getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (in != null) {
                PROPS.load(in);
                LOG.info("Loaded application.properties");
            } else {
                String msg = "application.properties not found in classpath";
                LOG.error(msg);
                throw new IllegalStateException(msg);
            }

        } catch (IOException e) {
            LOG.error("Failed to load application.properties", e);
            throw new IllegalStateException("Could not load application.properties", e);
        }
    }

    private Configuration() {
        // prevent instantiation
    }

    public static String getProperty(String key) {
       return PROPS.getProperty(key);
    }
}

