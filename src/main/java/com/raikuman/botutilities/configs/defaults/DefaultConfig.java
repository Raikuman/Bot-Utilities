package com.raikuman.botutilities.configs.defaults;

import com.raikuman.botutilities.configs.ConfigInterface;
import com.raikuman.botutilities.configs.DatabaseConfigInterface;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Provides configuration for bot
 *
 * @version 1.2 2022-16-07
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
	public List<String> tableStatements() {
		// language=SQLITE-SQL
		return List.of(
			"CREATE TABLE IF NOT EXISTS settings(" +
			"id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"guild_id VARCHAR(20) NOT NULL," +
			"prefix VARCHAR(255) NOT NULL DEFAULT '!')," +
			"UNIQUE(guild_id);"
		);
	}
}
