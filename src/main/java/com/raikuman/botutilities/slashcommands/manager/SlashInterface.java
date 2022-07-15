package com.raikuman.botutilities.slashcommands.manager;

import com.raikuman.botutilities.commands.manager.CategoryInterface;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/**
 * Provides interface for creating slash commands
 *
 * @version 1.0 2022-15-07
 * @since 1.2
 */
public interface SlashInterface {

	/**
	 * Handles what the slash will do using slash context
	 * @param ctx The slash context for handling
	 */
	void handle(SlashContext ctx);

	/**
	 * Returns the invocation string that the slash manager will check for a slash
	 * @return The invocation string
	 */
	String getInvoke();

	/**
	 * Returns a brief description of how the slash works
	 * @return The description string
	 */
	String getDescription();

	/**
	 * Returns the command data of the slash
	 * @return The command data
	 */
	CommandData getCommandData();

	/**
	 * Returns the category interface of the slash
	 * @return The category interface
	 */
	default CategoryInterface getCategory() {
		return null;
	}
}
