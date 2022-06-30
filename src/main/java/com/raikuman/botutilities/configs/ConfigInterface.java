package com.raikuman.botutilities.configs;

import java.util.LinkedHashMap;

/**
 * Provides interface for creating config files
 *
 * @version 1.1 2022-29-06
 * @since 1.0
 */
public interface ConfigInterface {

	/**
	 * Returns the name of the file
	 * @return The file name string
	 */
	String fileName();

	/**
	 * Returns a hashmap of configs
	 * @return The config hashmap
	 */
	LinkedHashMap<String, String> getConfigs();
}
