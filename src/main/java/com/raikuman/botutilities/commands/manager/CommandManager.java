package com.raikuman.botutilities.commands.manager;

import com.raikuman.botutilities.configs.ConfigIO;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Manages handling message events to invoke commands. Commands are added and checked if there are multiple
 * commands with the same invoke being added.
 *
 * @version 1.0 2022-18-06
 * @since 1.0
 */
public class CommandManager {

	private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);
	private final List<CommandInterface> commands = new ArrayList<>();

	public CommandManager(List<CommandInterface> commands) {
		addCommands(commands);
	}

	/**
	 * Gets the list of command interfaces
	 * @return The list of command interfaces
	 */
	public List<CommandInterface> getCommands() {
		return commands;
	}

	/**
	 * Adds a command interface to the command list
	 * @param command The command to add to the command list
	 */
	private void addCommand(CommandInterface command) {
		boolean commandFound = commands.stream().anyMatch(
			found -> found.getInvoke().equalsIgnoreCase(command.getInvoke())
		);

		if (commandFound) {
			logger.error("A command with this invoke already exists: " + command.getInvoke());
			return;
		}

		commands.add(command);
	}

	/**
	 * Adds multiple command interfaces to the command list
	 * @param commands The list of commands to add to the command list
	 */
	private void addCommands(List<CommandInterface> commands) {
		for (CommandInterface command : commands)
			addCommand(command);
	}

	/**
	 * Gets a command from the command list using a string to search for the invocation of the command
	 * @param search The invocation string to search for a command
	 * @return The found command, else null
	 */
	public CommandInterface getCommand(String search) {
		for (CommandInterface command : commands)
			if (command.getInvoke().equalsIgnoreCase(search) || command.getAliases().contains(search))
				return command;

		return null;
	}

	/**
	 * Handles the message event and checks if the event contains the prefix for the command manager. It
	 * will then check if the command exists and send a command context to handle the command
	 * @param event The event to check for prefix and command
	 */
	public void handleEvent(MessageReceivedEvent event) {
		String prefix = ConfigIO.readConfig("settings", "prefix");
		if (prefix == null) {
			logger.error("Could not retrieve prefix");
			return;
		}

		String[] split = event.getMessage().getContentRaw()
			.replaceFirst(
				"(?i)" + Pattern.quote(prefix),
				"")
			.split("\\s+");

		CommandInterface command = getCommand(split[0].toLowerCase());
		if (command == null) {
			logger.info("Could not retrieve command from command manager: " + split[0].toLowerCase());
			return;
		}

		List<String> args = Arrays.asList(split).subList(1, split.length);

		command.handle(new CommandContext(event, args));
	}
}
