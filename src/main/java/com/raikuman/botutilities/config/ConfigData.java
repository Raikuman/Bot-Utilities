package com.raikuman.botutilities.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigData {

    private static final Logger logger = LoggerFactory.getLogger(ConfigData.class);
    private final LinkedHashMap<String, String> configs;
    private final Config config;
    private final String DEFAULT_CONFIG = "config" + File.separator;

    public ConfigData(Config config) {
        this.config = config;
        File configFile = new File(DEFAULT_CONFIG + config.fileName() + ".cfg");

        // Read configs
        LinkedHashMap<String, String> configSetup = new LinkedHashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(configFile))) {
            String[] configLine;
            for (String line; (line = bufferedReader.readLine()) != null;) {
                configLine = line.replaceAll("\\s+","").split("=");

                // Config incorrect, continue...
                if (configLine.length != 2) continue;

                configSetup.put(configLine[0], configLine[1]);
            }
        } catch (IOException e) {
            logger.error("Error reading config file \"" + config.fileName() + "\"");
            configSetup = new LinkedHashMap<>();
        }

        configs = configSetup;
    }

    public String getConfig(String config) {
        String foundConfig = configs.get(config);
        return Objects.requireNonNullElse(foundConfig, "");
    }

    public boolean setConfig(String configLabel, String value) {
        File configFile = new File(DEFAULT_CONFIG + config.fileName() + ".cfg");
        if (!configFile.exists() || configs.get(configLabel) == null) {
            logger.error("Error writing to config file \"" + DEFAULT_CONFIG + config.fileName() + "\"");
            return false;
        }

        configs.put(configLabel, value);

        // Write new config to file
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(configFile, false));

            for (Map.Entry<String, String> entry : configs.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.write("\n");
            }

            writer.close();
        } catch (IOException e) {
            logger.error("Error writing to config file \"" + config.fileName() + "\"");
            return false;
        }

        return true;
    }
}
