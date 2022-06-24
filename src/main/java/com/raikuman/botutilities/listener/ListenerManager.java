package com.raikuman.botutilities.listener;

import com.raikuman.botutilities.buttons.ButtonEventListener;
import com.raikuman.botutilities.buttons.manager.ButtonInterface;
import com.raikuman.botutilities.commands.CommandEventListener;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A manager to provide the listeners to the JDA object
 *
 * @version 1.1 2022-23-06
 * @since 1.0
 */
public class ListenerManager {

	private final List<ListenerAdapter> listeners;
	private final List<CommandInterface> commands;
	private final List<ButtonInterface> buttons;

	ListenerManager(List<ListenerAdapter> listeners, List<CommandInterface> commands, List<ButtonInterface> buttons) {
		this.listeners = listeners;
		this.commands = commands;
		this.buttons = buttons;
	}

	/**
	 * Returns an object array of the given listeners
	 * @return The array of listeners
	 */
	public Object[] getListeners() {
		List<Object> listenerList = new ArrayList<>();
		listenerList.add(getCommandListener());
		listenerList.add(getButtonListener());
		listenerList.addAll(listeners);


		return listenerList.toArray();
	}

	/**
	 * Returns a CommandEventListener given a list of commands
	 * @return The command event listener
	 */
	private CommandEventListener getCommandListener() {
		return new CommandEventListener(commands);
	}

	/**
	 * Returns a ButtonEventListener given a list of buttons
	 * @return The button event listener
	 */
	private ButtonEventListener getButtonListener() {
		return new ButtonEventListener(buttons);
	}
}
