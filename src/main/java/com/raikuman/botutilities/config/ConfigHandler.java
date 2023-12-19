package com.raikuman.botutilities.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

public class ConfigHandler {

    private static final Logger logger = LoggerFactory.getLogger(ConfigHandler.class);
    private static final String DEFAULT_CONFIG = "config" + File.separator;

    public static void writeConfigs(Config[] configs) {
        for (Config config : configs) {
            File configFile = new File(DEFAULT_CONFIG + config.fileName() + ".cfg");

            // Config exists...
            if (configFile.exists()) continue;

            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(configFile))) {
                for (Map.Entry<String, String> entry : config.configs().entrySet()) {
                    bufferedWriter.write(entry.getKey() + "=" + entry.getValue());
                    bufferedWriter.write("\n");
                }
            } catch (IOException e) {
                logger.error("Error creating config file \"" + config.fileName() + "\"");
            }
        }
    }
}
