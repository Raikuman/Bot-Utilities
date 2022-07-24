package com.raikuman.botutilities.configs;

import com.raikuman.botutilities.helpers.FileLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Handles config files by loading and writing configs from files
 *
 * @version 1.4 2022-24-07
 * @since 1.0
 */
public class ConfigIO {

	private static final Logger logger = LoggerFactory.getLogger(ConfigIO.class);
	private static final String DEFAULT_DIRECTORY = "config";

	/**
	 * Returns the value of a config given a file name
	 * @param fileName The name of the file to search in
	 * @param configName The name of the config to search for
	 * @return The config's value, else null
	 */
	public static String readConfig(String fileName, String configName) {
		// Retrieve all files in DEFAULT_DIRECTORY
		List<File> filesInFolder = null;
		try (Stream<Path> paths = Files.walk(Paths.get("config"))) {
			filesInFolder = paths
				.filter(Files::isRegularFile)
				.map(Path::toFile)
				.collect(Collectors.toList());
		} catch (IOException e) {
			logger.error("An error occurred when getting paths from the " + DEFAULT_DIRECTORY + " folder");
		}

		if (filesInFolder == null) {
			logger.warn("Config file " + fileName + " does not exist");
			return null;
		}

		// Ensure that given fileName (including path) matches existing files
		File foundFile = null;
		String filePath;
		for (File file : filesInFolder) {
			filePath = file.getPath()
				.substring(file.getPath().indexOf(File.separator) + 1)
				.replace(File.separator, "/");

			if (filePath.equals(fileName + ".cfg")) {
				foundFile = file;
				break;
			}
		}

		if (foundFile == null) {
			logger.warn("Config file " + fileName + " does not exist");
			return null;
		}

		String readConfig = null;
		for (String arrayString : FileLoader.readFileToArray(foundFile)) {
			if (arrayString.toLowerCase().contains(configName.toLowerCase())) {
				readConfig = arrayString;
				break;
			}
		}

		if (readConfig != null) {
			String[] splitString = readConfig.split("=");
			if (splitString.length == 1) {
				logger.warn("No value found for config settings " + configName);
				return null;
			} else {
				return readConfig.split("=")[1];
			}
		} else {
			return null;
		}
	}

	/**
	 * Writes a config and its value into a given file name
	 * @param fileName The name of the file to write to
	 * @param configName The name of the config to write
	 * @param configValue The value of the config to write
	 */
	public static void writeConfig(String fileName, String configName, String configValue) {
		File file = new File(DEFAULT_DIRECTORY + File.separator + fileName + ".cfg");

		if (!file.exists()) {
			logger.warn("Config file " + file.getName() + " does not exist");
			return;
		}

		if (readConfig(fileName, configName) != null) {
			logger.warn("Config setting " + configName + " already exists in config file " + file.getName());
			return;
		}

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
			writer.write(configName + "=" + configValue);
			writer.write("\n");

			writer.close();
		} catch (IOException e) {
			logger.error("Could not find file " + file.getName());
		}
	}

	/**
	 * Overwrites a config settings value with a new config value
	 * @param fileName The name of the file to write to
	 * @param configName The name of the config to overwrite its value
	 * @param configValue The value of the config to overwrite
	 */
	public static void overwriteConfig(String fileName, String configName, String configValue) {
		File file = new File(DEFAULT_DIRECTORY + File.separator + fileName + ".cfg");

		if (!file.exists()) {
			logger.warn("Config file " + file.getName() + " does not exist");
			return;
		}

		HashMap<String, String> configMap = new LinkedHashMap<>();
		String[] config;
		for (String string : FileLoader.readFileToArray(file)) {
			config = string.split("=");
			if (config.length < 1 || config.length> 2) {
				logger.error("Error retrieving config settings from config file " + file.getName());
				return;
			}

			if (config.length == 1)
				configMap.put(config[0], "");
			else
				configMap.put(config[0], config[1]);
		}

		configMap.replace(configName, configValue);

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));

			for (Map.Entry<String, String> entry : configMap.entrySet()) {
				writer.write(entry.getKey() + "=" + entry.getValue());
				writer.write("\n");
			}

			writer.close();
		} catch (IOException e) {
			logger.error("Could not find file " + file.getName());
		}
	}
}
