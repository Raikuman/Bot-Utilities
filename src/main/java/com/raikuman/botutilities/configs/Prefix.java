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
 * @version 1.3 2022-24-07
 * @since 1.2
 */
public class Prefix {

	private static final Logger logger = LoggerFactory.getLogger(Prefix.class);

	public static String getPrefix(long guildId) {
		String config = DatabaseIO.getConfig(
			// language=SQLITE-SQL
			"SELECT prefix FROM guild_settings WHERE guild_id = ?",
			"prefix",
			String.valueOf(guildId)
		);

		if (config != null)
			return config;

		setDefault(guildId);

		return ConfigIO.readConfig(new DefaultConfig().fileName(), "prefix");
	}

	public static void updatePrefix(long guildId, String newPrefix) {
		boolean updated = DatabaseIO.updateConfig(
			// language=SQLITE-SQL
			"UPDATE guild_settings SET prefix = ? WHERE guild_id = ?",
			newPrefix,
			String.valueOf(guildId)
		);

		if (!updated)
			setDefault(guildId);
	}

	private static void setDefault(long guildId) {
		try (final PreparedStatement preparedStatement = DatabaseManager
			.getConnection()
			// language=SQLITE-SQL
			.prepareStatement(
				"INSERT OR IGNORE INTO guild_settings(guild_id) VALUES(?)"
			)) {

			preparedStatement.setString(1, String.valueOf(guildId));
			preparedStatement.execute();
		} catch (SQLException e) {
			logger.warn("Could not insert " + new DefaultConfig() + " to database");
		}
	}
}
