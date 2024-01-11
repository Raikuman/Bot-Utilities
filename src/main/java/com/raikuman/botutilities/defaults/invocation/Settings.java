package com.raikuman.botutilities.defaults.invocation;

import com.raikuman.botutilities.invocation.Category;

public class Settings implements Category {

    @Override
    public String getCategory() {
        return "settings";
    }

    @Override
    public String getEmoji() {
        return "⚙\uFE0F";
    }
}
