package com.raiku.botutilities.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Writes default config files using the ConfigInterface class
 *
 * @version 1.1 2022-18-06
 * @since 1.0
 */
public class ConfigFileWriter {

	private static final Logger logger = LoggerFactory.getLogger(ConfigFileWriter.class);
	private static final String DEFAULT_DIRECTORY = "config";

	/**
	 * First tries to get directories from ConfigInterface and create them, then finally creating the
	 * config file and writing config values into it
	 * @param configInterface The config interface to write a file
	 */
	private static void writeConfigFile(ConfigInterface configInterface) {
		File directory = new File("config");

		if (directory.mkdirs())
			logger.info("Default directory created");

		File file;

		// Get list of directories from the file path
		List<String> directories = new ArrayList<>(List.of(configInterface.fileName().split("/")));
		directories.remove(directories.size() - 1);

		if (directories.size() > 0) {
			StringBuilder configDirectory = new StringBuilder(DEFAULT_DIRECTORY);

			for (String stringDirectory : directories)
				configDirectory
					.append("/")
					.append(stringDirectory);

			file = new File(configDirectory.toString());

			if (!file.mkdirs())
				logger.info("Config directory " + file.getPath() + " already exists, continuing...");
		}

		file = new File(DEFAULT_DIRECTORY + "/" + configInterface.fileName() + ".cfg");

		if (file.exists()) {
			logger.info("config " + file.getPath() + " already exists, continuing...");
			return;
		}

		try {
			if (file.createNewFile()) {
				logger.info("Created config " + file.getPath() + " using defaults");
			} else {
				logger.warn("Could not create config file " + file.getPath() + ", continuing...");
				return;
			}

			BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			for (Map.Entry<String, String> config : configInterface.getConfigs().entrySet())
				writer.write(config.getKey() + "=" + config.getValue());

			writer.close();
		} catch (IOException e) {
			logger.warn("Could not write to file " + file.getPath() + ", continuing...");
		}
	}

	/**
	 * Checks the current list of ConfigInterface if there are conflicting directories. If there are
	 * conflicting directories, the method will log
	 * @param configInterfaces The list of config interfaces to write files
	 */
	public static void writeConfigFiles(ConfigInterface... configInterfaces) {
		// Check conflicting config directories
		List<String> configDirectories = new ArrayList<>();
		for (ConfigInterface configInterface : configInterfaces)
			configDirectories.add(configInterface.fileName());

		for (String directory : configDirectories) {
			if (Collections.frequency(configDirectories, directory) > 1) {
				logger.error("There are conflicting config directories with the directory: " + directory +
					", no config files will be written");
				return;
			}
		}

		for (ConfigInterface configInterface : configInterfaces)
			writeConfigFile(configInterface);
	}
}
