package com.raikuman.botutilities.configs.defaults;

import com.raikuman.botutilities.configs.ConfigInterface;

import java.util.LinkedHashMap;

/**
 * Provides configuration for bot
 *
 * @version 1.0 2022-29-06
 * @since 1.0
 */
public class DefaultConfig implements ConfigInterface {

	@Override
	public String fileName() {
		return "settings";
	}

	@Override
	public LinkedHashMap<String, String> getConfigs() {
		LinkedHashMap<String, String> configMap = new LinkedHashMap<>();
		configMap.put("prefix", "!");

		return configMap;
	}
}
