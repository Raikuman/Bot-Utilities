package com.raikuman.botutilities.defaults.invocation;

import com.raikuman.botutilities.invocation.Category;
import net.dv8tion.jda.api.entities.emoji.Emoji;

public class Settings implements Category {

    @Override
    public String getCategory() {
        return "settings";
    }

    @Override
    public Emoji getEmoji() {
        return Emoji.fromFormatted("⚙\uFE0F");
    }
}
