package com.raikuman.botutilities.listener;

import com.raikuman.botutilities.buttons.ButtonEventListener;
import com.raikuman.botutilities.buttons.manager.ButtonInterface;
import com.raikuman.botutilities.commands.CommandEventListener;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.selectmenus.SelectEventListener;
import com.raikuman.botutilities.selectmenus.manager.SelectInterface;
import com.raikuman.botutilities.slashcommands.SlashEventListener;
import com.raikuman.botutilities.slashcommands.manager.SlashInterface;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A manager to provide the listeners to the JDA object
 *
 * @version 1.2 2022-15-07
 * @since 1.0
 */
public class ListenerManager {

	private final List<ListenerAdapter> listeners;
	private final List<CommandInterface> commands;
	private final List<ButtonInterface> buttons;
	private final List<SelectInterface> selects;
	private final List<SlashInterface> slashes;

	ListenerManager(List<ListenerAdapter> listeners, List<CommandInterface> commands,
		List<ButtonInterface> buttons, List<SelectInterface> selects, List<SlashInterface> slashes) {
		this.listeners = listeners;
		this.commands = commands;
		this.buttons = buttons;
		this.selects = selects;
		this.slashes = slashes;
	}

	/**
	 * Returns an object array of the given listeners
	 * @return The array of listeners
	 */
	public Object[] getListeners() {
		List<Object> listenerList = new ArrayList<>();
		listenerList.add(getCommandListener());
		listenerList.add(getButtonListener());
		listenerList.add(getSelectListener());
		listenerList.add(getSlashListener());
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

	/**
	 * Returns a SelectEventListener given a list of selects
	 * @return The select event listener
	 */
	private SelectEventListener getSelectListener() {
		return new SelectEventListener(selects);
	}

	/**
	 * Returns a SlashEventListener given a list of slashes
	 * @return The slash event listener
	 */
	private SlashEventListener getSlashListener() {
		return new SlashEventListener(slashes);
	}
}
