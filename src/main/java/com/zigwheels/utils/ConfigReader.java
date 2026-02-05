package com.zigwheels.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static final Properties props = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
            props.load(fis);
        } catch (IOException e) {
            System.err.println("Could not load config.properties, using defaults. " + e.getMessage());
        }
    }

    public static String get(String key) {

        return props.getProperty(key);
    }

    public static String get(String key, String def) {

        return props.getProperty(key, def);
    }
}
