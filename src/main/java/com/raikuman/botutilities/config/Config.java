package com.raikuman.botutilities.config;

import java.util.LinkedHashMap;

public interface Config {

    String fileName();

    LinkedHashMap<String, String> configs();
}
