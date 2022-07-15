package com.raikuman.botutilities.listener;

import com.raikuman.botutilities.buttons.manager.ButtonInterface;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.selectmenus.manager.SelectInterface;
import com.raikuman.botutilities.slashcommands.manager.SlashInterface;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A listener manager builder to provide the listener manager to add to the JDA object
 *
 * @version 1.2 2022-15-07
 * @since 1.0
 */
public class ListenerBuilder {

	private List<ListenerAdapter> listeners;
	private List<CommandInterface> commands;
	private List<ButtonInterface> buttons;
	private List<SelectInterface> selects;
	private List<SlashInterface> slashes;

	public ListenerBuilder() {
		commands = new ArrayList<>();
		buttons = new ArrayList<>();
		listeners = new ArrayList<>();
		selects = new ArrayList<>();
		slashes = new ArrayList<>();
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

	/**
	 * Sets the select interface list to a list of selects
	 * @param selects The select interface list to set selects
	 * @return The listener builder object
	 */
	public ListenerBuilder setSelects(List<SelectInterface> selects) {
		this.selects = selects;
		return this;
	}

	/**
	 * Sets the slash interface list to a list of slashes
	 * @param slashes The slash interface list to set slashes
	 * @return The listener builder object
	 */
	public ListenerBuilder setSlashes(List<SlashInterface> slashes) {
		this.slashes = slashes;
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
			selects,
			slashes
		);
	}
}
