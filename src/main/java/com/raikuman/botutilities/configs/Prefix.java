package com.raikuman.botutilities.configs;

import com.raikuman.botutilities.configs.defaults.DefaultConfig;
import com.raikuman.botutilities.database.DatabaseIO;
import com.raikuman.botutilities.database.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Handles loading the bot prefix
 *
 * @version 1.0 2022-13-07
 * @since 1.2
 */
public class Prefix {

	private static final Logger logger = LoggerFactory.getLogger(Prefix.class);

	public static String getPrefix(long guildId) {
		DefaultConfig defaultConfig = new DefaultConfig();
		String config = DatabaseIO.getConfig(
			// language=SQLITE-SQL
			"SELECT prefix FROM guild_settings WHERE guild_id = ?",
			guildId,
			"prefix"
		);

		if (config != null)
			return config;

		try (final PreparedStatement preparedStatement = DatabaseManager
			.getConnection()
			// language=SQLITE-SQL
			.prepareStatement(
				"INSERT INTO guild_settings(guild_id) VALUES(?)"
			)) {

			preparedStatement.setString(1, String.valueOf(guildId));
			preparedStatement.execute();
		} catch (SQLException e) {
			logger.warn("Could not insert " + defaultConfig + " to database");
		}

		return ConfigIO.readConfig(new DefaultConfig().fileName(), "prefix");
	}
}
