package com.raikuman.botutilities.buttons.manager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;

import java.util.List;

/**
 * Provides interface for creating buttons
 *
 * @version 1.0 2022-18-06
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

	/**
	 * Returns the list of EmbedBuilders to act as pages for the button
	 * @param ctx The button context to use to set embeds
	 * @return The list of embed builders
	 */
	List<EmbedBuilder> setPages(ButtonContext ctx);
}
