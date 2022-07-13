package com.raikuman.botutilities.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Handles getting configs from the database and updating configs
 *
 * @version 1.0 2022-13-07
 * @since 1.2
 */
public class DatabaseIO {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseIO.class);

	/**
	 * Returns the value of a config from the database
	 * @param statement Statement to get value from database
	 * @param guildId Guild id to get from specific guild
	 * @param configName Name of the config to find the value of
	 * @return The config's value, else the default config value
	 */
	public static String getConfig(String statement, long guildId, String configName) {
		try (final PreparedStatement preparedStatement = DatabaseManager
			.getConnection().prepareStatement(statement)) {

			preparedStatement.setString(1, String.valueOf(guildId));

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next())
					return resultSet.getString(configName);
			}
		} catch (SQLException e) {
			logger.error("Could not retrieve config" + configName + "from database, using defaults");
		}

		return null;
	}

	/**
	 * Updates a config from the database with a new config value
	 * @param statement Statement to update the value in the database
	 * @param guildId Guild id to get from specific guild
	 * @param configValue The value of the config to update to
	 * @return If the config was successfully updated
	 */
	public static boolean updateConfig(String statement, long guildId, String configValue) {
		try (final PreparedStatement preparedStatement = DatabaseManager
			.getConnection().prepareStatement(statement)) {

			preparedStatement.setString(1, String.valueOf(configValue));
			preparedStatement.setString(2, String.valueOf(guildId));
			preparedStatement.execute();

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Could not insert config" + configValue + "to database, using defaults");
		}

		return false;
	}
}
