package com.raikuman.botutilities.invocation;

import net.dv8tion.jda.api.entities.emoji.Emoji;

public interface Category {
    String getCategory();
    Emoji getEmoji();
    default boolean isEqual(Category category) {
        return getCategory().equals(category.getCategory()) && getEmoji().equals(category.getEmoji());
    }
}
