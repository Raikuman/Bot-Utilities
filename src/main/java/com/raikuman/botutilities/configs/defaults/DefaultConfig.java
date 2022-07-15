package com.raikuman.botutilities.configs.defaults;

import com.raikuman.botutilities.configs.ConfigInterface;
import com.raikuman.botutilities.configs.DatabaseConfigInterface;

import java.util.LinkedHashMap;

/**
 * Provides configuration for bot
 *
 * @version 1.1 2022-13-07
 * @since 1.0
 */
public class DefaultConfig implements ConfigInterface, DatabaseConfigInterface {

	@Override
	public String fileName() {
		return "settings";
	}

	@Override
	public LinkedHashMap<String, String> getConfigs() {
		LinkedHashMap<String, String> configMap = new LinkedHashMap<>();
		configMap.put("prefix", "!");
		configMap.put("globalappcommands", "false");

		return configMap;
	}

	@Override
	public String tableName() {
		return "guild_settings";
	}

	@Override
	public String tableStatement() {
		// language=SQLITE-SQL
		return "CREATE TABLE IF NOT EXISTS " + tableName() + " (" +
			"id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"guild_id VARCHAR(20) NOT NULL," +
			"prefix VARCHAR(255) NOT NULL DEFAULT '!');";
	}
}
