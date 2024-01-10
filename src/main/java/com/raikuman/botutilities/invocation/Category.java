package com.raikuman.botutilities.invocation;

public interface Category {
    String getCategory();
    String getEmoji();
    default boolean isEqual(Category category) {
        return getCategory().equals(category.getCategory()) && getEmoji().equals(category.getEmoji());
    }
}
