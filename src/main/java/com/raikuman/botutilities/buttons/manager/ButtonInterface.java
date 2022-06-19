package com.raikuman.botutilities.buttons.manager;

import net.dv8tion.jda.api.entities.Emoji;

/**
 * Provides interface for creating buttons
 *
 * @version 1.2 2022-18-06
 * @since 1.0
 */
public interface ButtonInterface {

	/**
	 * Handles what the button will do using button context
	 * @param ctx The button context for handling
	 */
	void handle(ButtonContext ctx);

	/**
	 * Returns the button id that the button manager will check for a button
	 * @return The button id string
	 */
	String getButtonId();

	/**
	 * Returns the emoji that the button will display
	 * @return The emoji object
	 */
	Emoji getEmoji();

	/**
	 * Returns the label that the button will display
	 * @return The label string
	 */
	String getLabel();
}
