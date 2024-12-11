package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReplicateConfig {

    private static final Properties config;

    static {
        config = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream("replicate.properties");
        try {
            config.load(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String cluster1BootstrapServers() {
        return config.getProperty("cluster1.bootstrap.servers");
    }

    public static String cluster2BootstrapServers() {
        return config.getProperty("cluster2.bootstrap.servers");
    }

    public static String inputTopicName() {
        return config.getProperty("input.topic.name");
    }

    public static String outputTopicName() {
        return config.getProperty("output.topic.name");
    }

}
