package com.raikuman.botutilities.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;

public class ConfigHandler {

    private static final Logger logger = LoggerFactory.getLogger(ConfigHandler.class);
    private static final String DEFAULT_CONFIG = "config" + File.separator;

    public static void writeConfigs(List<Config> configs) {
        // Check for config directory
        if (!new File(DEFAULT_CONFIG).exists()) {
            if (new File(DEFAULT_CONFIG).mkdir()) {
                logger.info("Created directory {}", DEFAULT_CONFIG);
            }
        }

        for (Config config : configs) {
            File configFile = new File(DEFAULT_CONFIG + config.fileName() + ".cfg");

            // Config exists...
            if (configFile.exists()) continue;

            // Check for config parent directory
            if (!configFile.getParentFile().exists()) {
                if (configFile.getParentFile().mkdir()) {
                    logger.info("Created directory {}", configFile.getParentFile().getPath());
                }
            }

            // Create config
            try {
                if (configFile.createNewFile()) {
                    logger.info("Config file \"{}\" created", config.fileName());
                } else {
                    logger.error("Could not create config file \"{}\"", config.fileName());
                }
            } catch (IOException e) {
                logger.error("Error creating config file \"{}\"", config.fileName());
                continue;
            }

            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(configFile))) {
                for (Map.Entry<String, String> entry : config.configs().entrySet()) {
                    bufferedWriter.write(entry.getKey() + "=" + entry.getValue());
                    bufferedWriter.write("\n");
                }
            } catch (IOException e) {
                logger.error("Error creating config file \"{}\"", config.fileName());
            }
        }
    }
}
