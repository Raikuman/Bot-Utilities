package com.raikuman.botutilities.slashcommands.manager;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages handling message events to invoke slashes. Slash commands are added and checked if there are
 * multiple slashes with the same invoke being added.
 *
 * @version 1.0 2022-15-07
 * @since 1.2
 */
public class SlashManager {

	private static final Logger logger = LoggerFactory.getLogger(SlashManager.class);
	private final List<SlashInterface> slashes = new ArrayList<>();

	public SlashManager(List<SlashInterface> slashes) {
		addSlashCommands(slashes);
	}

	/**
	 * Gets the list of slash interfaces
	 * @return The list of slash interfaces
	 */
	public List<SlashInterface> getSlashCommands() {
		return slashes;
	}

	/**
	 * Adds a slash interface to the slash list
	 * @param slash The slash to add to the slash list
	 */
	private void addSlashCommand(SlashInterface slash) {
		boolean slashFound = slashes.stream().anyMatch(
			found -> found.getInvoke().equalsIgnoreCase(slash.getInvoke())
		);

		if (slashFound) {
			logger.error("A slash command with this invoke already exists: " + slash.getInvoke());
			return;
		}

		slashes.add(slash);
	}

	/**
	 * Adds multiple slash interfaces to the slash list
	 * @param slashes The list of slash to add to the slash list
	 */
	private void addSlashCommands(List<SlashInterface> slashes) {
		for (SlashInterface slash : slashes)
			addSlashCommand(slash);
	}

	/**
	 * Gets a slash command from the slash list using a string to search for the invocation of the slash
	 * @param search The invocation string to search for a slash
	 * @return The found slash, else null
	 */
	public SlashInterface getSlashCommand(String search) {
		for (SlashInterface slash : slashes)
			if (slash.getInvoke().equalsIgnoreCase(search))
				return slash;

		return null;
	}

	/**
	 * Handles the slash interaction event and checks if the event contains the prefix for the slash manager.
	 * It will then check if the slash exists and send a slash context to handle the slash
	 * @param event The event to check for prefix and slash
	 */
	public void handleEvent(SlashCommandInteractionEvent event) {
		SlashInterface slash = getSlashCommand(event.getName());

		if (slash != null)
			slash.handle(new SlashContext(event));
	}

	/**
	 * Creates a list of CommandData objects using SlashInterfaces
	 * @return The list of CommandData objects
	 */
	public List<CommandData> getSlashCommandData() {
		List<CommandData> commandData = new ArrayList<>();
		slashes.forEach(slash -> commandData.add(slash.getCommandData()));

		return commandData;
	}
}
