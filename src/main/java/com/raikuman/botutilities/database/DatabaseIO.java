package com.raikuman.botutilities.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Handles getting configs from the database and updating configs
 *
 * @version 1.1 2022-24-07
 * @since 1.2
 */
public class DatabaseIO {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseIO.class);

	/**
	 * Returns the value of a config from the database
	 * @param statement Statement to get value from database
	 * @param configName Name of the config to find the value of
	 * @param parameterValue Value to add to the parameter
	 * @return The config's value, else the default config value
	 */
	public static String getConfig(String statement, String configName, String parameterValue) {
		try (final PreparedStatement preparedStatement = DatabaseManager
			.getConnection().prepareStatement(statement)) {

			preparedStatement.setString(1, parameterValue);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next())
					return resultSet.getString(configName);
			}
		} catch (SQLException e) {
			logger.error("Could not retrieve config " + configName + " from database, using defaults");
		}

		return null;
	}

	/**
	 * Updates a config from the database with a new config value
	 * @param statement Statement to update the value in the database
	 * @param configValue The value of the config to update to
	 * @param parameterValue Value to add to the parameter
	 * @return If the config was successfully updated
	 */
	public static boolean updateConfig(String statement, String configValue, String parameterValue) {
		try (final PreparedStatement preparedStatement = DatabaseManager
			.getConnection().prepareStatement(statement)) {

			preparedStatement.setString(1, configValue);
			preparedStatement.setString(2, parameterValue);
			preparedStatement.execute();

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Could not insert config " + configValue + " to database, using defaults");
		}

		return false;
	}
}
