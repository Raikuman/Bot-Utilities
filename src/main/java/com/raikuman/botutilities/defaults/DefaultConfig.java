package com.raikuman.botutilities.defaults;

import com.raikuman.botutilities.config.Config;

import java.util.LinkedHashMap;

public class DefaultConfig implements Config {

    @Override
    public String fileName() {
        return "settings";
    }

    @Override
    public LinkedHashMap<String, String> configs() {
        LinkedHashMap<String, String> configMap = new LinkedHashMap<>();
        configMap.put("defaultprefix", "!");
        configMap.put("globalapp", "false");
        configMap.put("thindatabase", "true");

        return configMap;
    }
}
