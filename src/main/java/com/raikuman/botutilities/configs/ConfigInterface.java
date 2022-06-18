package com.raikuman.botutilities.configs;

import java.util.HashMap;

/**
 * Provides interface for creating config files
 *
 * @version 1.0 2022-17-06
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
	HashMap<String, String> getConfigs();
}
