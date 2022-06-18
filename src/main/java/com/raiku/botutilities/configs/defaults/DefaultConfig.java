package com.raiku.botutilities.configs.defaults;

import com.raiku.botutilities.configs.ConfigInterface;

import java.util.HashMap;
import java.util.Map;

public class DefaultConfig implements ConfigInterface {

	@Override
	public String fileName() {
		return "settings";
	}

	@Override
	public HashMap<String, String> getConfigs() {
		return new HashMap<>(Map.ofEntries(
			Map.entry("prefix", "!")
		));
	}
}
