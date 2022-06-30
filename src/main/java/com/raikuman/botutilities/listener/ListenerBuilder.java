package com.raikuman.botutilities.listener;

import com.raikuman.botutilities.buttons.manager.ButtonInterface;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.selectmenus.manager.SelectInterface;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A listener manager builder to provide the listener manager to add to the JDA object
 *
 * @version 1.1 2022-23-06
 * @since 1.0
 */
public class ListenerBuilder {

	private List<ListenerAdapter> listeners;
	private List<CommandInterface> commands;
	private List<ButtonInterface> buttons;
	private List<SelectInterface> selects;

	public ListenerBuilder() {
		commands = new ArrayList<>();
		buttons = new ArrayList<>();
		listeners = new ArrayList<>();
		selects = new ArrayList<>();
	}

	/**
	 * Sets the listener adapter list to a list of listener adapters
	 * @param listeners The listener adapter list
	 * @return The listener builder object
	 */
	public ListenerBuilder setListeners(List<ListenerAdapter> listeners) {
		this.listeners = listeners;
		return this;
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

	public ListenerBuilder setSelects(List<SelectInterface> selects) {
		this.selects = selects;
		return this;
	}

	/**
	 * Builds the listener manager from the builder
	 * @return The listener manager object
	 */
	public ListenerManager build() {
		return new ListenerManager(
			listeners,
			commands,
			buttons,
			selects
		);
	}
}
