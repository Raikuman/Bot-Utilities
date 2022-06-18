package com.raiku.botutilities.commands.manager;

import java.util.List;

/**
 * Provides interface for creating commands
 *
 * @version 1.0 2022-18-06
 * @since 1.0
 */
public interface CommandInterface {

	/**
	 * Handles what the command will do using command context
	 * @param ctx The command context for handling
	 */
	void handle(CommandContext ctx);

	/**
	 * Returns the invocation string that the command manager will check for a command
	 * @return The invocation string
	 */
	String getInvoke();

	/**
	 * Returns the usage string that shows how the command is used
	 * @return The usage string
	 */
	String getUsage();

	/**
	 * Returns a brief description of how the command works
	 * @return The description string
	 */
	String getDescription();

	/**
	 * Returns a list of aliases that the command can be invoked by
	 * @return The list of aliases
	 */
	default List<String> getAliases() {
		return List.of();
	}

	/**
	 * Returns the category interface of the command
	 * @return The category interface
	 */
	default CategoryInterface getCategory() {
		return null;
	}
}
