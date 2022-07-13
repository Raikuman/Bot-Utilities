package com.raikuman.botutilities.configs;

/**
 * Provides interface for adding database functionality to config interfaces
 *
 * @version 1.0 2022-13-07
 * @since 1.2
 */
public interface DatabaseConfigInterface {

	/**
	 * Returns the database table name of this config
	 * @return The config database table name
	 */
	String tableName();

	/**
	 * Returns the database statement to create a table of this config
	 * @return The config database statement
	 */
	String tableStatement();
}
