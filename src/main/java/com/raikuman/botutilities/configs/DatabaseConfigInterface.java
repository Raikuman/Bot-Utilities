package com.raikuman.botutilities.configs;

import java.util.List;

/**
 * Provides interface for adding database functionality to config interfaces
 *
 * @version 1.1 2022-16-07
 * @since 1.2
 */
public interface DatabaseConfigInterface {

	/**
	 * Returns the database statement to create a table of this config
	 * @return The config database statement
	 */
	List<String> tableStatements();
}
