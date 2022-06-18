package com.raiku.botutilities.commands.manager;

/**
 * Provides interface for creating categories
 *
 * @version 1.0 2022-18-06
 * @since 1.0
 */
public interface CategoryInterface {

	/**
	 * Returns the name of the category
	 * @return The category name
	 */
	String getName();

	/**
	 * Returns the emoji of the category
	 * @return The category emoji
	 */
	String getEmoji();
}
