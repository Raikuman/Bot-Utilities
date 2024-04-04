package com.raikuman.botutilities.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.LinkedHashMap;
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
            if (configFile.exists()) {
                // Check config
                checkConfig(configFile, config);
                continue;
            }

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

    private static void checkConfig(File file, Config config) {
        LinkedHashMap<String, String> foundConfigs = new LinkedHashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String[] configLine;
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                configLine = line.replaceAll("\\s+","").split("=");

                // Add to config map
                if (configLine.length == 1) {
                    // Config without value, add empty
                    foundConfigs.put(configLine[0], "");
                } else if (configLine.length == 2) {
                    foundConfigs.put(configLine[0], configLine[1]);
                }
            }
        } catch (IOException e) {
            logger.error("Could not check config file \"{}\"", config.fileName());
        }

        // Check discrepancies
        boolean discrepancy = false;
        for (Map.Entry<String, String> foundConfig : foundConfigs.entrySet()) {
            if (!config.configs().containsKey(foundConfig.getKey())) {
                discrepancy = true;
                break;
            }
        }

        if (discrepancy) {
            // Update config with correct data
            LinkedHashMap<String, String> newConfigMap = new LinkedHashMap<>();
            for (Map.Entry<String, String> configEntry : config.configs().entrySet()) {
                if (foundConfigs.containsKey(configEntry.getKey())) {
                    newConfigMap.put(configEntry.getKey(), foundConfigs.get(configEntry.getKey()));
                } else {
                    newConfigMap.put(configEntry.getKey(), configEntry.getValue());
                }
            }

            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
                for (Map.Entry<String, String> entry : newConfigMap.entrySet()) {
                    bufferedWriter.write(entry.getKey() + "=" + entry.getValue());
                    bufferedWriter.write("\n");
                }
            } catch (IOException e) {
                logger.error("Error updating config file \"{}\"", file.getName());
            }
        }
    }
}
