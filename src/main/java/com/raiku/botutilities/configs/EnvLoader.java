package com.raiku.botutilities.configs;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Provides a function to load a .env file to get environment variables
 *
 * @version 1.0 2022-18-06
 * @since 1.0
 */
public class EnvLoader {

	private static final Dotenv dotenv = Dotenv.load();

	/**
	 * Returns the value of a given environment variable in a .env file
	 * @param key The name of the key to look for
	 * @return The value of the key
	 */
	public static String get(String key) {
		return dotenv.get(key.toUpperCase());
	}
}
