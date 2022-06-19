package com.raikuman.botutilities.listener;

import com.raikuman.botutilities.buttons.manager.ButtonInterface;
import com.raikuman.botutilities.commands.manager.CommandInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * A listener manager builder to provide the listener manager to add to the JDA object
 *
 * @version 1.0 2022-19-06
 * @since 1.0
 */
public class ListenerBuilder {

	private List<CommandInterface> commands;
	private List<ButtonInterface> buttons;

	public ListenerBuilder() {
		commands = new ArrayList<>();
		buttons = new ArrayList<>();
	}

	/**
	 * Sets the command interface list to a list of commands
	 * @param commands The command interface list to set commands
	 * @return The listener builder object
	 */
	public ListenerBuilder setCommands(List<CommandInterface> commands) {
		this.commands = commands;
		return this;
	}

	/**
	 * Sets the button interface list to a list of buttons
	 * @param buttons The button interface list to set buttons
	 * @return The listener builder object
	 */
	public ListenerBuilder setButtons(List<ButtonInterface> buttons) {
		this.buttons = buttons;
		return this;
	}

	/**
	 * Builds the listener manager from the builder
	 * @return The listener manager object
	 */
	public ListenerManager build() {
		return new ListenerManager(commands, buttons);
	}
}
